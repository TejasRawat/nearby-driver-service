plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.testcontainers:kafka")

    // Elasticsearch
    implementation("co.elastic.clients:elasticsearch-java:8.18.0")
    implementation("org.elasticsearch.client:elasticsearch-rest-client:8.18.0")
    testImplementation("org.testcontainers:elasticsearch")

    //Redis
    implementation("redis.clients:jedis:6.0.0")
    testImplementation("com.redis:testcontainers-redis:2.2.2")

    // Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
