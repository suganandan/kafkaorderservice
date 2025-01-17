package com.suga.kafkaorderservice.service;


import com.suga.kafkaorderservice.api.APIRequestTemplate;
import com.suga.kafkaorderservice.entity.Orders;
import com.suga.kafkaorderservice.entity.Product;
import com.suga.kafkaorderservice.exception.ResourceNotFoundException;
import com.suga.kafkaorderservice.repo.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final APIRequestTemplate apiRequestTemplate;
    private final KafkaService kafkaService;

    public Orders createOrder(Orders orders) throws Exception {

        log.info("Product Id {}", orders.getProductId());
        Product product = apiRequestTemplate.getProduct(orders.getProductId());
        if (Objects.isNull(product)) {
            throw new ResourceNotFoundException("No Product Found for given productId");
        }
        Orders newOrderObj = ordersRepository.save(orders);
        product.setQty(product.getQty() + newOrderObj.getQuantity());
        log.info("Product Details :{} ", product.toString());
        kafkaService.sendProductNotification(product);

        return newOrderObj;
    }

    public Orders fetchOrderById(Long orderId) throws Exception {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order Not Found for ID: {}", orderId);
                    return new ResourceNotFoundException("Order Not Found for ID: " + orderId);
                });
    }

    public List<Orders> fetchAllOrders() {
        try {
            Optional<List<Orders>> ordersOptional = Optional.of(ordersRepository.findAll());

            return ordersOptional
                    .filter(orders -> !orders.isEmpty())
                    .map(orders -> {
                        log.info("Fetched {} orders from the system.", orders.size());
                        return orders;
                    })
                    .orElseGet(() -> {
                        log.warn("No orders found in the system.");
                        return new ArrayList<>();
                    });

        } catch (Exception exception) {
            log.error("An error occurred while fetching all orders: {}", exception.getMessage(), exception);
            throw new RuntimeException("Failed to fetch orders", exception);
        }
    }

    public Orders updateOrder(Orders orders) {
        try {
            return Optional.of(ordersRepository.findById(orders.getId()))
                    .filter(Optional::isPresent)
                    .map(order -> {
                        log.info("Updating order with ID: {}", orders.getId());
                        return ordersRepository.save(orders);
                    })
                    .orElseThrow(() -> {
                        log.error("Order with ID: {} not found for update.", orders.getId());
                        return new ResourceNotFoundException("Order with ID: " + orders.getId() + " does not exist.");
                    });
        } catch (Exception exception) {
            log.error("Error occurred while updating order with ID: {}: {}", orders.getId(), exception.getMessage(), exception);
            throw new RuntimeException("Failed to update the order", exception);
        }


    }
}
