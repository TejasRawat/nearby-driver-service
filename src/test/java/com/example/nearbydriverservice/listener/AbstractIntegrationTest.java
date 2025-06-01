package com.example.nearbydriverservice.listener;

import static org.hamcrest.Matchers.equalTo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.example.nearbydriverservice.common.ElasticsearchContainerHolder;
import com.example.nearbydriverservice.common.KafkaContainerHolder;
import com.example.nearbydriverservice.common.RedisContainerHolder;
import com.example.nearbydriverservice.listener.event.DriverLocationEvent;
import com.example.nearbydriverservice.repository.elasticsearch.constants.ElasticSearchConstants;
import com.example.nearbydriverservice.repository.redis.constants.RedisContstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AbstractIntegrationTest {

  protected static final String TOPIC = "driver-location-topic";
  private static boolean elasticsearchSchemaInitialized = false;

  @Value("classpath:elasticsearch/schema.json")
  private Resource elasticsearchSchema;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ElasticsearchClient esClient;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected KafkaTemplate<Integer, DriverLocationEvent> kafkaTemplate;

  @Autowired
  private JedisPool jedisPool;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", KafkaContainerHolder::getBootstrapServers);
    registry.add("spring.elasticsearch.uris", ElasticsearchContainerHolder::getHttpHostAddress);
    registry.add("spring.data.redis.url", RedisContainerHolder::getUrl);
  }

  @BeforeEach
  void init() throws IOException {
    if (elasticsearchSchemaInitialized) {
      flushIndex();
      return;
    }

    boolean indexAlreadyExists =
        esClient.indices().exists(b -> b.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)).value();

    if (indexAlreadyExists) {
      flushIndex();
    } else {
      esClient.indices().create(
          new CreateIndexRequest.Builder()
              .index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
              .withJson(elasticsearchSchema.getInputStream())
              .build()
      );
    }
    elasticsearchSchemaInitialized = true;

    try (Jedis jedis = jedisPool.getResource()) {
      jedis.flushAll();
      jedis.functionFlush();
    }
  }

  private void flushIndex() throws IOException {
    esClient.deleteByQuery(
        dbq -> dbq.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)
            .query(qb -> qb.matchAll(b -> b))
            .waitForCompletion(true)
            .refresh(true)
            .conflicts(Conflicts.Proceed)
    );
  }

  protected void sendEvents(List<DriverLocationEvent> driverLocationEvents) {
    driverLocationEvents.forEach(
        event -> {
          CompletableFuture<SendResult<Integer, DriverLocationEvent>> future =
              kafkaTemplate.send(TOPIC, event.getDriverId(), event);
          try {
            future.get();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
    );
  }

  protected void assertDocsStoredInElasticsearch(int numberOfDocuments) {
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofSeconds(2))
        .until(this::getESDocCount, equalTo((long) numberOfDocuments));
  }

  protected long getESDocCount() throws IOException {
    esClient.indices().refresh();
    return esClient.count(b -> b.index(ElasticSearchConstants.ELASTICSEARCH_INDEX)).count();
  }

  protected void assertEntryInRedis(int expectedCount) {
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(2))
        .until(this::getRedisKeyCount, equalTo((long) expectedCount));

  }

  protected long getRedisKeyCount() {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.zcard(RedisContstants.GEO_KEY);
    }
  }

}
