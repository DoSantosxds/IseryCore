package net.iseryproject.core.profile.punishment.command;

import network.katana.core.Locale;
import network.katana.core.Core;
import network.katana.core.profile.Profile;
import network.katana.core.profile.punishment.Punishment;
import network.katana.core.profile.punishment.PunishmentType;
import network.katana.core.util.CC;
import network.katana.core.util.duration.Duration;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import com.qrakn.honcho.command.CommandOption;
import net.evilblock.pidgin.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandMeta(label = "mute", permission = "core.staff.mute", async = true, options = "s")
public class MuteCommand {

	public void execute(CommandSender sender, CommandOption option, @CPL("player") Profile profile, Duration duration, String reason) {
		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		if (profile.getActivePunishmentByType(PunishmentType.MUTE) != null) {
			sender.sendMessage(CC.RED + "That player is already muted.");
			return;
		}

		if (duration.getValue() == -1) {
			sender.sendMessage(CC.RED + "That duration is not valid.");
			sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
			return;
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.MUTE, System.currentTimeMillis(),
				reason, duration.getValue());

		if (sender instanceof Player) {
			punishment.setAddedBy(((Player) sender).getUniqueId());
		}

		profile.getPunishments().add(punishment);
		profile.save();

		Player player = profile.getPlayer();

		if (player != null) {
			String senderName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender).getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";
			player.sendMessage(CC.RED + "You have been " + punishment.getContext() + " by " +
					senderName + CC.RED + ".");
			player.sendMessage(CC.RED + "The reason for this punishment: " + CC.WHITE +
					punishment.getAddedReason());

			if (!punishment.isPermanent()) {
				player.sendMessage(CC.RED + "This mute will expire in " + CC.WHITE +
						punishment.getTimeRemaining() + CC.RED + ".");
			}
		}

		Map<String, Object> data = new HashMap<>();
		data.put("punishment", Punishment.SERIALIZER.serialize(punishment));
		data.put("staff", staffName);
		data.put("target", profile.getColoredUsername());
		data.put("targetUUID", String.valueOf(profile.getUuid()));
		data.put("silent", String.valueOf(option != null));
		Core.get().getPidgin().sendMessage(new Message("PUNISHMENT", data));
	}

}
