package com.identityverification.service;

import com.identityverification.model.Entity;
import com.identityverification.repository.EntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Entity business logic.
 * 
 * Handles all business operations related to Entity entities.
 * Includes proper logging, transaction management, and error handling.
 */
@Service
@Transactional
public class EntityService {

    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private final EntityRepository entityRepository;

    @Autowired
    public EntityService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * Create a new Entity.
     * 
     * @param entity the Entity to create
     * @return the created Entity
     * @throws IllegalArgumentException if entity is null or invalid
     */
    public Entity create(Entity entity) {
        logger.info("Creating new Entity: {}", entity.getName());
        
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        if (entityRepository.existsByName(entity.getName())) {
            throw new IllegalArgumentException("Entity with name '" + entity.getName() + "' already exists");
        }
        
        Entity saved = entityRepository.save(entity);
        logger.info("Successfully created Entity with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * Find Entity by ID.
     * 
     * @param id the ID to search for
     * @return Optional containing the Entity if found
     */
    @Transactional(readOnly = true)
    public Optional<Entity> findById(Long id) {
        logger.debug("Finding Entity by ID: {}", id);
        return entityRepository.findById(id);
    }

    /**
     * Find all Entitys.
     * 
     * @return List of all Entitys
     */
    @Transactional(readOnly = true)
    public List<Entity> findAll() {
        logger.debug("Finding all Entitys");
        return entityRepository.findAll();
    }

    /**
     * Update an existing Entity.
     * 
     * @param id the ID of the Entity to update
     * @param updated the updated Entity data
     * @return the updated Entity
     * @throws IllegalArgumentException if Entity not found
     */
    public Entity update(Long id, Entity updated) {
        logger.info("Updating Entity with ID: {}", id);
        
        Entity existing = entityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity not found with ID: " + id));
        
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        
        Entity saved = entityRepository.save(existing);
        logger.info("Successfully updated Entity with ID: {}", id);
        
        return saved;
    }

    /**
     * Delete Entity by ID.
     * 
     * @param id the ID of the Entity to delete
     * @throws IllegalArgumentException if Entity not found
     */
    public void delete(Long id) {
        logger.info("Deleting Entity with ID: {}", id);
        
        if (!entityRepository.existsById(id)) {
            throw new IllegalArgumentException("Entity not found with ID: " + id);
        }
        
        entityRepository.deleteById(id);
        logger.info("Successfully deleted Entity with ID: {}", id);
    }
}
