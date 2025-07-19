package com.weyland.bishop;

import org.springframework.context.annotation.Configuration;

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