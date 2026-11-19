package br.com.lefmachado.gasstation.controller;

import br.com.lefmachado.gasstation.dto.FuelTypeRequest;
import br.com.lefmachado.gasstation.dto.FuelTypeResponse;
import br.com.lefmachado.gasstation.service.FuelTypeService;
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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @PostMapping
    public ResponseEntity<FuelTypeResponse> create(@RequestBody FuelTypeRequest request) {
        FuelTypeResponse created = fuelTypeService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeResponse>> findAll() {
        return ResponseEntity.ok(fuelTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelTypeService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelTypeResponse> update(
            @PathVariable Long id,
            @RequestBody FuelTypeRequest request) {
        return ResponseEntity.ok(fuelTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}