package com.example.nearbydriverservice.listener;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.service.DriverLocationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverLocationListener {

  private final DriverLocationService driverLocationService;

  @KafkaListener(
      topics = "driver-location-topic",
      containerFactory = "driverLocationKafkaListenerContainerFactory",
      groupId = "${spring.kafka.consumer.group-id}"
  )
  public void listen(List<DriverLocationEvent> batch
  ) {
    batch = squash(batch);
    driverLocationService.saveDriversLocation(batch);
  }

  private List<DriverLocationEvent> squash(List<DriverLocationEvent> batch) {
    return batch.stream()
        .collect(Collectors.toMap(
            DriverLocationEvent::getDriverId,
            event -> event,
            (oldEvent, newEvent) -> newEvent,
            java.util.LinkedHashMap::new
        ))
        .values()
        .stream()
        .toList();
  }

}
