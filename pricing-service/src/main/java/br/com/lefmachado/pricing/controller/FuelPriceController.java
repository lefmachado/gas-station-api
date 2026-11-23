package br.com.lefmachado.pricing.controller;

import br.com.lefmachado.pricing.dto.FuelPriceRequest;
import br.com.lefmachado.pricing.dto.FuelPriceResponse;
import br.com.lefmachado.pricing.service.FuelPriceService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class FuelPriceController {

    private final FuelPriceService fuelPriceService;

    @PostMapping
    public ResponseEntity<FuelPriceResponse> create(@Valid @RequestBody FuelPriceRequest request) {
        FuelPriceResponse created = fuelPriceService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FuelPriceResponse>> findAll() {
        return ResponseEntity.ok(fuelPriceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelPriceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelPriceService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelPriceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelPriceRequest request) {
        return ResponseEntity.ok(fuelPriceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelPriceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
