package io.smartup.handyman.sample;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.netflix.zuul.ZuulFilter;
import io.smartup.handyman.CachingMaintenanceStatusProvider;
import io.smartup.handyman.InMemoryMaintenanceStatusService;
import io.smartup.handyman.MaintenanceStatusManager;
import io.smartup.handyman.MaintenanceStatusProvider;
import io.smartup.handyman.MaintenanceZuulFilter;
import io.smartup.handyman.s3.S3MaintenanceStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
@EnableZuulServer
@EnableZuulProxy
public class SampleApplication {

    private final static String S3_FILE_NAME = "maintenance-mode-file.json";
    private final static String BUCKET_NAME = "handyman-test";

    private S3MaintenanceStatusService s3;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @PostConstruct
    public void init() {
        s3 = s3Service();
    }

    public InMemoryMaintenanceStatusService inMemoryService() {
        return new InMemoryMaintenanceStatusService();
    }

    public S3MaintenanceStatusService s3Service() {
        return S3MaintenanceStatusService.builder()
                .withAmazonS3(
                        AmazonS3ClientBuilder.standard()
                                .build()
                )
                .withBucketName(BUCKET_NAME)
                .withFileName(S3_FILE_NAME)
                .build();
    }


    @Bean
    public MaintenanceStatusProvider provider() {
        return new CachingMaintenanceStatusProvider(s3);
    }

    @Bean
    public MaintenanceStatusManager manager(){
        return s3;
    }

    @Bean
    public ZuulFilter maintenanceFilter() {
        return new MaintenanceZuulFilter(provider());
    }

}
