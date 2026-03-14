package com.example.paymentservice.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    public boolean hasProcessed(String eventId) {
        return processedEvents.contains(eventId);
    }

    public void markAsProcessed(String eventId) {
        processedEvents.add(eventId);
    }
}

