package com.identityverification.controller;

import com.identityverification.model.Entity;
import com.identityverification.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for Entity operations.
 */
@RestController
@RequestMapping("/api/v1/entitys")
public class EntityController {

    private static final Logger logger = LoggerFactory.getLogger(EntityController.class);
    private final EntityService entityService;

    @Autowired
    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @PostMapping
    public ResponseEntity<Entity> create(@Valid @RequestBody Entity entity) {
        Entity created = entityService.create(entity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Entity>> getAll() {
        List<Entity> entitys = entityService.findAll();
        return new ResponseEntity<>(entitys, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entity> getById(@PathVariable Long id) {
        Optional<Entity> entity = entityService.findById(id);
        return entity.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entity> update(@PathVariable Long id, @Valid @RequestBody Entity entity) {
        Entity updated = entityService.update(id, entity);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        entityService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
