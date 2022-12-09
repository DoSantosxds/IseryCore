package net.iseryproject.core.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;

import net.iseryproject.core.Core;
import net.iseryproject.core.CoreAPI;
import net.iseryproject.core.profile.conversation.ProfileConversations;
import net.iseryproject.core.profile.grant.Grant;
import net.iseryproject.core.profile.grant.event.GrantAppliedEvent;
import net.iseryproject.core.profile.grant.event.GrantExpireEvent;
import net.iseryproject.core.profile.option.ProfileOptions;
import net.iseryproject.core.profile.punishment.Punishment;
import net.iseryproject.core.profile.punishment.PunishmentType;
import net.iseryproject.core.profile.staff.ProfileStaffOptions;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.Cooldown;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class Profile {

	@Getter private static Map<UUID, Profile> profiles = new HashMap<>();
	private static MongoCollection<Document> collection = Core.get().getMongoDatabase().getCollection("profiles");
	private static MongoCollection<Document> disguise = Core.get().getMongoDatabase().getCollection("disguise");

	@Getter private final UUID uuid;
	@Getter @Setter private String name;
	@Getter @Setter private Long firstSeen;
	@Getter @Setter private Long lastSeen;
	@Getter @Setter private String currentAddress;
	@Getter private List<String> ipAddresses;
	@Getter private final List<UUID> knownAlts;
	@Getter private final ProfileOptions options;
	@Getter private final ProfileStaffOptions staffOptions;
	@Getter private final ProfileConversations conversations;
	@Getter private Grant activeGrant;
	//Disguise
	@Setter @Getter private String disguisedName;
	@Setter @Getter private Rank disguisedRank;
	//End Disguise
	@Getter private final List<Grant> grants;
	@Getter private final List<Punishment> punishments;
	@Getter @Setter private boolean loaded;
	@Getter @Setter private Cooldown chatCooldown;
	@Getter @Setter private Cooldown requestCooldown;

	@Getter @Setter private Map<String, String> serversTag;

	@Getter private final List<String> permissions = new ArrayList<>();

	public Profile(String username, UUID uuid) {
		this.uuid = uuid;
		this.name = username;
		this.grants = new ArrayList<>();
		this.punishments = new ArrayList<>();
		this.ipAddresses = new ArrayList<>();
		this.knownAlts = new ArrayList<>();
		this.options = new ProfileOptions();
		this.serversTag = new HashMap<>();
		this.staffOptions = new ProfileStaffOptions();
		this.conversations = new ProfileConversations(this);
		this.chatCooldown = new Cooldown(0);
		this.requestCooldown = new Cooldown(0);

		load();
	}



	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public String getColoredUsername() {
		return getDisplayRank().getColor() + getDisplayName();
	}

	public Punishment getActivePunishmentByType(PunishmentType type) {
		for (Punishment punishment : punishments) {
			if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired()) {
				return punishment;
			}
		}

		return null;
	}

	public int getPunishmentCountByType(PunishmentType type) {
		int i = 0;

		for (Punishment punishment : punishments) {
			if (punishment.getType() == type) i++;
		}

		return i;
	}

	public Rank getActiveRank() {
		return activeGrant.getRank();
	}

	public Rank getDisplayRank() {
		return disguisedRank != null ? disguisedRank : getActiveRank();
	}

	public String getDisplayName() {
		return disguisedName != null ? disguisedName : name;
	}

	/**
	 * Finds and applies the next best grant.
	 * The next chosen grant is determined by comparing descending rank weights.
	 */
	public void activateNextGrant() {
		List<Grant> grants = new ArrayList<>(this.grants);

		grants.sort(Comparator.comparingInt(grant -> grant.getRank().getWeight()));
		Collections.reverse(grants);

		for (Grant grant : grants) {
			if (!grant.isRemoved() && !grant.hasExpired()) {
				if (!grant.equals(activeGrant)) {
					activeGrant = grant;
					setupBukkitPlayer(getPlayer());
					return;
				}
			}
		}
	}

	/**
	 * Checks for and updates any grants that have pending changes.
	 */
	public void checkGrants() {
		Player player = getPlayer();

		// Update grants that are expired and not removed yet
		for (Grant grant : grants) {
			if (!grant.isRemoved() && grant.hasExpired()) {
				grant.setRemovedAt(System.currentTimeMillis());
				grant.setRemovedReason("Grant expired");
				grant.setRemoved(true);

				if (player != null) {
					new GrantExpireEvent(player, grant).call();
				}
			}
		}

		// Active next available grant if active grant is now removed
		if (activeGrant != null && activeGrant.isRemoved()) {
			activateNextGrant();
		}

		// Generate a default grant if there is no active grant
		if (activeGrant == null) {
			Grant defaultGrant = new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null,
					System.currentTimeMillis(), "Default", Integer.MAX_VALUE);

			grants.add(defaultGrant);
			activeGrant = defaultGrant;

			if (player != null) {
				setupBukkitPlayer(getPlayer());
				new GrantAppliedEvent(player, defaultGrant).call();
			}
		}
	}

	public void setupBukkitPlayer(Player player) {
		if (player == null) {
			return;
		}

		// Clear any permissions set for this player by this plugin
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			if (attachmentInfo.getAttachment() == null || attachmentInfo.getAttachment().getPlugin() == null ||
			    !attachmentInfo.getAttachment().getPlugin().equals(Core.get())) {
				continue;
			}

			attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> {
				attachmentInfo.getAttachment().unsetPermission(permission);
			});
		}

		PermissionAttachment attachment = player.addAttachment(Core.get());

		for (String permission : activeGrant.getRank().getAllPermissions()) {
			attachment.setPermission(permission, true);
		}

		for (String permission : permissions) {
			attachment.setPermission(permission, true);
		}

		player.recalculatePermissions();

		String displayName = getDisplayRank().getPrefix() + player.getName();
		String coloredName = CoreAPI.getColoredName(player);

		if (coloredName.length() > 16) {
			coloredName = coloredName.substring(0, 15);
		}

		player.setDisplayName(displayName);

		if (Core.get().getMainConfig().getBoolean("SETTINGS.UPDATE_PLAYER_LIST_NAME")) {
			player.setPlayerListName(coloredName);
		}
	}

	public void load() {
		Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

		if (document != null) {
			if (name == null) {
				name = document.getString("name");
			}

			firstSeen = document.getLong("firstSeen");
			lastSeen = document.getLong("lastSeen");
			currentAddress = document.getString("currentAddress");
			ipAddresses = Core.GSON.fromJson(document.getString("ipAddresses"), Core.LIST_STRING_TYPE);

			Document optionsDocument = (Document) document.get("options");
			options.publicChatEnabled(optionsDocument.getBoolean("publicChatEnabled"));
			options.receivingNewConversations(optionsDocument.getBoolean("receivingNewConversations"));
			options.playingMessageSounds(optionsDocument.getBoolean("playingMessageSounds"));

			JsonArray grantList = new JsonParser().parse(document.getString("grants")).getAsJsonArray();

			for (JsonElement grantData : grantList) {
				// Transform into a Grant object
				Grant grant = Grant.DESERIALIZER.deserialize(grantData.getAsJsonObject());

				if (grant != null) {
					this.grants.add(grant);
				}
			}

			JsonArray punishmentList = new JsonParser().parse(document.getString("punishments")).getAsJsonArray();

			for (JsonElement punishmentData : punishmentList) {
				// Transform into a Grant object
				Punishment punishment = Punishment.DESERIALIZER.deserialize(punishmentData.getAsJsonObject());

				if (punishment != null) {
					this.punishments.add(punishment);
				}
			}

			if(document.containsKey("permissions")){
				permissions.clear();
				permissions.addAll(Core.GSON.<List<String>>fromJson(document.getString("permissions"), Core.LIST_STRING_TYPE));
			}

		}

		// Update active grants
		activateNextGrant();
		checkGrants();

		// Set loaded to true
		loaded = true;
	}

	public void save() {
		Document document = new Document();
		document.put("name", name);
		document.put("uuid", uuid.toString());
		document.put("firstSeen", firstSeen);
		document.put("lastSeen", lastSeen);
		document.put("currentAddress", currentAddress);
		document.put("ipAddresses", Core.GSON.toJson(ipAddresses, Core.LIST_STRING_TYPE));

		Document optionsDocument = new Document();
		optionsDocument.put("publicChatEnabled", options.publicChatEnabled());
		optionsDocument.put("receivingNewConversations", options.receivingNewConversations());
		optionsDocument.put("playingMessageSounds", options.playingMessageSounds());
		document.put("options", optionsDocument);

		JsonArray grantList = new JsonArray();

		for (Grant grant : this.grants) {
			grantList.add(Grant.SERIALIZER.serialize(grant));
		}

		document.put("grants", grantList.toString());

		JsonArray punishmentList = new JsonArray();

		for (Punishment punishment : this.punishments) {
			punishmentList.add(Punishment.SERIALIZER.serialize(punishment));
		}

		document.put("punishments", punishmentList.toString());

		document.put("permissions", Core.GSON.toJson(permissions));

		BasicDBList objects = new BasicDBList();

		for (Map.Entry<String, String> entry : serversTag.entrySet()) {
			BasicDBObject object = new BasicDBObject();
			object.put("serverName", entry.getKey());
			object.put("tagName", entry.getValue());

			objects.add(object);
		}
		document.put("serversTag", objects);

		collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
	}

	public static Profile getByUuid(UUID uuid) {
		if (profiles.containsKey(uuid)) {
			return profiles.get(uuid);
		}

		return new Profile(null, uuid);
	}

	public static Profile getByUsername(String username) {
		Player player = Bukkit.getPlayer(username);

		if (player != null) {
			return profiles.get(player.getUniqueId());
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

		if (offlinePlayer.hasPlayedBefore()) {
			if (profiles.containsKey(offlinePlayer.getUniqueId())) {
				return profiles.get(offlinePlayer.getUniqueId());
			}

			return new Profile(offlinePlayer.getName(), offlinePlayer.getUniqueId());
		}

		UUID uuid = Core.get().getRedisCache().getUuid(username);

		if (uuid != null) {
			if (profiles.containsKey(uuid)) {
				return profiles.get(uuid);
			}

			return new Profile(username, uuid);
		}

		return null;
	}

	public static List<Profile> getByIpAddress(String ipAddress) {
		List<Profile> profiles = new ArrayList<>();
		Bson filter = Filters.eq("currentAddress", ipAddress);

		try (MongoCursor<Document> cursor = collection.find(filter).iterator()) {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				profiles.add(new Profile(document.getString("username"),
						UUID.fromString(document.getString("uuid"))));
			}
		}

		return profiles;
	}

}
