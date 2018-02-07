package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
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
                    cachedMaintenanceStatus = MaintenanceStatus.NO_MAINTENANCE_MAINTENANCE_STATUS;

                    scheduler.scheduleAtFixedRate(new CacheRefresher(), 0,
                            refreshFrequencyInMillis, TimeUnit.MILLISECONDS);
                }
            }
        }

        return cachedMaintenanceStatus;
    }

    private class CacheRefresher implements Runnable {

        @Override
        public void run() {
            try {
                cachedMaintenanceStatus = origin.getStatus();
                log.debug("Refreshed status of maintenance = {}", cachedMaintenanceStatus);
            } catch (Exception e) {
                log.warn("Could not refresh maintenance status in cache", e);
            }
        }
    }

}
