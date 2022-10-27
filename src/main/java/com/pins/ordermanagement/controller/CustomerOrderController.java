package com.pins.ordermanagement.controller;


import com.pins.ordermanagement.model.Order;
import com.pins.ordermanagement.model.User;
import com.pins.ordermanagement.security.AuthSuccessHandler;
import com.pins.ordermanagement.security.MyUserPrincipal;
import com.pins.ordermanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                        @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(value = "size", required = false, defaultValue = "5") int pageSize){


        MyUserPrincipal myUserPrincipal = authSuccessHandler.getLoginInUserPrincipal();

        if(myUserPrincipal==null){
            return "accessDenied";
        }

        String role = myUserPrincipal.getAuthorities().toString();
        Page<Order> orderPage = null;

        if(role.contains("USER")){
            orderPage = orderService.findOrderByCustomerEmail(myUserPrincipal.getUsername(),orderNumber.trim(),pageNumber,pageSize);
        }else if(role.contains("ADMIN")){
            orderPage = orderService.findOrderByCustomerEmail("",orderNumber.trim(),pageNumber,pageSize);
        }
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("pages", new int[orderPage.getTotalPages()]);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("orderNumber", orderNumber);
        return "customer-index";
    }

}
