package net.iseryproject.core.chat.command;


import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.Core;
import net.iseryproject.core.CoreAPI;
import net.iseryproject.core.Locale;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(label = "slowchat", permission = "core.staff.slowchat")
public class SlowChatCommand {

	public void execute(CommandSender sender) {
		Core.get().getChat().togglePublicChatDelay();

		String senderName;

		if (sender instanceof Player) {
			senderName = CoreAPI.getColoredName((Player) sender);
		} else {
			senderName = ChatColor.DARK_RED + "Console";
		}

		String context = Core.get().getChat().getDelayTime() == 1 ? "" : "s";

		if (Core.get().getChat().isPublicChatDelayed()) {
			Bukkit.broadcastMessage(Locale.DELAY_CHAT_ENABLED_BROADCAST.format(senderName,
					Core.get().getChat().getDelayTime(), context));
		} else {
			Bukkit.broadcastMessage(Locale.DELAY_CHAT_DISABLED_BROADCAST.format(senderName));
		}
	}

	public void execute(CommandSender sender, Integer seconds) {
		if (seconds < 0 || seconds > 60) {
			sender.sendMessage(ChatColor.RED + "A delay can only be between 1-60 seconds.");
			return;
		}

		String context = seconds == 1 ? "" : "s";

		sender.sendMessage(ChatColor.GREEN + "You have updated the chat delay to " + seconds + " second" + context + ".");
		Core.get().getChat().setDelayTime(seconds);
	}

}
