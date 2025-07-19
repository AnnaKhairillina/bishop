package com.weyland.bishop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CommandService {
    private static final Logger LOG = LoggerFactory.getLogger(CommandService.class);
    private static final int MAX_QUEUE_SIZE = 100;
    private final BlockingQueue<CommandRequest> commandQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    private final MetricsService metricsService;
    private final ApplicationContext context;

    public CommandService(MetricsService metricsService, ApplicationContext context) {
        this.metricsService = metricsService;
        this.context = context;
    }

    @PostConstruct
    public void init() {
        LOG.info("Initializing command queue processor");
        new Thread(this::processQueue).start();
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

    public void processQueue() {
        LOG.info("Starting command queue processing");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                CommandRequest command = commandQueue.take();
                context.getBean(CommandService.class).asyncProcessCommand(command);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn("Command queue processing interrupted");
                break;
            }
        }
    }

    @Async("taskExecutor")
    public void asyncProcessCommand(CommandRequest command) {
        LOG.info("COMMON command executed: {} for author: {}", 
                command.description(), command.author());
        metricsService.incrementAuthorCounter(command.author());
    }

    public int getQueueSize() {
        return commandQueue.size();
    }
    
    public void resetQueue() {
        commandQueue.clear();
        LOG.warn("Command queue has been reset");
    }
}