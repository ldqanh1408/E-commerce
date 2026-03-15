package com.example.paymentservice.service;

import com.example.paymentservice.query.entity.ProcessedEvent;
import com.example.paymentservice.query.repository.ProcessedEventRepository; // Giả định bạn đã tạo Repository này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class IdempotencyService {

    private final ProcessedEventRepository repository;

    public IdempotencyService(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    public boolean hasProcessed(String eventId) {
        return repository.existsByEventId(eventId);
    }

    @Transactional
    public void markAsProcessed(String eventId) {
        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(eventId);
        processedEvent.setProcessedAt(Instant.now());
        repository.save(processedEvent);
    }
}
