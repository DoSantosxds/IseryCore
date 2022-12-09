package net.iseryproject.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class ServerShutDownPacket extends Event {
    private final String serverName;

    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
