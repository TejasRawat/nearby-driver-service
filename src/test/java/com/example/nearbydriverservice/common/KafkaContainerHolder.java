package com.example.nearbydriverservice.common;


import org.testcontainers.kafka.ConfluentKafkaContainer;

public class KafkaContainerHolder {


  static ConfluentKafkaContainer KAFKA = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.1")
      .withEnv("KAFKA_NUM_PARTITIONS", "4")
      .withReuse(true);


  static {
    KAFKA.start();
  }

  public static String getBootstrapServers() {
    return KAFKA.getBootstrapServers();
  }

}


