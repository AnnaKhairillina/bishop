package com.weyland.bishop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class MetricsService {
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> authorCounters = new ConcurrentHashMap<>();
    private Gauge queueGauge;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void initQueueGauge(CommandService commandService) {
        queueGauge = Gauge.builder("android.queue.size", commandService::getQueueSize)
                        .description("Current command queue size")
                        .register(meterRegistry);
    }

    public void incrementAuthorCounter(String author) {
        authorCounters.computeIfAbsent(author, 
            a -> Counter.builder("android.commands.executed")
                        .tag("author", a)
                        .description("Total commands executed by author")
                        .register(meterRegistry))
            .increment();
    }
}