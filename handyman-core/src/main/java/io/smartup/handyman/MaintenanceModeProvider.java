package io.smartup.handyman;

import io.smartup.handyman.model.MaintenanceStatus;

public interface MaintenanceModeProvider {

    MaintenanceStatus getStatus();
}
