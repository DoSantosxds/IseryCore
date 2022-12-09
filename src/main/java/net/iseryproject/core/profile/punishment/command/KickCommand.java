package net.iseryproject.core.profile.punishment.command;

import network.katana.core.Locale;
import network.katana.core.Core;
import network.katana.core.profile.Profile;
import network.katana.core.profile.punishment.Punishment;
import network.katana.core.profile.punishment.PunishmentType;
import network.katana.core.util.CC;
import com.qrakn.honcho.command.CommandMeta;
import com.qrakn.honcho.command.CommandOption;
import net.evilblock.pidgin.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandMeta(label = "kick", permission = "core.staff.kick", async = true, options = "s")
public class KickCommand {

	public void execute(CommandSender sender, CommandOption option, Player player, String reason) {
		if (player == null) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		Profile profile = Profile.getProfiles().get(player.getUniqueId());

		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.KICK, System.currentTimeMillis(),
				reason, -1);

		if (sender instanceof Player) {
			punishment.setAddedBy(((Player) sender).getUniqueId());
		}

		profile.getPunishments().add(punishment);
		profile.save();

		Map<String, Object> data = new HashMap<>();
		data.put("punishment", Punishment.SERIALIZER.serialize(punishment));
		data.put("staff", staffName);
		data.put("target", profile.getColoredUsername());
		data.put("targetUUID", String.valueOf(profile.getUuid()));
		data.put("silent", String.valueOf(option != null));
		Core.get().getPidgin().sendMessage(new Message("PUNISHMENT", data));

		new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer(punishment.getKickMessage());
			}
		}.runTask(Core.get());
	}

}
