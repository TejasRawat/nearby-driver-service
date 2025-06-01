package com.example.nearbydriverservice.repository;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import java.util.List;

public interface DriverLocationRepository {

  void saveDriversLocation(List<DriverLocationEvent> batch);

  List<DriverSearchResult> getDriversNearMe(DriverSearchQuery query);
}
