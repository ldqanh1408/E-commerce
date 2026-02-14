package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses", schema = "public")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @jakarta.validation.constraints.Size(max = 255)
    @jakarta.validation.constraints.NotNull
    @Column(name = "street", nullable = false)
    private String street;

    @jakarta.validation.constraints.Size(max = 100)
    @jakarta.validation.constraints.NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @jakarta.validation.constraints.Size(max = 100)
    @jakarta.validation.constraints.NotNull
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @jakarta.validation.constraints.Size(max = 20)
    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}