package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;

public interface MaintenanceStatusProvider {

    MaintenanceStatus getStatus();
}
