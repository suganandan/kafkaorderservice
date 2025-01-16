package com.suga.kafkaorderservice.controller;


import com.suga.kafkaorderservice.entity.Orders;
import com.suga.kafkaorderservice.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;


    @PostMapping("/orders")
    public ResponseEntity<Object> createOrder(@RequestBody Orders orders) throws Exception {
        return ResponseEntity.ok(ordersService.createOrder(orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> fetchOrderById(@PathVariable(value = "id") Long orderId) throws Exception {
        return ResponseEntity.ok(ordersService.fetchOrderById(orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<Object> fetchAllOrders() throws Exception {
        return ResponseEntity.ok(ordersService.fetchAllOrders());
    }

    @PutMapping("/orders")
    public ResponseEntity<Object> updateOrder(@RequestBody Orders orders) throws Exception {
        return ResponseEntity.ok(ordersService.updateOrder(orders));
    }


}