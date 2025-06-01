package com.example.nearbydriverservice.listener;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles("test")
@Slf4j
class DriverLocationEventListenerElasticsearchTest extends AbstractIntegrationTest {

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("driver.location.repository.type", () -> "elasticsearch");
  }

  @Test
  void shouldStoreAllUniqueDriverLocationsAndReturnResults_whenEventsAreInserted() throws Exception {
    int numberOfDocuments = 10;
    List<DriverLocationEvent> driverLocationEvents = DriverLocationTestUtil.generateDriverLocations(numberOfDocuments);

    sendEvents(driverLocationEvents);

    assertDocsStoredInElasticsearch(numberOfDocuments);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/drivers/near-me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                DriverSearchQuery.builder()
                    .latitude(52.338)
                    .longitude(13.088)
                    .radiusInKilometers(50)
                    .build()
            )))
        .andExpect(status().isOk())
        .andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();

    List<DriverSearchResult> searchResults = objectMapper.readValue(contentAsString, new TypeReference<>() {
    });

    Assertions.assertNotNull(searchResults);
    Assertions.assertTrue(searchResults.size() > 0);
  }
}