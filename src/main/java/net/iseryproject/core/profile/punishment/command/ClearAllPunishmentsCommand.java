package net.iseryproject.core.profile.punishment.command;

import com.qrakn.honcho.command.CommandMeta;
import network.katana.core.profile.Profile;
import network.katana.core.profile.punishment.Punishment;
import network.katana.core.profile.punishment.PunishmentType;
import network.katana.core.util.CC;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@CommandMeta(label = "clearpunishments all", permission = "core.admin.clearallpunishments", async = true)
public class ClearAllPunishmentsCommand {

	public void execute(CommandSender sender) {
		AtomicInteger i = new AtomicInteger();

		Bukkit.broadcastMessage(CC.translate("&6[PardonWave] &eEvent has started"));

		Arrays.stream(Bukkit.getOfflinePlayers()).forEach(player -> {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			Punishment punishment = profile.getActivePunishmentByType(PunishmentType.BAN);

			if (punishment != null) {
				punishment.setRemovedAt(System.currentTimeMillis());
				punishment.setRemovedBy(null);
				punishment.setRemovedReason("Unban All");
				punishment.setRemoved(true);

				profile.save();
				Bukkit.broadcastMessage(CC.translate("&6[PardonWave] " + profile.getActiveRank().getColor() + player.getName() + " &6unbanned. &7(Number #" + i.incrementAndGet() + ')'));
			}
		});

		Bukkit.broadcastMessage(CC.translate("&6[PardonWave] &d&l" + i.get() + " &eplayer" + (i.get() == 1 ? "" : "s") + ' ' + (i.get() == 1 ? "was" : "were") + " pardoned from Katana Network."));

	}

}
