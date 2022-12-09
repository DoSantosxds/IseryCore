package net.iseryproject.core.profile.grant.command;


import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.Locale;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.grant.menu.GrantsListMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = "grants", async = true, permission = "core.grants.show")
public class GrantsCommand {

	public void execute(Player player, @CPL("player") Profile profile) {
		if (profile == null || !profile.isLoaded()) {
			player.sendMessage(Locale.COULD_NOT_RESOLVE_PLAYER.format());
			return;
		}

		new GrantsListMenu(profile).openMenu(player);
	}

}
