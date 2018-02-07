package io.smartup.handyman.sample;

import io.smartup.handyman.MaintenanceStatusManager;
import io.smartup.handyman.MaintenanceStatusProvider;
import io.smartup.handyman.model.MaintenanceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class SampleRestController {

    private final static MaintenanceStatus OFF = MaintenanceStatus.NO_MAINTENANCE_MAINTENANCE_STATUS;
    private final static MaintenanceStatus PLANNED = new MaintenanceStatus()
            .setMode(MaintenanceStatus.MaintenanceMode.PLANNED);

    private final MaintenanceStatus[] statuses = new MaintenanceStatus[]{OFF, PLANNED};
    private int currentStatus = 0;

    @Autowired
    private MaintenanceStatusManager manager;

    @Autowired
    private MaintenanceStatusProvider provider;

    @GetMapping("/toggle")
    public ResponseEntity<String> toggleMaintenanceMode() {
        currentStatus = (currentStatus + 1) & 0x1;
        manager.setMaintenanceStatus(statuses[currentStatus]);

        return ResponseEntity.ok("Changed maintenance status to " + statuses[currentStatus]);
    }

    @GetMapping("/routed")
    public ResponseEntity<String> routed() {
        return ResponseEntity.ok("Zuul routed your request here. Maintenance mode is: " + provider.getStatus());
    }
}
