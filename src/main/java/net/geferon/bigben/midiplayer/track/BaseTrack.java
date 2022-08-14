package net.geferon.bigben.midiplayer.track;

import net.geferon.bigben.configuration.ConfigProvider;
import net.geferon.bigben.midiplayer.midiparser.NoteFrame;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class BaseTrack {

    /**
     * Legth of 1/2 tick in miliseconds
     */
    private final static int HALF_TICK = 1000 / ConfigProvider.TICKS_PER_SECOND / 2;

    /**
     * Number of miliseconds to wait before performing loop
     */
    private final static int LOOP_WAIT = 1000;

    /**
     * Music track notes
     */
    private final NoteFrame[] m_notes;

    /**
     * Current track wait time
     */
    private long m_wait;

    /**
     * Is the track looped
     */
    private final boolean m_isLooped;

    /**
     * Track position
     */
    private int m_pos;

    /**
     * Next note to play
     */
    private NoteFrame m_nextNote;

    /**
     * Use the per player sound location
     */
    private final boolean m_perPlayerLocation;

    protected BaseTrack(NoteFrame[] notes, boolean loop, boolean singleLocation) {
        m_isLooped = loop;
        m_notes = notes;
        m_perPlayerLocation = !singleLocation;

        rewind();
    }


    /**
     * Rewind track to the begining.
     * Allows you to add the track once again to the player.
     */
    public final void rewind() {
        m_pos = 0;
        if (m_notes != null && m_notes.length > 0) {
            m_nextNote = m_notes[0];
            m_wait = m_nextNote.getWait();
        } else {
            m_nextNote = null;
            m_wait = 0;
        }
    }

    /**
     * Get list of players that should hear the music
     *
     * @return The list of players
     */
    protected abstract Collection<? extends Player> getPlayers();

    /**
     * Get the sound global location
     * (if null then get player location will be used)
     * @return The location tu use for the sound
     */
    protected Location getLocation() { return null; }

    /**
     * Get the sound location
     * @param player The player to get the location for
     * @return The location of the given player
     */
    protected Location getLocation(Player player)  { return null; }

    public void play(long delta) {
        m_wait -= delta;

        final Collection<? extends Player> players = getPlayers();
        final Location location = m_perPlayerLocation ? null : getLocation();

        while (m_wait <= HALF_TICK && m_nextNote != null) {
            for (Player p : players) {
                m_nextNote.play(p, m_perPlayerLocation ? getLocation(p) : location);
            }

            m_pos++;
            if (m_pos < m_notes.length) {
                m_nextNote = m_notes[m_pos];
                m_wait += m_nextNote.getWait();
            } else if (m_isLooped) {
                m_pos %= m_notes.length;
                m_nextNote = m_notes[m_pos];

                m_wait += LOOP_WAIT;
            } else {
                m_nextNote = null;
                synchronized (this)
                {
                    notify();
                }
            }
        }
    }

    /**
     * Is track finished
     *
     * @return Whether the track has finished playing or not
     */
    public boolean isFinished() {
        return m_nextNote == null;
    }
}
