package net.iseryproject.core.rank.command;

import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "rank setprefix", permission = "core.admin.rank", async = true)
public class RankSetPrefixCommand {

	public void execute(CommandSender sender, Rank rank, String prefix) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		rank.setPrefix(CC.translate(prefix));
		rank.save();
		rank.refresh();

		sender.sendMessage(CC.GREEN + "You updated the rank's prefix.");
	}

}
