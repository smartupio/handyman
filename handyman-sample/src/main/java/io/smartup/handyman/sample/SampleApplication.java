package io.smartup.handyman.sample;

import com.netflix.zuul.ZuulFilter;
import io.smartup.handyman.InMemoryMaintenanceService;
import io.smartup.handyman.MaintenanceZuulFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableZuulServer
@EnableZuulProxy
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    public InMemoryMaintenanceService inMemoryMaintenanceService() {
        return new InMemoryMaintenanceService();
    }

    @Bean
    public ZuulFilter maintenanceFilter() {
        return new MaintenanceZuulFilter(inMemoryMaintenanceService());
    }

}
