package ca.gbc.inventoryservice.dto;

public record InventoryRequest(
        Long id,
        String skuCode,
        Integer quantity
) {
}
