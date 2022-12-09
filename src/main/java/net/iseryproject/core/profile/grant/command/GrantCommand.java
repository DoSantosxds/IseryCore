package net.iseryproject.core.profile.grant.command;


import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import net.evilblock.pidgin.message.Message;
import net.iseryproject.core.Core;
import net.iseryproject.core.Locale;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.grant.Grant;
import net.iseryproject.core.profile.grant.event.GrantAppliedEvent;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.CC;
import net.iseryproject.core.util.TimeUtil;
import net.iseryproject.core.util.duration.Duration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandMeta(label = "grant", async = true, permission = "core.grants.add")
public class GrantCommand {

	public void execute(CommandSender sender, @CPL("player") Profile profile, Rank rank, Duration duration, String reason) {
		if (rank == null) {
			sender.sendMessage(Locale.RANK_NOT_FOUND.format());
			return;
		}

		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		if (duration.getValue() == -1) {
			sender.sendMessage(CC.RED + "That duration is not valid.");
			sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
			return;
		}

		UUID addedBy = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
		Grant grant = new Grant(UUID.randomUUID(), rank, addedBy, System.currentTimeMillis(), reason,
				duration.getValue());

		profile.getGrants().add(grant);
		profile.save();
		profile.activateNextGrant();

		Map<String, Object> data = new HashMap<>();
		data.put("playerUUID", String.valueOf(profile.getUuid()));
		data.put("grant", Grant.SERIALIZER.serialize(grant));
		Core.get().getPidgin().sendMessage(new Message("ADD_GRANT", data));

		sender.sendMessage(CC.GREEN + "You applied a `{rank}` grant to `{player}` for {time-remaining}."
				.replace("{rank}", rank.getDisplayName())
				.replace("{player}", profile.getName())
				.replace("{time-remaining}", duration.getValue() == Integer.MAX_VALUE ? "forever"
						: TimeUtil.millisToRoundedTime(duration.getValue())));

		Player player = profile.getPlayer();

		if (player != null) {
			new GrantAppliedEvent(player, grant).call();
		}
	}

}
