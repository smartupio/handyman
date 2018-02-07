# SmartUp Handyman

## What does it do?
![Handyman Logo](./handyman.png "Handyman logo")

This project is meant to solve the simple problem of activating, inactivating and interrogating the status of a maintenance mode flag in a Java web application.

## Modules

* handyman-core - core models and interfaces
* handyman-parent - maven parent pom for easy dependency management
* handyman-s3 - implementation that stores and retrieves the maintenance mode settings from S3
* handyman-zuul - Zuul filter to check maintenance mode and short-circuit requests if it is enabled
* handyman-sample - Sample code to show you how to use it

## Core classes

#### `MaintenanceStatusManager`
Interface to set the maintenance status. 

```java
    void setMaintenanceStatus(MaintenanceStatus status);
```

#### `MaintenanceStatusProvider`
Interface for retrieving the maintenance status.

```java
    MaintenanceStatus getStatus();
```

#### `InMemoryMaintenanceStatusService`
Solely for testing purposes, or for really simple, single instance deployments. It is purely in-memory and does not persist maintenance status anywhere.

#### `CachingMaintenanceStatusProvider`
Provided a delegate `MaintenanceStatusProvider` called origin it is going to cache and periodically refresh the maintenance status.

You can provide the refresh interval, which defaults to 10 000 milliseconds.

## Backends

### S3 backend
To use the S3 backend you'll have to build an `S3MaintenanceStatusService`. You can use the nested builder to do so.

Example:
```java
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

```

## Filters
Filters allow you to short circuit the incoming request-response flow and quickly return [HTTP 503 (Service Unavailable)](
https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/503).

**IMPORTANT** In servlet context each request is going to hit your filters, no matter your choice of tech(Java Servlet Filter, Spring Once-Per-Request, Zuul, etc), you most probably want to use the `CachingMaintenanceStatusProvider` to avoid making too many round trips to the flag providing backend.

### Zuul Filter
Contains `MaintenanceZuulFilter` which is a "pre" filter in Zuul terms. It's going to stop the filter chain and respond to the incoming request without routing to origin.