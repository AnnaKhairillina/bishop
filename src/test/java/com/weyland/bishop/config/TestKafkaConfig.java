package com.weyland.bishop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class TestKafkaConfig {
    
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
}