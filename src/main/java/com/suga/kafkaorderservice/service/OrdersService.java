package com.suga.kafkaorderservice.service;


import com.suga.kafkaorderservice.api.APIRequestTemplate;
import com.suga.kafkaorderservice.entity.Orders;
import com.suga.kafkaorderservice.entity.Product;
import com.suga.kafkaorderservice.exception.ResourceNotFoundException;
import com.suga.kafkaorderservice.repo.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final APIRequestTemplate apiRequestTemplate;
    private final KafkaService kafkaService;

    public Orders createOrder(Orders orders) throws Exception {


        Product product = apiRequestTemplate.getProduct(orders.getProductId());
        if (Objects.isNull(product)) {
            throw new ResourceNotFoundException("No Product Found for given productId");
        }
        Orders newOrderObj = ordersRepository.save(orders);
        product.setQty(product.getQty() + newOrderObj.getQuantity());
        kafkaService.sendProductNotification(product);

        return newOrderObj;
    }

    public Orders fetchOrderById(Long orderId) throws Exception {
        Optional<Orders> ordersOptional = ordersRepository.findById(orderId);
        if (ordersOptional.isPresent()) {
            return ordersOptional.get();
        } else {
            throw new ResourceNotFoundException("Order Not Found");
        }
    }

    public List<Orders> fetchAllOrders() {
        return ordersRepository.findAll();
    }

    public Orders updateOrder(Orders orders) throws Exception {
        Optional<Orders> ordersOptional = ordersRepository.findById(orders.getId());
        if (ordersOptional.isPresent()) {
            return ordersRepository.save(orders);
        } else {
            throw new ResourceNotFoundException("Order is not Exists.");
        }
    }

}
