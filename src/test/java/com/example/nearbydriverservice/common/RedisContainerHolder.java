package com.example.nearbydriverservice.common;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainerHolder {

  private static final RedisContainer REDIS = new RedisContainer(
      DockerImageName
          .parse("redis")
          .withTag("7.2")
  )
      .withReuse(true);

  static {
    REDIS.start();
  }

  public static String getUrl() {
    return REDIS.getRedisURI();
  }

}
