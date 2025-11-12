package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Immutable // Chỉ đọc, không được cập nhật
@FieldDefaults(level = AccessLevel.PRIVATE) // y cho privetha
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String productId;
    String productName;
    BigDecimal productPrice;

}
