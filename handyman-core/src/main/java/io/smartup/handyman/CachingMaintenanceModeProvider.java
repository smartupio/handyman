package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachingMaintenanceModeProvider implements MaintenanceModeProvider {

    private final static long DEFAULT_REFRESH_FREQUENCY_IN_MILLIS = 10000;

    private final MaintenanceModeProvider origin;
    private final long refreshFrequencyInMillis;
    private MaintenanceStatus cachedMaintenanceStatus;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CachingMaintenanceModeProvider(MaintenanceModeProvider origin) {
        this(origin, DEFAULT_REFRESH_FREQUENCY_IN_MILLIS);
    }

    public CachingMaintenanceModeProvider(MaintenanceModeProvider origin, long refreshFrequencyInMillis) {
        this.origin = origin;
        this.refreshFrequencyInMillis = refreshFrequencyInMillis;
    }

    @Override
    public MaintenanceStatus getStatus() {
        if (cachedMaintenanceStatus == null) {
            synchronized (this) {
                if (cachedMaintenanceStatus == null) {
                    scheduler.scheduleAtFixedRate(new MaintenanceModeRefresher(), refreshFrequencyInMillis,
                            refreshFrequencyInMillis, TimeUnit.MILLISECONDS);
                }
            }

            cachedMaintenanceStatus = origin.getStatus();
        }

        return cachedMaintenanceStatus;
    }

    private class MaintenanceModeRefresher implements Runnable {

        @Override
        public void run() {
            cachedMaintenanceStatus = origin.getStatus();
        }
    }
}
