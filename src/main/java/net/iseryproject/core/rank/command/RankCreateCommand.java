package net.iseryproject.core.rank.command;


import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "rank create", permission = "core.admin.rank", async = true)
public class RankCreateCommand {

	public void execute(CommandSender sender, String name) {
		if (Rank.getRankByDisplayName(name) != null) {
			sender.sendMessage(CC.RED + "A rank with that name already exists.");
			return;
		}

		Rank rank = new Rank(name);
		rank.save();

		sender.sendMessage(CC.GREEN + "You created a new rank.");
	}

}
