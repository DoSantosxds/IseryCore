package net.iseryproject.core.profile.grant.command;


import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import net.evilblock.pidgin.message.Message;
import net.iseryproject.core.Core;
import net.iseryproject.core.Locale;
import net.iseryproject.core.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

@CommandMeta(label = "cleargrants", permission = "core.admin.cleargrants", async = true)
public class ClearGrantsCommand {

	public void execute(CommandSender sender, @CPL("player") Profile profile) {
		if (profile == null) {
			sender.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		profile.getGrants().clear();
		profile.save();

		Map<String, Object> data = new HashMap<>();
		data.put("uuid", String.valueOf(profile.getUuid()));
		Core.get().getPidgin().sendMessage(new Message("GRANT_CLEAR", data));

		sender.sendMessage(ChatColor.GREEN + "Cleared grants of " + profile.getName() + "!");
	}

}
