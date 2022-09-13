package com.pins.ordermanagement.service;

import com.pins.ordermanagement.model.Order;
import com.pins.ordermanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAll(){
        return orderRepository.findAll();
    }

    public Page<Order> findByOrderNumberContains(String orderNumber , int pageNumber, int pageSize){
        Page<Order> orderPage =  orderRepository.findByOrderNumberContainsOrderByIdDesc(orderNumber , PageRequest.of(pageNumber,pageSize));
        return orderPage;
    }

    public Page<Order> findOrderByCustomerEmail(String email, String orderNumber, int pageNumber, int pageSize){
        Page<Order> orderPage =  orderRepository.findByCustomerEmailAndOrderNumberContainsOrderByIdDesc(email, orderNumber, PageRequest.of(pageNumber,pageSize));
        return orderPage;
    }

    public List<Order> findOrderByCustomerEmail(String email){
        List<Order> orderPage =  orderRepository.findByCustomerEmailOrderByIdDesc(email);
        return orderPage;
    }


    public List<Order> findByOrderNumber(String orderNumber){
        List<Order> orderList =  orderRepository.findByOrderNumber(orderNumber);
        return orderList;
    }

    public void save(Order order){
        orderRepository.save(order);
    }


    public Order findById(long id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()){
            return optionalOrder.get();
        }
        return new Order();
    }

    public String generateRandomOrderNumber(){
        long number;
        List<Order> orderList;

        do{
            number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            System.out.println("Number Generated is: "+number);
            orderList = orderRepository.findByOrderNumber(String.valueOf(number));
        }while(orderList!=null && orderList.size()!=0);

        return String.valueOf(number);
    }


    public void deleteById(long id){
        orderRepository.deleteById(id);
    }
}
