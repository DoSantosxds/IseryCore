package net.iseryproject.core.profile.conversation.command;


import com.qrakn.honcho.command.CommandMeta;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.conversation.Conversation;
import net.iseryproject.core.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;

@CommandMeta(label = { "reply", "r" }, async = true)
public class ReplyCommand {

    public void execute(Player player, String message) {
        Profile playerProfile = Profile.getByUuid(player.getUniqueId());
        Conversation conversation = playerProfile.getConversations().getLastRepliedConversation();

        if (conversation != null) {
            if (conversation.validate()) {
                conversation.sendMessage(player, Bukkit.getPlayer(conversation.getPartner(player.getUniqueId())), message);
            } else {
                player.sendMessage(CC.RED + "You can no longer reply to that player.");
            }
        } else {
            player.sendMessage(CC.RED + "You have nobody to reply to.");
        }
    }

}
