package com.suga.kafkaorderservice.controller;


import com.suga.kafkaorderservice.entity.Orders;
import com.suga.kafkaorderservice.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody Orders orders) throws Exception {
        return ResponseEntity.ok(ordersService.createOrder(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchOrderById(@PathVariable(value = "id") Long orderId) throws Exception {
        return ResponseEntity.ok(ordersService.fetchOrderById(orderId));
    }

    @GetMapping
    public ResponseEntity<Object> fetchAllOrders() throws Exception {
        return ResponseEntity.ok(ordersService.fetchAllOrders());
    }

    @PutMapping
    public ResponseEntity<Object> updateOrder(@RequestBody Orders orders) throws Exception {
        return ResponseEntity.ok(ordersService.updateOrder(orders));
    }


}