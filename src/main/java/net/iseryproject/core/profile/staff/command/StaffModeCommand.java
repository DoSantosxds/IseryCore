package net.iseryproject.core.profile.staff.command;

import com.qrakn.honcho.command.CommandMeta;

import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;

@CommandMeta(label = { "staffmode", "sm" }, permission = "core.staff")
public class StaffModeCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getStaffOptions().staffModeEnabled(!profile.getStaffOptions().staffModeEnabled());

        player.sendMessage(profile.getStaffOptions().staffModeEnabled() ?
                CC.GREEN + "You are now in staff mode." : CC.RED + "You are no longer in staff mode.");

    }

}
