package com.weyland.bishop;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.weyland.bishop.config.TestKafkaConfig;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestKafkaConfig.class)
class BishopStarterApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
        assertThat(context.containsBean("auditAspect")).isTrue();
        assertThat(context.containsBean("commandService")).isTrue();
    }
}