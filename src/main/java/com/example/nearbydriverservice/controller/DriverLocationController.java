package com.example.nearbydriverservice.controller;

import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import com.example.nearbydriverservice.service.DriverLocationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DriverLocationController {

  private final DriverLocationService driverLocationService;

  @PostMapping("/drivers/near-me")
  public List<DriverSearchResult> getDriversNearMe(@RequestBody DriverSearchQuery query) {
    return driverLocationService.getDriversNearMe(query);
  }

}


