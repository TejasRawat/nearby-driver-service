package com.example.nearbydriverservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DriverSearchResult {

  private int driverId;
  private double latitude;
  private double longitude;

}
