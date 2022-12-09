package net.iseryproject.core.util;

import network.katana.core.Core;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

public class Tip {

    static int i = 0;

    public static void init(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if(i == 0){
                    Bukkit.broadcastMessage(CC.translate(""));
                    Bukkit.broadcastMessage(CC.translate("&fMake sure to follow us on Twitter!"));
                   // Bukkit.broadcastMessage(CC.translate("&6https://aikar.cc/twitter"));
                    Bukkit.broadcastMessage(CC.translate(""));
                    i = i + 1;
                }else if(i == 1){
                    Bukkit.broadcastMessage(CC.translate(""));
                    Bukkit.broadcastMessage(CC.translate("&fMake sure to join our Discord!"));
                    //Bukkit.broadcastMessage(CC.translate("&6https://discord.aikar.cc/"));
                    Bukkit.broadcastMessage(CC.translate(""));
                    i = i + 1;
                }else if(i == 2){
                    Bukkit.broadcastMessage(CC.translate(""));
                    Bukkit.broadcastMessage(CC.translate("&fWe have a 20% OFF on our Store!"));
                   // Bukkit.broadcastMessage(CC.translate("&6https://store.aikar.cc/"));
                    Bukkit.broadcastMessage(CC.translate(""));
                    i = 0;
                }
            }
        }.runTaskTimerAsynchronously(Core.get(), 0, 60 * 20L);
    }

}
