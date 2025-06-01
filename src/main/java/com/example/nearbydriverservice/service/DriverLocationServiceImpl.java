package com.example.nearbydriverservice.service;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import com.example.nearbydriverservice.repository.DriverLocationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLocationServiceImpl implements DriverLocationService {

  private final DriverLocationRepository driverLocationRepository;

  @Override
  public List<DriverSearchResult> getDriversNearMe(DriverSearchQuery query) {
    return driverLocationRepository.getDriversNearMe(query);
  }

  @Override
  public void saveDriversLocation(List<DriverLocationEvent> batch) {
    driverLocationRepository.saveDriversLocation(batch);
  }
}
