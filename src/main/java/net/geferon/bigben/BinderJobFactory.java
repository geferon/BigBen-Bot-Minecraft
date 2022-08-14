package net.geferon.bigben;

import com.google.inject.Injector;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import javax.inject.Inject;

public class BinderJobFactory implements JobFactory {
    private Injector injector;

    @Inject
    public BinderJobFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        return injector.getInstance(bundle.getJobDetail().getJobClass());
    }
}
