package com.example.nearbydriverservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

  @Bean
  public ElasticsearchClient esClient(RestClientBuilder restClientBuilder, ObjectMapper objectMapper) {
    var restClientTransport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper(objectMapper));
    return new ElasticsearchClient(restClientTransport);
  }

}
