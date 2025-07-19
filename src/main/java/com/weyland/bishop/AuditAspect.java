package com.weyland.bishop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String auditTopic;
    private final boolean useKafka;

    public AuditAspect(
        KafkaTemplate<String, String> kafkaTemplate,
        @Value("${audit.kafka.topic:audit-log}") String auditTopic,
        @Value("${audit.mode:console}") String auditMode
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditTopic = auditTopic;
        this.useKafka = "kafka".equalsIgnoreCase(auditMode);
    }

    @Around("@annotation(com.weyland.bishop.annotation.WeylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String params = Arrays.toString(joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            String auditMsg = String.format(
                "Method: %s | Params: %s | Result: %s", 
                methodName, params, result
            );
            sendAudit(auditMsg);
            return result;
        } catch (Throwable e) {
            String auditMsg = String.format(
                "Method: %s | Params: %s | Error: %s", 
                methodName, params, e.getMessage()
            );
            sendAudit(auditMsg);
            throw e;
        }
    }

    private void sendAudit(String message) {
        if (useKafka) {
            kafkaTemplate.send(auditTopic, message);
        } else {
            System.out.println("[AUDIT] " + message);
        }
    }
}