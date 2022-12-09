package net.iseryproject.core.profile.punishment.command;

import network.katana.core.Locale;
import network.katana.core.Core;
import network.katana.core.profile.Profile;
import network.katana.core.profile.punishment.Punishment;
import network.katana.core.profile.punishment.PunishmentType;
import network.katana.core.util.CC;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import com.qrakn.honcho.command.CommandOption;
import net.evilblock.pidgin.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandMeta(label = "unmute", permission = "core.staff.unmute", async = true, options = "s")
public class UnmuteCommand {

	public void execute(CommandSender sender, CommandOption option, @CPL("player") Profile profile, String reason) {
		if (profile == null || !profile.isLoaded()) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		if (profile.getActivePunishmentByType(PunishmentType.MUTE) == null) {
			sender.sendMessage(CC.RED + "That player is not muted.");
			return;
		}

		String staffName = sender instanceof Player ? Profile.getProfiles().get(((Player) sender)
				.getUniqueId()).getColoredUsername() : CC.DARK_RED + "Console";

		Punishment punishment = profile.getActivePunishmentByType(PunishmentType.MUTE);
		punishment.setRemovedAt(System.currentTimeMillis());
		punishment.setRemovedReason(reason);
		punishment.setRemoved(true);

		if (sender instanceof Player) {
			punishment.setRemovedBy(((Player) sender).getUniqueId());
		}

		profile.save();

		Map<String, Object> data = new HashMap<>();
		data.put("punishment", Punishment.SERIALIZER.serialize(punishment));
		data.put("staff", staffName);
		data.put("target", profile.getColoredUsername());
		data.put("targetUUID", String.valueOf(profile.getUuid()));
		data.put("silent", String.valueOf(option != null));
		Core.get().getPidgin().sendMessage(new Message("PUNISHMENT", data));
	}

}
