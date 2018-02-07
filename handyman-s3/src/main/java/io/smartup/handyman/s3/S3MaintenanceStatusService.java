package io.smartup.handyman.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smartup.handyman.MaintenanceStatusManager;
import io.smartup.handyman.MaintenanceStatusProvider;
import io.smartup.handyman.model.MaintenanceStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3MaintenanceStatusService implements MaintenanceStatusManager, MaintenanceStatusProvider {

    private final AmazonS3 s3;
    private final String bucketName;
    private final String fileName;
    private final ObjectMapper objectMapper;

    private S3MaintenanceStatusService(AmazonS3 s3,
                                       String bucketName,
                                       String fileName,
                                       ObjectMapper objectMapper) {
        this.s3 = s3;
        this.bucketName = bucketName;
        this.fileName = fileName;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setMaintenanceStatus(MaintenanceStatus status) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(status);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            s3.putObject(new PutObjectRequest(bucketName, fileName, bais, metadata));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can not deserialize MaintenanceStatusChange", e);
        }
    }

    @Override
    public MaintenanceStatus getStatus() {
        try (InputStream is = s3.getObject(bucketName, fileName).getObjectContent()) {
            MaintenanceStatus ms = objectMapper.readValue(is, MaintenanceStatus.class);

            return ms;
        } catch (IOException e) {
            if (!s3.doesObjectExist(bucketName, fileName)) {
                return MaintenanceStatus.NO_MAINTENANCE_MAINTENANCE_STATUS;
            }

            throw new IllegalArgumentException("Can not deserialize MaintenanceStatus from S3" +
                    " bucket=" + bucketName +
                    " key=" + fileName, e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String DEFAULT_FILENAME = "maintenance_mode.json";

        private AmazonS3 s3 = null;
        private String bucketName;
        private String fileName = DEFAULT_FILENAME;
        private ObjectMapper om = null;

        public Builder withAmazonS3(AmazonS3 s3) {
            this.s3 = s3;

            return this;
        }

        public Builder withBucketName(String bucketName) {
            this.bucketName = bucketName;

            return this;
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;

            return this;
        }

        public Builder withObjectMapper(ObjectMapper om) {
            this.om = om;

            return this;
        }

        public S3MaintenanceStatusService build() {
            if (this.s3 == null) {
                throw new IllegalStateException("AmazonS3 must be provided");
            }

            if (this.bucketName == null) {
                throw new IllegalStateException("BucketName must be provided");
            }

            if (this.om == null) {
                om = new ObjectMapper();
            }

            return new S3MaintenanceStatusService(this.s3, this.bucketName, this.fileName, this.om);
        }

    }
}
