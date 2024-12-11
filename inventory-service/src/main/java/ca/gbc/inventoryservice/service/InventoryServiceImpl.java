package ca.gbc.inventoryservice.service;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;


    @Override
    public boolean isInStock(String skuCode, Integer quantity){
        return inventoryRepository.existsBySkuCodeAndQuantityGreaterThanEqual(skuCode, quantity);
    }

    @Override
    public void createInventory(InventoryRequest inventoryRequest) {

        boolean isSkuCodeEmpty = inventoryRequest.skuCode().isEmpty();

        if(isSkuCodeEmpty) {
            Inventory inventory = Inventory.builder()
                    .skuCode(generateSkuCode())
                    .quantity(inventoryRequest.quantity())
                    .build();

            inventoryRepository.save(inventory);
        }else if(!isSkuCodeEmpty){
            Inventory inventory = Inventory.builder()
                    .skuCode(inventoryRequest.skuCode())
                    .quantity(inventoryRequest.quantity())
                    .build();

            inventoryRepository.save(inventory);
        }else{
            throw new RuntimeException("Product with skuCode " +
                    inventoryRequest.skuCode() + "fail to create in inventory");
        }

    }

    private String generateSkuCode(){
        return "SKU" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    @Override
    public List<InventoryResponse> getInventories() {
        List<Inventory> products = inventoryRepository.findAll();
        return products.stream().map(this::mapToInventoryResponse).toList();
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory){
        return new InventoryResponse(
                inventory.getId(),inventory.getSkuCode(),
                inventory.getQuantity()
        );

    }

    @Override
    public Long updateInventory(Long id, InventoryRequest inventoryRequest) {
        Inventory updateInventory = inventoryRepository.findById(id).orElse(null);

        if(updateInventory != null){
            boolean isSkuCodeEmpty = inventoryRequest.skuCode().isEmpty();
            if(isSkuCodeEmpty) {
                updateInventory.setQuantity(inventoryRequest.quantity());
                return inventoryRepository.save(updateInventory).getId();
            }else{
                updateInventory.setSkuCode(inventoryRequest.skuCode());
                updateInventory.setQuantity(inventoryRequest.quantity());
                return inventoryRepository.save(updateInventory).getId();
            }
        }

        return id;
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }


}
