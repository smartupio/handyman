package io.smartup.handyman.model;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class MaintenanceStatus {

    public static final MaintenanceStatus NO_MAINTENANCE_MAINTENANCE_STATUS =
            new MaintenanceStatus()
                    .setMode(MaintenanceMode.OFF)
                    .setStartTime(LocalDateTime.MIN)
                    .setEndTime(LocalDateTime.MAX);

    private MaintenanceMode mode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Getter
    public enum MaintenanceMode {

        OFF("Operating normally"),
        PLANNED("Planned maintenance"),
        UNPLANNED("Unplanned maintenance");

        private String message;

        MaintenanceMode(String message) {
            this.message = message;
        }
    }


}
