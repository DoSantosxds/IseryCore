package net.iseryproject.core.profile.punishment.listener;

import network.katana.core.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class TwoFactorListener implements Listener {
    private final String denyMessage = ChatColor.RED + "You cannot do this whilst not authenticated.";

    @EventHandler (priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(!profile.getOptions().twoFA()) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        if(from.getX() != to.getX() || from.getZ() != to.getZ()) {
            Location newLocation = from.getBlock().getLocation().add(0.5, 0.0, 0.5);
            newLocation.setPitch(to.getPitch());
            newLocation.setYaw(to.getYaw());
            event.setTo(newLocation);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if(profile == null || !profile.isLoaded() || !profile.getOptions().twoFA()) return;
        damager.sendMessage(ChatColor.RED + player.getDisplayName() + ChatColor.RED + " cannot be damaged as they are not logged in.");
        event.setCancelled(true);
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event){
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        String command = event.getMessage().split(" ")[0];
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            if(!command.contains("login")) {
                event.getPlayer().sendMessage(denyMessage);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
        Player player = (Player) event.getWhoClicked();
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            player.sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        if(profile == null || !profile.isLoaded()) return;
        if(profile.getOptions().twoFA()) {
            event.getPlayer().sendMessage(denyMessage);
            event.setCancelled(true);
        }
    }
}
