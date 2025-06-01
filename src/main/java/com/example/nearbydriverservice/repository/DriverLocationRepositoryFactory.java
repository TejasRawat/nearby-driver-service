package com.example.nearbydriverservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DriverLocationRepositoryFactory {

  private final DriverLocationRepository driverLocationRedisRepository;
  private final DriverLocationRepository driverLocationElasticsearchRepository;

  @Value("${driver.location.repository.type:redis}")
  private String repositoryType;

  @Bean
  public DriverLocationRepository driverLocationRepository() {
    if ("elasticsearch".equalsIgnoreCase(repositoryType)) {
      return driverLocationElasticsearchRepository;
    }
    return driverLocationRedisRepository;
  }

}
