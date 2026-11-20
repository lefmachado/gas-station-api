package br.com.lefmachado.gasstation.controller;

import br.com.lefmachado.gasstation.dto.FuelPumpRequest;
import br.com.lefmachado.gasstation.dto.FuelPumpResponse;
import br.com.lefmachado.gasstation.service.FuelPumpService;
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
@RequestMapping("/api/v1/fuel-pumps")
@RequiredArgsConstructor
public class FuelPumpController {

    private final FuelPumpService fuelPumpService;

    @PostMapping
    public ResponseEntity<FuelPumpResponse> create(@RequestBody FuelPumpRequest request) {
        FuelPumpResponse created = fuelPumpService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FuelPumpResponse>> findAll() {
        return ResponseEntity.ok(fuelPumpService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelPumpResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelPumpService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelPumpResponse> update(
            @PathVariable Long id,
            @RequestBody FuelPumpRequest request) {
        return ResponseEntity.ok(fuelPumpService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelPumpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}