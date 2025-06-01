package com.example.nearbydriverservice.repository.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DriverLocationDocument {

  private int driverId;
  private DriverLocation location;

}
