package net.iseryproject.core.profile.option.command;

import network.katana.core.profile.option.menu.ProfileOptionsMenu;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "options", "settings" })
public class OptionsCommand {

	public void execute(Player player) {
		new ProfileOptionsMenu().openMenu(player);
	}

}
