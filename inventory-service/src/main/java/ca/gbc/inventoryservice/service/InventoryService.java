package ca.gbc.inventoryservice.service;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {

    boolean isInStock(String skuCode, Integer quantity);

    void createInventory(InventoryRequest inventoryRequest);
    List<InventoryResponse> getInventories();
    Long updateInventory(Long id, InventoryRequest inventoryRequest);
    void deleteInventory(Long id);

}
