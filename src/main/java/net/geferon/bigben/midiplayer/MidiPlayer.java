package net.geferon.bigben.midiplayer;

import net.geferon.bigben.BigBenPlugin;
import net.geferon.bigben.midiplayer.track.BaseTrack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MidiPlayer implements Runnable {
    /**
     * Is the player running
     */
    private boolean m_isRunning;

    /**
     * Last run enter time
     */
    private long m_lastEnter;

    /**
     * List of all playing music tracks
     */
    private final List<BaseTrack> m_playingTracks;

    /**
     * The task
     */
    private final BukkitTask m_task;

    @Inject
    public MidiPlayer(BigBenPlugin plugin) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        m_lastEnter = System.currentTimeMillis();
        m_task = scheduler.runTaskTimer(plugin, this, 1, 1);
        m_playingTracks = new ArrayList<>();
        m_isRunning = true;
    }

    /**
     * Stop the player
     */
    public void stop() {
        synchronized (m_playingTracks){
            m_isRunning = false;
            m_playingTracks.clear();
        }

        m_task.cancel();
    }

    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        final long delta = now - m_lastEnter;
        m_lastEnter = now;

        final BaseTrack[] tracks;
        synchronized (m_playingTracks) {
            tracks = m_playingTracks.toArray(new BaseTrack[0]);
        }

        for (BaseTrack track : tracks) {
            track.play(delta);
            if (track.isFinished()) {
                removeTrack(track);
            }
        }
    }

    /**
     * Remove track from playback
     *
     * @param track The track to be removed from this MusicPlayer instance
     */
    public void removeTrack(BaseTrack track) {
        if (track == null) {
            return;
        }
        synchronized (m_playingTracks) {
            m_playingTracks.remove(track);
        }
    }

    /**
     * Play provided track
     *
     * @param track The track to be played by this MusicPlayer instance
     */
    public void playTrack(BaseTrack track) {
        if (track == null) {
            return;
        }
        synchronized (m_playingTracks) {
            if (m_isRunning) {
                m_playingTracks.add(track);
            }
        }
    }
}
