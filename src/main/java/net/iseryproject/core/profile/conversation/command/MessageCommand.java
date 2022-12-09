package net.iseryproject.core.profile.conversation.command;


import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.conversation.Conversation;
import net.iseryproject.core.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "message", "msg", "whisper", "tell", "t" }, async = true)
public class MessageCommand {

    public void execute(Player player, Player target, String message) {
        if (player.equals(target)) {
            player.sendMessage(CC.RED + "You cannot message yourself!");
            return;
        }

        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        Profile playerProfile = Profile.getByUuid(player.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (targetProfile.getConversations().canBeMessagedBy(player)) {
            Conversation conversation = playerProfile.getConversations().getOrCreateConversation(target);

            if (conversation.validate()) {
                conversation.sendMessage(player, target, message);
            } else {
                player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
            }
        } else {
            player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
        }
    }

}
