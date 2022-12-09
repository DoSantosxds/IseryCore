package net.iseryproject.core.rank.command;

import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "rank setweight", permission = "core.admin.rank", async = true)
public class RankSetWeightCommand {

	public void execute(CommandSender sender, Rank rank, String weight) {
		if (rank == null) {
			sender.sendMessage(CC.RED + "A rank with that name does not exist.");
			return;
		}

		try {
			Integer.parseInt(weight);
		} catch (Exception e) {
			sender.sendMessage(CC.RED + "Invalid number.");
			return;
		}

		rank.setWeight(Integer.parseInt(weight));
		rank.save();
		rank.refresh();

		sender.sendMessage(CC.GREEN + "You updated the rank's weight.");
	}

}
