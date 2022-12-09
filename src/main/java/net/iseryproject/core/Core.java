package net.iseryproject.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.qrakn.honcho.Honcho;
import com.qrakn.honcho.command.example.ExampleCommand;
import lombok.Getter;
import lombok.Setter;
import me.jesusmx.spigot.commands.PingCommand;
import net.evilblock.pidgin.Pidgin;
import net.evilblock.pidgin.PidginOptions;
import net.iseryproject.core.cache.RedisCache;
import net.iseryproject.core.chat.Chat;
import net.iseryproject.core.chat.ChatListener;
import net.iseryproject.core.chat.command.ClearChatCommand;
import net.iseryproject.core.chat.command.MuteChatCommand;
import net.iseryproject.core.chat.command.SlowChatCommand;
import net.iseryproject.core.config.ConfigValidation;
import net.iseryproject.core.messages.MessagesListener;
import net.iseryproject.core.profile.Profile;
import net.iseryproject.core.profile.ProfileListener;
import net.iseryproject.core.profile.ProfileTypeAdapter;
import net.iseryproject.core.profile.conversation.command.MessageCommand;
import net.iseryproject.core.profile.conversation.command.ReplyCommand;
import net.iseryproject.core.profile.grant.GrantListener;
import net.iseryproject.core.profile.grant.command.ClearGrantsCommand;
import net.iseryproject.core.profile.grant.command.GrantCommand;
import net.iseryproject.core.profile.grant.command.GrantsCommand;
import net.iseryproject.core.profile.option.command.OptionsCommand;
import net.iseryproject.core.profile.option.command.ToggleGlobalChatCommand;
import net.iseryproject.core.profile.option.command.TogglePrivateMessagesCommand;
import net.iseryproject.core.profile.option.command.ToggleSoundsCommand;
import net.iseryproject.core.profile.punishment.command.*;
import net.iseryproject.core.profile.punishment.listener.PunishmentListener;
import net.iseryproject.core.profile.staff.command.AltsCommand;
import net.iseryproject.core.profile.staff.command.StaffChatCommand;
import net.iseryproject.core.profile.staff.command.StaffModeCommand;
import net.iseryproject.core.rank.Rank;
import net.iseryproject.core.rank.RankTypeAdapter;
import net.iseryproject.core.rank.command.*;
import net.iseryproject.core.util.BasicConfigurationFile;
import net.iseryproject.core.util.CC;
import net.iseryproject.core.util.adapter.ChatColorTypeAdapter;
import net.iseryproject.core.util.duration.Duration;
import net.iseryproject.core.util.duration.DurationTypeAdapter;
import net.iseryproject.core.util.menu.MenuListener;
import net.iseryproject.core.util.menu.MenuUpdateTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.defaults.ClearCommand;
import org.bukkit.command.defaults.ListCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Core extends JavaPlugin {

    public static final Gson GSON = new Gson();
    public static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();

    private static Core core;



    @Getter private BasicConfigurationFile mainConfig;

    @Getter private Honcho honcho;
    @Getter private Pidgin pidgin;

    @Getter private MongoDatabase mongoDatabase;
    @Getter private JedisPool jedisPool;
    @Getter private RedisCache redisCache;

    @Getter private Chat chat;

    public Map<String, Integer> playerCount = new HashMap<>();
    public int proxyCount = 0;

    @Getter @Setter private boolean debug;

    @Override
    public void onEnable() {
        core = this;

        mainConfig = new BasicConfigurationFile(this, "config");

        new ConfigValidation(mainConfig.getFile(), mainConfig.getConfiguration(), 4).check();

        loadMongo();
        loadRedis();

        redisCache = new RedisCache(this);
        chat = new Chat(this);

        honcho = new Honcho(this);

        Arrays.asList(
                new ExampleCommand.BroadcastCommand(),
                new ClearCommand(),
                new ExampleCommand.GameModeCommand(),
                new ClearChatCommand(),
                new SlowChatCommand(),
                new AltsCommand(),
                new BanCommand(),
                new CheckCommand(),
                new KickCommand(),
                new MuteCommand(),
                new UnbanCommand(),
                new UnblacklistCommand(),
                new BlacklistCommand(),
                new UnmuteCommand(),
                new WarnCommand(),
                new GrantCommand(),
                new GrantsCommand(),
                new StaffChatCommand(),
                new StaffModeCommand(),
                new MuteChatCommand(),
                new OptionsCommand(),
                new RankAddPermissionCommand(),
                new RankCreateCommand(),
                new RankDeleteCommand(),
                new RankHelpCommand(),
                new RankInfoCommand(),
                new RankInheritCommand(),
                new RankRemovePermissionCommand(),
                new RanksCommand(),
                new RankSetColorCommand(),
                new RankSetPrefixCommand(),
                new RankSetWeightCommand(),
                new RankUninheritCommand(),
                new MessageCommand(),
                new ReplyCommand(),
                new ToggleGlobalChatCommand(),
                new TogglePrivateMessagesCommand(),
                new ToggleSoundsCommand(),
                //new PingCommand(),
                new ListCommand(),
                //new ReportCommand(),
                //new RequestCommand(),
                new ClearGrantsCommand(),
                new ClearPunishmentsCommand(),
                new ClearAllPunishmentsCommand()

        ).forEach(honcho::registerCommand);

        honcho.registerTypeAdapter(Rank.class, new RankTypeAdapter());
        honcho.registerTypeAdapter(Profile.class, new ProfileTypeAdapter());
        honcho.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        honcho.registerTypeAdapter(ChatColor.class, new ChatColorTypeAdapter());

        pidgin = new Pidgin("Zoot:ALL", jedisPool, new PidginOptions(true));

        pidgin.registerListener(new MessagesListener());
        Arrays.asList(
                new ProfileListener(),
                new MenuListener(),
                new ChatListener(),
                new GrantListener(),
                //new TwoFactorListener(),
                //new FreezeListener(),
                new PunishmentListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));


        this.getServer().getScheduler().runTaskTimer(this, new MenuUpdateTask(), 0, 1L);
    }

    @Override
    public void onDisable() {
        try {
            jedisPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all server operators.
     *
     * @param message The message.
     */
    public static void broadcastOps(String message) {
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> op.sendMessage(CC.translate(message)));
    }

    private void loadMongo() {
        if (mainConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            ServerAddress serverAddress = new ServerAddress(mainConfig.getString("MONGO.HOST"),
                    mainConfig.getInteger("MONGO.PORT"));

            MongoCredential credential = MongoCredential.createCredential(
                    mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"), "admin",
                    mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray());

            mongoDatabase = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build())
                    .getDatabase(mainConfig.getString("MONGO.DATABASE"));
        } else {
            mongoDatabase = new MongoClient(mainConfig.getString("MONGO.HOST"),
                    mainConfig.getInteger("MONGO.PORT")).getDatabase(mainConfig.getString("MONGO.DATABASE"));
        }
    }

    private void loadRedis() {
        jedisPool = new JedisPool(mainConfig.getString("REDIS.HOST"), mainConfig.getInteger("REDIS.PORT"));

        if (mainConfig.getBoolean("REDIS.AUTHENTICATION.ENABLED")) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.auth(mainConfig.getString("REDIS.AUTHENTICATION.PASSWORD"));
            }
        }
    }



    public static Core get() {
        return core;
    }

}