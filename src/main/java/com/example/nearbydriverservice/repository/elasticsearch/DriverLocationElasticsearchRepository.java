package com.example.nearbydriverservice.repository.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.UpdateAction;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import com.example.nearbydriverservice.repository.DriverLocationRepository;
import com.example.nearbydriverservice.repository.elasticsearch.constants.ElasticSearchConstants;
import com.example.nearbydriverservice.repository.elasticsearch.document.DriverLocation;
import com.example.nearbydriverservice.repository.elasticsearch.document.DriverLocationDocument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DriverLocationElasticsearchRepository implements DriverLocationRepository {

  private final ElasticsearchClient esClient;


  @Override
  @SneakyThrows
  public void saveDriversLocation(List<DriverLocationEvent> batch) {
    List<DriverLocationDocument> documents = toDocs(batch);
    BulkRequest bulkRequest = createBulkRequest(documents);
    BulkResponse bulkResponse = esClient.bulk(bulkRequest);
    if (bulkResponse.errors()) {
      for (int i = 0; i < bulkResponse.items().size(); i++) {
        BulkResponseItem item = bulkResponse.items().get(i);
        if (item.error() != null) {
          log.error("Error saving document with ID {}: {}", item.id(),
              item.error().reason());
        }
      }
    }
  }

  private List<DriverLocationDocument> toDocs(List<DriverLocationEvent> batch) {
    return batch.stream().map(
        event ->
            DriverLocationDocument.builder()
                .driverId(event.getDriverId())
                .location(
                    DriverLocation.builder()
                        .lat(event.getLatitude())
                        .lon(event.getLongitude())
                        .build()
                ).build()
    ).toList();
  }

  private BulkRequest createBulkRequest(
      List<DriverLocationDocument> documents) {
    return new BulkRequest.Builder().index(
            ElasticSearchConstants.ELASTICSEARCH_INDEX)
        .operations(getBulkOperations(documents)).build();
  }

  private List<BulkOperation> getBulkOperations(
      List<DriverLocationDocument> documents) {
    return documents.stream().map(doc -> new BulkOperation.Builder().update(
            u -> u.id(String.valueOf(doc.getDriverId()))
                .action(
                    UpdateAction.of(update -> update.docAsUpsert(true).doc(doc))))
        .build()).toList();
  }


  @Override
  @SneakyThrows
  public List<DriverSearchResult> getDriversNearMe(DriverSearchQuery query) {
    String distance = query.getRadiusInKilometers() + "km";

    List<DriverLocationDocument> driverDocs =
        esClient.search(s -> s.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
                .query(q -> q.geoDistance(
                        gd -> gd.field(ElasticSearchConstants.LOCATION_FIELD)
                            .distance(distance).location(
                                GeoLocation.of(loc -> loc.latlon(
                                        LatLonGeoLocation.of(coordinate -> coordinate
                                            .lat(query.getLatitude())
                                            .lon(query.getLongitude()))
                                    )
                                )
                            )
                    )
                ), DriverLocationDocument.class)
            .hits()
            .hits()
            .stream()
            .map(Hit::source)
            .toList();

    return driverDocs.stream()
        .map(doc ->
            DriverSearchResult.builder()
                .driverId(doc.getDriverId())
                .latitude(doc.getLocation().getLat())
                .longitude(doc.getLocation().getLon())
                .build()
        ).toList();
  }


}
