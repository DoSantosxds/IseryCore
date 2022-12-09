package net.iseryproject.core.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class BasicConfigurationFile extends AbstractConfigurationFile
{
    private final File file;
    private final YamlConfiguration configuration;

    public BasicConfigurationFile(final JavaPlugin plugin, final String name, final boolean overwrite) {
        super(plugin, name);
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        plugin.saveResource(name + ".yml", overwrite);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public BasicConfigurationFile(final JavaPlugin plugin, final String name) {
        this(plugin, name, false);
    }

    @Override
    public String getString(final String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return null;
    }

    @Override
    public String getStringOrDefault(final String path, final String or) {
        final String toReturn = this.getString(path);
        return (toReturn == null) ? or : toReturn;
    }

    @Override
    public int getInteger(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public boolean getBoolean(final String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    @Override
    public double getDouble(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0;
    }

    @Override
    public Object get(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.get(path);
        }
        return null;
    }

    @Override
    public List<String> getStringList(final String path) {
        if (this.configuration.contains(path)) {
            return (List<String>)this.configuration.getStringList(path);
        }
        return null;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}
