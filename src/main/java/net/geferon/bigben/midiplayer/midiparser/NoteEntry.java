package net.geferon.bigben.midiplayer.midiparser;

import net.geferon.bigben.configuration.ConfigProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NoteEntry {

    private final String m_instrumentPatch;
    private final float m_volume;
    private final float m_frq;

    NoteEntry(String instrumentPatch, float frq, float volume) {
        m_instrumentPatch = instrumentPatch;
        m_frq = frq;
        m_volume = volume;
    }

    public void play(Player player, Location location) {
        if (m_instrumentPatch == null
                || m_volume == 0
                || player == null || !player.isOnline()) {
            return;
        }

        if (location == null) {
            location = player.getLocation();
        }

        if (m_frq < 0 || m_frq > 2) {
            return;
        }
        player.playSound(location, m_instrumentPatch, ConfigProvider.getSoundCategory(), m_volume, m_frq);
    }

    @Override
    public int hashCode() {
        return ((Float) m_frq).hashCode()
                ^ ((Float) m_volume).hashCode()
                ^ (m_instrumentPatch != null ? m_instrumentPatch.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NoteEntry) {
            NoteEntry other = (NoteEntry) obj;

            return m_frq == other.m_frq &&
                    //m_volume == other.m_volume &&
                    ((m_instrumentPatch == null && other.m_instrumentPatch == null) ||
                            (m_instrumentPatch != null && m_instrumentPatch.equals(other.m_instrumentPatch)));
        }

        return false;
    }


}
