package net.iseryproject.core.profile.punishment.listener;

import network.katana.core.profile.punishment.procedure.PunishmentProcedure;
import network.katana.core.profile.punishment.procedure.PunishmentProcedureStage;
import network.katana.core.profile.punishment.procedure.PunishmentProcedureType;
import network.katana.core.util.CC;
import network.katana.core.util.callback.TypeCallback;
import network.katana.core.util.menu.menus.ConfirmMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PunishmentListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().hasPermission("core.staff.grant")) {
			return;
		}

		PunishmentProcedure procedure = PunishmentProcedure.getByPlayer(event.getPlayer());

		if (procedure != null && procedure.getStage() == PunishmentProcedureStage.REQUIRE_TEXT) {
			event.setCancelled(true);

			if (event.getMessage().equalsIgnoreCase("cancel")) {
				PunishmentProcedure.getProcedures().remove(procedure);
				event.getPlayer().sendMessage(CC.RED + "You have cancelled the punishment procedure.");
				return;
			}

			if (procedure.getType() == PunishmentProcedureType.PARDON) {
				new ConfirmMenu(CC.YELLOW + "Pardon this punishment?", new TypeCallback<Boolean>() {
					@Override
					public void callback(Boolean data) {
						if (data) {
							procedure.getPunishment().setRemovedBy(event.getPlayer().getUniqueId());
							procedure.getPunishment().setRemovedAt(System.currentTimeMillis());
							procedure.getPunishment().setRemovedReason(event.getMessage());
							procedure.getPunishment().setRemoved(true);
							procedure.finish();

							event.getPlayer().sendMessage(CC.GREEN + "The punishment has been removed.");
						} else {
							procedure.cancel();
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
						}
					}
				}, true) {
					@Override
					public void onClose(Player player) {
						if (!isClosedByMenu()) {
							procedure.cancel();
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
						}
					}
				}.openMenu(event.getPlayer());
			}
		}
	}

}
