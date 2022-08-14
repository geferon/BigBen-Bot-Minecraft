package net.geferon.bigben.jobs;

import net.geferon.bigben.BigBenPlugin;
import net.geferon.bigben.midiplayer.midiparser.MidiParser;
import net.geferon.bigben.midiplayer.midiparser.NoteFrame;
import net.geferon.bigben.midiplayer.midiparser.NoteTrack;
import net.geferon.bigben.midiplayer.track.GlobalTrack;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BigBenJob implements Job {
    private final BigBenPlugin plugin;
    @Inject
    public BigBenJob(BigBenPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Calendar curDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));

        int hour = curDate.get(Calendar.HOUR_OF_DAY) % 12;
        if (hour == 00) hour = 12;

        plugin.getLogger().log(Level.INFO, "Playing chimes for " + hour);

        String[] tracksToPlay = new String[] {"p2", "p3", "p4", "p5"};

        try {
            for (String trackToPlay : tracksToPlay) {
                NoteTrack track = MidiParser.loadFile(new File(new File(plugin.getDataFolder(), "chimes"), trackToPlay + ".midi"));
                if (track == null || track.isError()) {
                    throw new JobExecutionException("Error loading the track");
                }

                final NoteFrame[] notes = track.getNotes();
                GlobalTrack gTrack = new GlobalTrack(plugin, notes, false);
                plugin.getMidiPlayer().playTrack(gTrack);
                synchronized (gTrack) {
                    plugin.getLogger().log(Level.INFO, "Starting playing " + trackToPlay);
                    gTrack.wait();
                    Thread.sleep(1000);
                    plugin.getLogger().log(Level.INFO, "Finished playing " + trackToPlay);
                }
            }

            NoteTrack hourTrack = MidiParser.loadFile(new File(new File(plugin.getDataFolder(), "chimes"), "hour.midi"));
            if (hourTrack == null || hourTrack.isError()) {
                throw new JobExecutionException("Error loading the track");
            }
            final NoteFrame[] hourNotes = hourTrack.getNotes();

            Thread.sleep(6000);
            for (int i = 0; i < hour; i++) {
                GlobalTrack gTrack = new GlobalTrack(plugin, hourNotes, false);
                plugin.getMidiPlayer().playTrack(gTrack);
                synchronized (gTrack) {
                    gTrack.wait();
                    if (i != hour)
                        Thread.sleep(2500);
                }
            }
        } catch (InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }
}
