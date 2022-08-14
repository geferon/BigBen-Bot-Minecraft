package net.geferon.bigben;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BinderModule extends AbstractModule {
    private final BigBenPlugin plugin;

    public BinderModule(BigBenPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(BigBenPlugin.class).toInstance(this.plugin);
    }
}
