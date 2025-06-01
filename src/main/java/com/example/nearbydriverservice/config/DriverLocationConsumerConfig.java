package com.example.nearbydriverservice.config;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class DriverLocationConsumerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<Integer, DriverLocationEvent> driverLocationKafkaListenerContainerFactory(
      ConsumerFactory<Integer, DriverLocationEvent> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<Integer, DriverLocationEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setBatchListener(true);

    return factory;
  }


}
