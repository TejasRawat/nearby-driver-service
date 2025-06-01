package com.example.nearbydriverservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {

  @Bean
  public JedisPool jedisPool(@Value("${spring.data.redis.url}") String url) {
    return new JedisPool(url);
  }

}
