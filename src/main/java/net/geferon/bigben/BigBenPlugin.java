package net.geferon.bigben;

import com.google.inject.Injector;
import lombok.Getter;
import net.geferon.bigben.configuration.ConfigProvider;
import net.geferon.bigben.jobs.BigBenJob;
import net.geferon.bigben.midiplayer.MidiPlayer;
import net.geferon.bigben.midiplayer.instruments.MapFileParser;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BigBenPlugin extends JavaPlugin {
    private static Logger s_log = null;

    /**
     * Send message to the log
     *
     * @param lvl The level of the error
     * @param msg Message to log
     */
    public static void log(Level lvl, String msg) {
        if (s_log == null || msg == null) {
            return;
        }

        s_log.log(lvl, msg);
    }

    @Getter
    private Scheduler scheduler;

    @Inject
    @Getter
    private MidiPlayer midiPlayer;

    @Override
    public void onEnable() {
        s_log = getLogger();

        BinderModule module = new BinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        loadConfig();

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(injector.getInstance(BinderJobFactory.class));

            configureSchedules();

            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        midiPlayer.stop();
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void configureSchedules() throws SchedulerException {
        scheduler.scheduleJob(
            JobBuilder.newJob(BigBenJob.class)
                .build(),
            TriggerBuilder.newTrigger()
                .startNow()
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * 1/1 * ? *")) // Testing
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0/1 1/1 * ? *")) // Prod
                .build()
        );
    }

    public void loadConfig() {
        saveDefaultConfig();
        for (String file : new String[] {"default.map", "default.drm",
                "chimes/hour.midi", "chimes/p1.midi", "chimes/p2.midi", "chimes/p3.midi", "chimes/p4.midi", "chimes/p5.midi"}) {
            saveResource(file, true);
        }
        ConfigProvider.load(this);

        String instrumentName = ConfigProvider.getInstrumentMapFile();
        String drumName = ConfigProvider.getDrumMapFile();
        File instrumentFile = new File(ConfigProvider.getPluginFolder(), instrumentName);
        File drumFile = new File(ConfigProvider.getPluginFolder(), drumName);

        if (!MapFileParser.loadMap(instrumentFile)) {
            getLogger().log(Level.INFO, "Failed to load instrument map, loading default.");
            if (!MapFileParser.loadDefaultMap())
                getLogger().log(Level.SEVERE, "Failed to load instrument map.");
        }
        if (!MapFileParser.loadDrumMap(drumFile)) {
            getLogger().log(Level.INFO, "Failed to load drum map, loading default.");
            if (!MapFileParser.loadDefaultDrumMap())
                getLogger().log(Level.SEVERE, "Failed to load drum map.");
        }
    }
}
