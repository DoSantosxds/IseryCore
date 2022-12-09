package net.iseryproject.core.profile.punishment.command;

import network.katana.core.Core;
import network.katana.core.Locale;
import network.katana.core.profile.Profile;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import net.evilblock.pidgin.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

@CommandMeta(label = "clearpunishments", permission = "core.admin.clearpunishments", async = true)
public class ClearPunishmentsCommand {

	public void execute(CommandSender sender, @CPL("player") Profile profile) {
		if (profile == null) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		profile.getPunishments().clear();
		profile.save();

		Map<String, Object> data = new HashMap<>();
		data.put("uuid", String.valueOf(profile.getUuid()));
		Core.get().getPidgin().sendMessage(new Message("PUNISHMENT_CLEAR", data));

		sender.sendMessage(ChatColor.GREEN + "Cleared punishments of " + profile.getColoredUsername() +
		                   ChatColor.GREEN + "!");

	}

}
