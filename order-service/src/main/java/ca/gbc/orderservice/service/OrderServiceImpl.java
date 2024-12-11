package ca.gbc.orderservice.service;

import ca.gbc.orderservice.client.InventoryClient;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.dto.OrderResponse;
import ca.gbc.orderservice.event.OrderPlacedEvent;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional //allows roll back if you want to
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    //Inject inventory client
    private final InventoryClient inventoryClient;

    //Week 14
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;



    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        //check inventory
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .price(orderRequest.price())
                    .skuCode(orderRequest.skuCode())
                    .quantity(orderRequest.quantity())
                    .build();

            orderRepository.save(order);

            //send message to kafka
            OrderPlacedEvent orderPlacedEvent =
                    new OrderPlacedEvent(order.getOrderNumber(),orderRequest.userDetails().email());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order--placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("Complete - Sent OrderPlacedEvent {} to Kafka topic order--placed", orderPlacedEvent);

            return new OrderResponse(order.getId(),order.getOrderNumber(),order.getSkuCode(),
                    order.getPrice(),order.getQuantity());
        }else{
            throw new RuntimeException("Product with skuCode " + orderRequest.skuCode() + "is not in stock");
        }
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.debug("Returning a list products");

        List<Order> products = orderRepository.findAll();
        return products.stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    public Optional<OrderResponse> orderDetail(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToOrderResponse);
    }



    private OrderResponse mapToOrderResponse(Order order){
        return new OrderResponse(
                order.getId(),order.getOrderNumber(),order.getSkuCode(),
                order.getPrice(),order.getQuantity()
        );

    }

    @Override
    public Long updateOrder(Long id, OrderRequest orderRequest) {
        Order updateOrder = orderRepository.findById(id).orElse(null);

        if(updateOrder != null){
            updateOrder.setSkuCode(orderRequest.skuCode());
            updateOrder.setPrice(orderRequest.price());
            updateOrder.setQuantity(orderRequest.quantity());
            return orderRepository.save(updateOrder).getId();
        }

        return id;
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }


}
