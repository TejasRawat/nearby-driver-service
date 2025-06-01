package com.example.nearbydriverservice.common;


import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticsearchContainerHolder {

  private static final ElasticsearchContainer ELASTICSEARCH = new ElasticsearchContainer(
      DockerImageName
          .parse("docker.elastic.co/elasticsearch/elasticsearch")
          .withTag("8.1.2")
  )
      .withEnv("xpack.security.enabled", "false")
      .withReuse(true);

  static {
    ELASTICSEARCH.start();
  }

  public static String getHttpHostAddress() {
    return ELASTICSEARCH.getHttpHostAddress();
  }


}
