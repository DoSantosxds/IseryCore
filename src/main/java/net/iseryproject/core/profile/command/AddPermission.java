package net.iseryproject.core.profile.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;

import net.iseryproject.core.Locale;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"addperm", "addpermission"}, async = true, permission = "core.admin.profile")
public class AddPermission {

    public void execute(Player player, @CPL("player") Profile profile, String permission){
        if (profile == null) {
            player.sendMessage(Locale.PLAYER_NOT_FOUND.format());
            return;
        }

        permission = permission.toLowerCase().trim();

        if (profile.getPermissions().contains(permission)) {
            player.sendMessage(CC.RED + "That player has already that permission.");
            return;
        }

        profile.getPermissions().add(permission);
        profile.save();

        player.sendMessage(CC.GREEN + "Successfully added permission to the player.");
    }

}
