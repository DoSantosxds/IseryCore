package net.iseryproject.core.messages;


import com.google.gson.JsonObject;
import net.evilblock.pidgin.message.handler.IncomingMessageHandler;
import net.evilblock.pidgin.message.listener.MessageListener;

import net.iseryproject.core.Core;
import net.iseryproject.core.CoreAPI;
import net.iseryproject.core.Locale;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.grant.Grant;
import net.iseryproject.core.profile.grant.event.GrantAppliedEvent;
import net.iseryproject.core.profile.grant.event.GrantExpireEvent;
import net.iseryproject.core.profile.punishment.Punishment;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessagesListener implements MessageListener {

    @IncomingMessageHandler(id = "STARTUP")
    public void onStartUp(JsonObject json) {
        String serverName = Bukkit.getServerName();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.getStaffOptions().staffModeEnabled()) {
                    CoreAPI.sendToStaff("&8[&eServer Monitor&8] &rAdding server " + serverName + "...");
                }
            }
        }
    }

/*   @IncomingMessageHandler(id = "STAFF_CHAT")
    public void onStaffChat(JsonObject json){
        String serverName = json.get("serverName").getAsString();
        String playerName = json.get("playerName").getAsString();
        String chatMessage = json.get("chatMessage").getAsString();

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.hasPermission("core.staff"))
                .forEach(onlinePlayer -> {
                    ReceiveStaffChatEvent event = new ReceiveStaffChatEvent(onlinePlayer);

                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

                        if (profile != null/* && profile.getStaffOptions().staffModeEnabled()*/
                            //onlinePlayer.sendMessage(Locale.STAFF_CHAT.format(playerName, serverName,
                                  //  chatMessage));




    @IncomingMessageHandler(id = "ADMIT")
    public void onAdmit(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        String playerName = json.get("playerName").getAsString();

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.hasPermission("core.staff"))
                .forEach(onlinePlayer -> {
                    Profile profile = Profile.getProfiles().get(onlinePlayer.getUniqueId());

                    if (profile != null && profile.getStaffOptions().staffModeEnabled()) {
                        onlinePlayer.sendMessage(CC.translate("&9[S] &7[" + serverName + "] &f" + playerName + " &badmitted to use cheats."));
                    }

                });
    }

    @IncomingMessageHandler(id = "FREEZE")
    public void onFreeze(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        String playerName = json.get("playerName").getAsString();
        String staffer = json.get("staffer").getAsString();
        String action = json.get("action").getAsString();

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.hasPermission("core.staff"))
                .forEach(onlinePlayer -> {
                    Profile profile = Profile.getProfiles().get(onlinePlayer.getUniqueId());

                    if (profile != null && profile.getStaffOptions().staffModeEnabled()) {
                        onlinePlayer.sendMessage(CC.translate("&9[S] &7[" + serverName + "] &f" + staffer + " &b" + action + " " + playerName + "&b."));
                    }

                });
    }

    @IncomingMessageHandler(id = "ADD_GRANT")
    public void onAddGrant(JsonObject json){
        Player player = Bukkit.getPlayer(UUID.fromString(json.get("playerUUID").getAsString()));
        Grant grant = Grant.DESERIALIZER.deserialize(json.getAsJsonObject("grant"));

        if (player != null) {
            Profile profile = Profile.getProfiles().get(player.getUniqueId());
            profile.getGrants().removeIf(other -> Objects.equals(other, grant));
            profile.getGrants().add(grant);

            new GrantAppliedEvent(player, grant);
        }
    }

    @IncomingMessageHandler(id = "DELETE_GRANT")
    public void onDeleteGrant(JsonObject json){
        Player player = Bukkit.getPlayer(UUID.fromString(json.get("playerUUID").getAsString()));
        Grant grant = Grant.DESERIALIZER.deserialize(json.getAsJsonObject("grant"));

        if (player != null) {
            Profile profile = Profile.getProfiles().get(player.getUniqueId());
            profile.getGrants().removeIf(other -> Objects.equals(other, grant));
            profile.getGrants().add(grant);

            new GrantExpireEvent(player, grant);
        }
    }

    @IncomingMessageHandler(id = "REPORT")
    public void onReport(JsonObject json){
        String serverName = json.get("serverName").getAsString();
        String sentBy = json.get("sentBy").getAsString();
        String accused = json.get("accused").getAsString();
        String reason = json.get("reason").getAsString();
        List<String> messages = Locale.STAFF_REPORT_BROADCAST.formatLines(sentBy, accused,
                reason, serverName, serverName);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.getStaffOptions().staffModeEnabled()) {
                    for (String message : messages) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @IncomingMessageHandler(id = "REQUEST")
    public void onRequest(JsonObject json){
        String serverName = json.get("serverName").getAsString();
        String sentBy = json.get("sentBy").getAsString();
        String reason = json.get("reason").getAsString();
        List<String> messages = Locale.STAFF_REQUEST_BROADCAST.formatLines(sentBy, reason,
                serverName, serverName);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.getStaffOptions().staffModeEnabled()) {
                    for (String message : messages) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @IncomingMessageHandler(id = "RANK_DELETE")
    public void onRankDelete(JsonObject json){
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        Rank rank = Rank.getRanks().remove(uuid);

        if (rank != null) {
            Core.broadcastOps("&8[&eNetwork&8] &fDeleted rank " + rank.getDisplayName() );
        }
    }

    @IncomingMessageHandler(id = "RANK_REFRESH")
    public void onRankRefresh(JsonObject json){
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        Rank rank = Rank.getRankByUuid(uuid);

        if (rank == null) {
            rank = new Rank(uuid, name);
        }

        rank.load();

        Core.broadcastOps("&8[&eNetwork&8] &fRefreshed rank " + rank.getDisplayName());
    }

    @IncomingMessageHandler(id = "PUNISHMENT")
    public void onPunishment(JsonObject json){
        Punishment punishment = Punishment.DESERIALIZER.deserialize(json.get("punishment").getAsJsonObject());
        String staff = json.get("staff").getAsString();
        String target = json.get("target").getAsString();
        UUID targetUUID = UUID.fromString(json.get("targetUUID").getAsString());
        boolean silent = json.get("silent").getAsBoolean();
        punishment.broadcast(staff, target, silent);

        Player player = Bukkit.getPlayer(targetUUID);

        if (player != null) {
            Profile profile = Profile.getProfiles().get(player.getUniqueId());
            profile.getPunishments().removeIf(other -> Objects.equals(other, punishment));
            profile.getPunishments().add(punishment);

            if (punishment.getType().isBan() && !punishment.isRemoved() && !punishment.hasExpired()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.kickPlayer(punishment.getKickMessage());
                    }
                }.runTask(Core.get());
            }
        }
    }

    @IncomingMessageHandler(id = "PUNISHMENT_CLEAR")
    public void onClearPunishments(JsonObject json){
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getPunishments().clear();
        }
    }

    @IncomingMessageHandler(id = "GRANT_CLEAR")
    public void onClearGrant(JsonObject json){
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getGrants().clear();
        }
    }
}
