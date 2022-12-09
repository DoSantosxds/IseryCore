package net.iseryproject.core.profile.grant.event;

import net.iseryproject.core.profile.grant.Grant;
import net.iseryproject.core.util.BaseEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class GrantAppliedEvent extends BaseEvent {

	private Player player;
	private Grant grant;

}
