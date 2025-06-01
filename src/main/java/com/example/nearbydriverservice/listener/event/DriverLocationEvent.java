package com.example.nearbydriverservice.listener.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverLocationEvent {

  private int driverId;
  private double latitude;
  private double longitude;

}
