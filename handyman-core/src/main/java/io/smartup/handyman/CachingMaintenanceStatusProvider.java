package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachingMaintenanceStatusProvider implements MaintenanceStatusProvider {

    private final static long DEFAULT_REFRESH_FREQUENCY_IN_MILLIS = 10000;

    private final MaintenanceStatusProvider origin;
    private final long refreshFrequencyInMillis;
    private MaintenanceStatus cachedMaintenanceStatus;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CachingMaintenanceStatusProvider(MaintenanceStatusProvider origin) {
        this(origin, DEFAULT_REFRESH_FREQUENCY_IN_MILLIS);
    }

    public CachingMaintenanceStatusProvider(MaintenanceStatusProvider origin, long refreshFrequencyInMillis) {
        this.origin = origin;
        this.refreshFrequencyInMillis = refreshFrequencyInMillis;
    }

    @Override
    public MaintenanceStatus getStatus() {
        if (cachedMaintenanceStatus == null) {
            synchronized (this) {
                if (cachedMaintenanceStatus == null) {
                    scheduler.scheduleAtFixedRate(
                            () -> cachedMaintenanceStatus = origin.getStatus(),
                            0,
                            refreshFrequencyInMillis,
                            TimeUnit.MILLISECONDS
                    );
                }
            }

            cachedMaintenanceStatus = origin.getStatus();
        }

        return cachedMaintenanceStatus;
    }

}
