package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.dto.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrders();

    Long updateOrder(Long id, OrderRequest orderRequest);

    void deleteOrder(Long id);

    Optional<OrderResponse> orderDetail(Long id);


}
