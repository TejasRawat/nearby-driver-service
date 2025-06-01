package com.example.nearbydriverservice.listener;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DriverLocationTestUtil {

  // Lat long bounds for Berlin, Germany
  private static final double MIN_LAT = 52.338;
  private static final double MAX_LAT = 52.675;
  private static final double MIN_LON = 13.088;
  private static final double MAX_LON = 13.761;

  public static List<DriverLocationEvent> generateDriverLocations(int count) {
    List<DriverLocationEvent> locations = new ArrayList<>();

    for (int i = 1; i <= count; i++) {
      double lat = round(randomBetween(MIN_LAT, MAX_LAT));
      double lon = round(randomBetween(MIN_LON, MAX_LON));
      locations.add(new DriverLocationEvent(i, lat, lon));
    }

    return locations;
  }

  private static double randomBetween(double min, double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  private static double round(double value) {
    return new BigDecimal(value)
        .setScale(6, RoundingMode.HALF_UP)
        .doubleValue();
  }


}
