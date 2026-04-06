package com.fhsh.daitda.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // 스프링 부트가 yml 설정을 읽어서 자동으로 만든 Factory를 주입
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key 직렬화 (String으로 저장)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value 직렬화 (String으로 저장)
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
