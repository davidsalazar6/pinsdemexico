package com.pins.ordermanagement.repository;

import com.pins.ordermanagement.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order , Long> {


    Page<Order> findByOrderNumberContainsOrderByIdDesc(String orderNumber , Pageable pageable);

    List<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerEmailAndOrderNumberContainsOrderByIdDesc(String customerEmail, String OrderNumber, Pageable pageable);

    List<Order> findByCustomerEmailOrderByIdDesc(String email);
}
