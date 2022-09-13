package com.pins.ordermanagement.model;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    @Size(min = 10, max = 10, message = "Order Number should have 10 characters only")
    private String orderNumber;
    private Double price = 0.0;
    private Double height = 0.0;
    private Double weight = 0.0;
    private String photoUrl;
    private String customerEmail;
}
