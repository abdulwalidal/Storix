package com.storix.config;

import com.storix.dto.FileResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, FileResponse> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, FileResponse> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(
                new JacksonJsonRedisSerializer<>(FileResponse.class)
        );

        template.afterPropertiesSet();

        return template;
    }
}