package com.weyland.bishop.config;

import org.springframework.context.annotation.Configuration;

import com.weyland.bishop.CommandService;
import com.weyland.bishop.MetricsService;

import jakarta.annotation.PostConstruct;

@Configuration
public class MetricsConfig {
    private final MetricsService metricsService;
    private final CommandService commandService;

    public MetricsConfig(MetricsService metricsService, CommandService commandService) {
        this.metricsService = metricsService;
        this.commandService = commandService;
    }

    @PostConstruct
    public void init() {
        metricsService.initQueueGauge(commandService);
    }
}