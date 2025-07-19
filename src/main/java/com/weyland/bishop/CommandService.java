package com.weyland.bishop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CommandService {
    private static final Logger LOG = LoggerFactory.getLogger(CommandService.class);
    private static final int MAX_QUEUE_SIZE = 100;
    private final BlockingQueue<CommandRequest> commandQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    private final MetricsService metricsService;

    public CommandService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    // Инициализация обработки очереди
    @PostConstruct
    public void init() {
        LOG.info("Initializing command queue processor");
        processQueue();
    }

    public void executeCommand(CommandRequest command) {
        if (command.priority() == CommandPriority.CRITICAL) {
            processCriticalCommand(command);
        } else {
            enqueueCommonCommand(command);
        }
    }

    private void processCriticalCommand(CommandRequest command) {
        LOG.info("CRITICAL command executed: {}", command.description());
        metricsService.incrementAuthorCounter(command.author());
    }

    private void enqueueCommonCommand(CommandRequest command) {
        if (!commandQueue.offer(command)) {
            throw new IllegalStateException("Command queue is full");
        }
        LOG.info("COMMON command enqueued: {}", command.description());
    }

    @Async
    public void processQueue() {
        LOG.info("Starting command queue processing");
        while (true) {
            try {
                CommandRequest command = commandQueue.take();
                LOG.info("COMMON command executed: {} for author: {}", 
                command.description(), command.author());
                metricsService.incrementAuthorCounter(command.author());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn("Command queue processing interrupted");
                break;
            }
        }
    }

    public int getQueueSize() {
        return commandQueue.size();
    }
}