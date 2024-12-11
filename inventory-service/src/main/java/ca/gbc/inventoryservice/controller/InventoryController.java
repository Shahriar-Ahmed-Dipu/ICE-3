package ca.gbc.inventoryservice.controller;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    //http:localhost:8083/api/inventory?skuCode=SKU003&quantity=5
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode,@RequestParam Integer quantity){
        return inventoryService.isInStock(skuCode, quantity);
    }

    @GetMapping("/allItems")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getInventories(){
        return inventoryService.getInventories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createInventory(@RequestBody InventoryRequest inventoryRequest){
        inventoryService.createInventory(inventoryRequest);
        return "Inventory creation success";
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable("id") Long id,
                                         @RequestBody InventoryRequest inventoryRequest){

        Long updateInventoryId = inventoryService.updateInventory(id, inventoryRequest);

        //set the location header attribute
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location","/api/inventory" + updateInventoryId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable("id") Long id){
        inventoryService.deleteInventory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
