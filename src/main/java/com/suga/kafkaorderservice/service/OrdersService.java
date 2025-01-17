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

    public Orders createOrder(final Orders orders) {
        try {
            if (orders == null || orders.getProductId() == null) {
                log.error("Invalid order details provided.");
                throw new IllegalArgumentException("Invalid order details provided.");
            }
            Product product = apiRequestTemplate.getProduct(orders.getProductId());
            if (Objects.isNull(product)) {
                log.error("No Product Found for the given productId: {}", orders.getProductId());
                throw new ResourceNotFoundException("No Product Found for the given productId: " + orders.getProductId());
            }
            final Orders newOrder = ordersRepository.save(orders);
            product.setQty(product.getQty() + newOrder.getQuantity());
            kafkaService.sendProductNotification(product);
            return newOrder;
        } catch (IllegalArgumentException | ResourceNotFoundException illegalArgumentException) {
            log.error("Order creation failed due to invalid input: {}", illegalArgumentException.getMessage());
            throw new ResourceNotFoundException("Order creation failed due to invalid input: " + illegalArgumentException.getMessage());
        } catch (Exception exception) {
            log.error("An unexpected error occurred while creating the order. {}", exception.getMessage());
            throw new ResourceNotFoundException("An unexpected error occurred while creating the order." + exception.getMessage());
        }
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
