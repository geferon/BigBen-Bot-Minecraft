package net.geferon.bigben.midiplayer.midiparser;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class NoteFrame {

    /**
     * The frame wait in milliseconds
     */
    private final long m_wait;

    /**
     * Get the wait delay in milliseconds
     * @return The wait delay
     */
    public long getWait() {
        return m_wait;
    }

    /**
     * The notes
     */
    private final NoteEntry[] m_notes;

    public NoteFrame(long delta, Set<TrackEntry> notes) {
        m_wait = delta;

        if (notes == null) {
            m_notes = new NoteEntry[0];
        } else {
            final int cnt = notes.size();
            m_notes = new NoteEntry[cnt];

            int i = 0;
            for (TrackEntry entry : notes) {
                m_notes[i] = entry.getNote();
                i++;
            }
        }
    }

    public void play(Player player, Location location) {
        if (player == null || !player.isOnline()) {
            return;
        }

        if (location == null) {
            location = player.getLocation();
        }
        for (NoteEntry note : m_notes) {
            note.play(player, location);
        }
    }
}