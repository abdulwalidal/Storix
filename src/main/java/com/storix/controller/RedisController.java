//package com.storix.controller;
//
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("redis")
//public class RedisController {
//    private final RedisTemplate<String, String> redisTemplate;
//
//
//    public RedisController(RedisTemplate<String, String> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @GetMapping
//    public String testRedis() {
//        redisTemplate.opsForValue().set("storix:test", "hello redis");
//
//        return redisTemplate.opsForValue().get("storix:test");
//    }
//}
