# Nearby Driver Location Service

A microservice built with Spring Boot for real-time driver location tracking and nearby driver search.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

This service consumes driver location updates from a Kafka topic, indexes them in either **Redis** or **Elasticsearch
** (configurable via the `driver.location.repository.type` property), and exposes a REST API to query nearby drivers
within a given radius.

- Set `driver.location.repository.type=redis` to use Redis (**default**).
- Set `driver.location.repository.type=elasticsearch` to use Elasticsearch.

The repository implementation is selected at runtime using a **factory pattern**, allowing seamless switching between
Redis and Elasticsearch backends.

```
                        +------------------+
                        |   Kafka Topic    |
                        | (DriverLocation) |
                        +--------+---------+
                                 |
                                 v
                    +----------------------------+
                    | NearByDriverLocationService|
                    |         Service            |
                    |     - Kafka Listener       |
                    +-------------+--------------+
                                  |
             +--------------------+--------------------+
             |                                         |
             v                                         v
+--------------------------+            +--------------------------+
|    Redis Repository      |            | Elasticsearch Repository |
| (Geospatial Indexing)    |            |   (Geo Query Support)    |
+--------------------------+            +--------------------------+

                                 ^
                                 |
                     +------------------------+
                     |        REST API        |
                     |  POST /drivers/near-me |
                     +-----------+------------+
                                 |
                                 v
                         +---------------+
                         |     Client    |
                         | (Web/App User)|
                         +---------------+
```

---

## Features

- Real-time Kafka consumer for driver location events
- Geospatial indexing using Redis or Elasticsearch (configurable)
- REST API to search drivers near a location within a radius
- Efficient batch processing and Kafka consumer config
- Integration tests for Kafka, Redis, Elasticsearch, and REST
- Factory pattern for runtime repository selection

---

## API Endpoints

### 🔍 `POST /drivers/near-me`

Search for drivers near a given location within a specified radius.

#### Request Body

```json
{
  "latitude": 52.338,
  "longitude": 13.088,
  "radiusInKilometers": 10
}
```

#### Response Body

```json
[
  {
    "driverId": 123,
    "latitude": 52.3401,
    "longitude": 13.0902
  },
  {
    "driverId": 456,
    "latitude": 52.3415,
    "longitude": 13.0879
  }
]
```


## Tech Stack

- Java 17
- Spring Boot 3.x
- Apache Kafka
- Redis (geospatial indexing)
- Elasticsearch 8.x (Java API client)
- Jackson (JSON serialization)
- JUnit 5 & Awaitility (testing)
- Testcontainers (integration tests)

---

## Getting Started

### Prerequisites

- Java 17+
- [Docker](https://www.docker.com/) (required for Testcontainers)
- Kafka (local or cloud)
- Elasticsearch (default: `http://localhost:9200`)
- Gradle or Maven
- Redis (default: `localhost:6379`)

> **Note:** Integration tests use [Testcontainers](https://www.testcontainers.org/) to automatically spin up Kafka,
> Elasticsearch, and Redis in Docker. Make sure Docker is running before executing tests.

### Build & Run

```bash
./gradlew clean build
./gradlew bootRun