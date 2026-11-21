package br.com.lefmachado.gasstation.controller;

import br.com.lefmachado.gasstation.dto.RefuelingRequest;
import br.com.lefmachado.gasstation.dto.RefuelingResponse;
import br.com.lefmachado.gasstation.service.RefuelingService;
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
@RequestMapping("/api/v1/refuelings")
@RequiredArgsConstructor
public class RefuelingController {

    private final RefuelingService refuelingService;

    @PostMapping
    public ResponseEntity<RefuelingResponse> create(@Valid @RequestBody RefuelingRequest request) {
        RefuelingResponse created = refuelingService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<RefuelingResponse>> findAll() {
        return ResponseEntity.ok(refuelingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefuelingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(refuelingService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefuelingResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RefuelingRequest request) {
        return ResponseEntity.ok(refuelingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        refuelingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
