package com.example.productservice.command.aggregate;

import com.example.productapi.commands.CreateProductCommand;
import com.example.productapi.commands.DeleteProductCommand;
import com.example.productapi.commands.UpdateProductCommand;
import com.example.productapi.events.ProductCreatedEvent;
import com.example.productapi.events.ProductDeletedEvent;
import com.example.productapi.events.ProductUpdatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
@Slf4j
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private String imageUrl;
    private Long categoryId;
    private Boolean active;
    private boolean deleted;

    // ── Command Handlers ────────────────────────────────────────────

    @CommandHandler
    public ProductAggregate(CreateProductCommand cmd) {
        log.info("Handling CreateProductCommand: {}", cmd.getProductId());

        AggregateLifecycle.apply(ProductCreatedEvent.builder()
                .productId(cmd.getProductId())
                .name(cmd.getName())
                .description(cmd.getDescription())
                .price(cmd.getPrice())
                .sku(cmd.getSku())
                .imageUrl(cmd.getImageUrl())
                .categoryId(cmd.getCategoryId())
                .active(cmd.getActive() != null ? cmd.getActive() : true)
                .build());
    }

    @CommandHandler
    public void handle(UpdateProductCommand cmd) {
        if (this.deleted) {
            throw new IllegalStateException("Sản phẩm đã bị xoá, không thể cập nhật");
        }
        log.info("Handling UpdateProductCommand: {}", cmd.getProductId());

        AggregateLifecycle.apply(ProductUpdatedEvent.builder()
                .productId(cmd.getProductId())
                .name(cmd.getName())
                .description(cmd.getDescription())
                .price(cmd.getPrice())
                .sku(cmd.getSku())
                .imageUrl(cmd.getImageUrl())
                .categoryId(cmd.getCategoryId())
                .active(cmd.getActive())
                .build());
    }

    @CommandHandler
    public void handle(DeleteProductCommand cmd) {
        if (this.deleted) {
            throw new IllegalStateException("Sản phẩm đã bị xoá trước đó");
        }
        log.info("Handling DeleteProductCommand: {}", cmd.getProductId());

        AggregateLifecycle.apply(new ProductDeletedEvent(cmd.getProductId()));
    }

    // ── Event Sourcing Handlers (Rebuild state) ─────────────────────

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.price = event.getPrice();
        this.sku = event.getSku();
        this.imageUrl = event.getImageUrl();
        this.categoryId = event.getCategoryId();
        this.active = event.getActive();
        this.deleted = false;
    }

    @EventSourcingHandler
    public void on(ProductUpdatedEvent event) {
        if (event.getName() != null) this.name = event.getName();
        if (event.getDescription() != null) this.description = event.getDescription();
        if (event.getPrice() != null) this.price = event.getPrice();
        if (event.getSku() != null) this.sku = event.getSku();
        if (event.getImageUrl() != null) this.imageUrl = event.getImageUrl();
        if (event.getCategoryId() != null) this.categoryId = event.getCategoryId();
        if (event.getActive() != null) this.active = event.getActive();
    }

    @EventSourcingHandler
    public void on(ProductDeletedEvent event) {
        this.deleted = true;
        AggregateLifecycle.markDeleted();
    }
}

