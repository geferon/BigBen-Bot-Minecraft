package net.geferon.bigben.configuration;

import org.bukkit.SoundCategory;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigProvider {

    /**
     * Number of ticks in one second
     */
    public static final int TICKS_PER_SECOND = 20;

    private static String m_configVersion;

    private static File m_pluginFolder;

    private static String m_instrumentMap;

    private static String m_drumMap;

    private static SoundCategory m_soundCategory;

    /**
     * Plugin root folder
     *
     * @return The root folder of this plugin
     */
    public static File getPluginFolder() {
        return m_pluginFolder;
    }

    /**
     * Get the config version
     *
     * @return Current config version
     */
    public static String getConfigVersion() {
        return m_configVersion;
    }

    public static String getInstrumentMapFile() {
        return m_instrumentMap;
    }

    public static String getDrumMapFile() {
        return m_drumMap;
    }

    public static SoundCategory getSoundCategory() {
        return m_soundCategory;
    }

    /**
     * Load configuration
     *
     * @param plugin parent plugin
     * @return true if config loaded
     */
    public static boolean load(JavaPlugin plugin) {
        if (plugin == null) {
            return false;
        }

        plugin.saveDefaultConfig();
        m_pluginFolder = plugin.getDataFolder();

        Configuration config = plugin.getConfig();
        ConfigurationSection mainSection = config.getConfigurationSection("midiPlayer");
        if (mainSection == null) {
            return false;
        }

        m_instrumentMap = mainSection.getString("map", "");
        m_drumMap = mainSection.getString("drum", "");

        m_soundCategory = parseSoundCategory(mainSection.getString("soundCategory", "music"));

        return true;
    }

    private static SoundCategory parseSoundCategory(String categoryName) {
        if (categoryName != null) {
            categoryName = categoryName.trim();
            for (SoundCategory c : SoundCategory.values()) {
                if (categoryName.equalsIgnoreCase(c.name())) {
                    return c;
                }
            }
        }

        //log(Level.WARNING, "Specified SoundCategory not found! Using " + SoundCategory.MUSIC.name());
        return SoundCategory.MUSIC;
    }
}
