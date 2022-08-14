package net.geferon.bigben.midiplayer.track;

import net.geferon.bigben.midiplayer.midiparser.NoteFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Global music track - music played by this track type
 * is heard all over the server
 */
public class GlobalTrack extends BaseTrack {
    /**
     * The plugin
     */
    private final JavaPlugin m_plugin;

    @Override
    protected Collection<? extends Player> getPlayers() {
        return m_plugin.getServer().getOnlinePlayers();
    }

    public GlobalTrack(JavaPlugin plugin, NoteFrame[] notes) {
        this(plugin, notes, false);
    }

    public GlobalTrack(JavaPlugin plugin, NoteFrame[] notes, boolean loop) {
        super(notes, loop, true);
        m_plugin = plugin;
    }
}
