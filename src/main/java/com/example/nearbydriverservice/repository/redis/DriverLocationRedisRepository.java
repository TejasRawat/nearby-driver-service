package com.example.nearbydriverservice.repository.redis;

import static com.example.nearbydriverservice.repository.redis.constants.RedisContstants.GEO_KEY;

import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.model.DriverSearchQuery;
import com.example.nearbydriverservice.model.DriverSearchResult;
import com.example.nearbydriverservice.repository.DriverLocationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.resps.GeoRadiusResponse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DriverLocationRedisRepository implements DriverLocationRepository {

  private final JedisPool jedisPool;

  @Override
  public void saveDriversLocation(List<DriverLocationEvent> batch) {
    try (Jedis jedis = jedisPool.getResource()) {
      Pipeline pipeline = jedis.pipelined();

      for (DriverLocationEvent event : batch) {
        pipeline.geoadd(
            GEO_KEY,
            event.getLongitude(),
            event.getLatitude(),
            String.valueOf(event.getDriverId())
        );
      }

      pipeline.sync();
    } catch (Exception e) {
      log.error("Failed to save driver locations to Redis", e);
    }
  }

  @Override
  public List<DriverSearchResult> getDriversNearMe(DriverSearchQuery query) {
    try (Jedis jedis = jedisPool.getResource()) {

      List<GeoRadiusResponse> responses = jedis.georadius(
          GEO_KEY,
          query.getLongitude(),
          query.getLatitude(),
          query.getRadiusInKilometers(),
          GeoUnit.KM,
          GeoRadiusParam.geoRadiusParam()
              .withDist()
              .withCoord()
              .sortAscending()
      );

      return responses.stream().map(response ->
          DriverSearchResult.builder()
              .driverId(Integer.parseInt(response.getMemberByString()))
              .latitude(response.getCoordinate().getLatitude())
              .longitude(response.getCoordinate().getLongitude())
              .build()
      ).toList();

    } catch (Exception e) {
      log.error("Failed to search drivers in Redis", e);
      return List.of();
    }
  }

}
