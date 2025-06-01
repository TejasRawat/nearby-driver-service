package com.example.nearbydriverservice.service;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import java.util.List;

public interface DriverLocationService {

  List<DriverSearchResult> getDriversNearMe(DriverSearchQuery query);

  void saveDriversLocation(List<DriverLocationEvent> batch);


}
