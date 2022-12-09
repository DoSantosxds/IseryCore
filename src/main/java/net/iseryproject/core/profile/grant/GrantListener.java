package net.iseryproject.core.profile.grant;


import net.evilblock.pidgin.message.Message;
import net.iseryproject.core.Core;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.grant.event.GrantAppliedEvent;
import net.iseryproject.core.profile.grant.event.GrantExpireEvent;
import net.iseryproject.core.profile.grant.procedure.GrantProcedure;
import net.iseryproject.core.profile.grant.procedure.GrantProcedureStage;
import net.iseryproject.core.profile.grant.procedure.GrantProcedureType;
import net.iseryproject.core.util.CC;
import net.iseryproject.core.util.TimeUtil;
import net.iseryproject.core.util.callback.TypeCallback;
import net.iseryproject.core.util.menu.menus.ConfirmMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class GrantListener implements Listener {

	@EventHandler
	public void onGrantAppliedEvent(GrantAppliedEvent event) {
		Player player = event.getPlayer();
		Grant grant = event.getGrant();

		player.sendMessage(CC.GREEN + ("A `{rank}` grant has been applied to you for {time-remaining}.")
				.replace("{rank}", grant.getRank().getDisplayName())
				.replace("{time-remaining}", grant.getDuration() == Integer.MAX_VALUE ?
						"forever" : TimeUtil.millisToRoundedTime((grant.getAddedAt() + grant.getDuration()) -
						                                         System.currentTimeMillis())));

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setupBukkitPlayer(player);
	}

	@EventHandler
	public void onGrantExpireEvent(GrantExpireEvent event) {
		Player player = event.getPlayer();
		Grant grant = event.getGrant();

		player.sendMessage(CC.RED + ("Your `{rank}` grant has expired.")
				.replace("{rank}", grant.getRank().getDisplayName()));

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setupBukkitPlayer(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().hasPermission("core.staff.grant")) {
			return;
		}

		GrantProcedure procedure = GrantProcedure.getByPlayer(event.getPlayer());

		if (procedure != null && procedure.getStage() == GrantProcedureStage.REQUIRE_TEXT) {
			event.setCancelled(true);

			if (event.getMessage().equalsIgnoreCase("cancel")) {
				GrantProcedure.getProcedures().remove(procedure);
				event.getPlayer().sendMessage(CC.RED + "You have cancelled the grant procedure.");
				return;
			}

			if (procedure.getType() == GrantProcedureType.REMOVE) {
				new ConfirmMenu(CC.YELLOW + "Delete this grant?", new TypeCallback<Boolean>() {
					@Override
					public void callback(Boolean data) {
						if (data) {
							procedure.getGrant().setRemovedBy(event.getPlayer().getUniqueId());
							procedure.getGrant().setRemovedAt(System.currentTimeMillis());
							procedure.getGrant().setRemovedReason(event.getMessage());
							procedure.getGrant().setRemoved(true);
							procedure.finish();
							event.getPlayer().sendMessage(CC.GREEN + "The grant has been removed.");

							Map<String, Object> data2 = new HashMap<>();
							data2.put("playerUUID", procedure.getRecipient().getUuid());
							data2.put("grant", Grant.SERIALIZER.serialize(procedure.getGrant()));
							Core.get().getPidgin().sendMessage(new Message("DELETE_GRANT", data2));
						} else {
							procedure.cancel();
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to remove the grant.");
						}
					}
				}, true) {
					@Override
					public void onClose(Player player) {
						if (!isClosedByMenu()) {
							procedure.cancel();
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to remove the grant.");
						}
					}
				}.openMenu(event.getPlayer());
			}
		}
	}

}
