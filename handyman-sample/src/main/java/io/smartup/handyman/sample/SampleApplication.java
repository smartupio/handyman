package io.smartup.handyman.sample;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.netflix.zuul.ZuulFilter;
import io.smartup.handyman.MaintenanceZuulFilter;
import io.smartup.handyman.s3.S3MaintenanceStatusService;
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

    private final static String S3_FILE_NAME = "maintenance-mode-file.json";
    private final static String BUCKET_NAME = "bucket";

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

//    @Bean
//    public InMemoryMaintenanceStatusService service() {
//        return new InMemoryMaintenanceStatusService();
//    }

    @Bean
    public S3MaintenanceStatusService service() {
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
    public ZuulFilter maintenanceFilter() {
        return new MaintenanceZuulFilter(service());
    }

}
