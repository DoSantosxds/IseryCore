package net.iseryproject.core.profile.staff.command;


import com.qrakn.honcho.command.CommandMeta;
import net.evilblock.pidgin.message.Message;
import net.iseryproject.core.Core;
import net.iseryproject.core.CoreAPI;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandMeta(label = { "staffchat", "sc" }, permission = "core.staff")
public class StaffChatCommand {

	public void execute(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		profile.getStaffOptions().staffChatModeEnabled(!profile.getStaffOptions().staffChatModeEnabled());

		player.sendMessage(profile.getStaffOptions().staffChatModeEnabled() ?
				CC.GREEN + "You are now talking in staff chat." : CC.RED + "You are no longer talking in staff chat.");
	}

	public void execute(Player player, String message) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());

		/*if (!profile.getStaffOptions().staffModeEnabled()) {
			player.sendMessage(CC.RED + "You are not in staff mode.");
			return;
		}*/

		Map<String, Object> data = new HashMap<>();
		data.put("serverName", Bukkit.getServerId());
		data.put("playerName", CoreAPI.getColoredName(player));
		data.put("chatMessage", message);
		Core.get().getPidgin().sendMessage(new Message("STAFF_CHAT", data));
	}

}
