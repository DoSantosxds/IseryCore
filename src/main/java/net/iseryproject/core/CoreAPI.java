package net.iseryproject.core;


import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.rank.Rank;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CoreAPI {

	public static ChatColor getColorOfPlayer(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile == null ? ChatColor.WHITE : profile.getDisplayRank().getColor();
	}

	public static void sendToStaff(final String message) {
		Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("core.staff")).forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
	}

	public static String getColoredName(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return (profile == null ? ChatColor.WHITE + player.getName() : profile.getDisplayRank().getColor() + profile.getDisplayName());
	}


	public static Rank getRankOfPlayer(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile == null ? Rank.getDefaultRank() : profile.getDisplayRank();
	}

	public static boolean isInStaffMode(Player player) {
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		return profile != null && player.hasPermission("core.staff") && profile.getStaffOptions().staffModeEnabled();
	}

}
