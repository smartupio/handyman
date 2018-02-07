package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;

public class InMemoryMaintenanceService implements MaintenanceStatusManager, MaintenanceStatusProvider {

    private MaintenanceStatus status;

    @Override
    public void setMaintenanceStatus(MaintenanceStatus status) {
        this.status = status;
    }

    @Override
    public MaintenanceStatus getStatus() {
        if (status == null) {
            synchronized (this) {
                if (status == null) {
                    this.status = MaintenanceStatus.NO_MAINTENANCE_MAINTENANCE_STATUS;
                }
            }
        }

        return this.status;
    }
}
