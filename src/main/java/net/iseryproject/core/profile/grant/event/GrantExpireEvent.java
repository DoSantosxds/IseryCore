package net.iseryproject.core.profile.grant.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.iseryproject.core.profile.grant.Grant;
import net.iseryproject.core.util.BaseEvent;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class GrantExpireEvent extends BaseEvent {

	private Player player;
	private Grant grant;

}
