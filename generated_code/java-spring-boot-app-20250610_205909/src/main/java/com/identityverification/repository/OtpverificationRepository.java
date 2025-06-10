package com.identityverification.repository;

import com.identityverification.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Entity entity.
 * 
 * Provides CRUD operations and custom query methods for Entity.
 * Follows Spring Data JPA conventions and best practices.
 */
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {

    /**
     * Find Entity by name.
     * 
     * @param name the name to search for
     * @return Optional containing the Entity if found
     */
    Optional<Entity> findByName(String name);

    /**
     * Find all Entitys containing the given name (case-insensitive).
     * 
     * @param name the name pattern to search for
     * @return List of matching Entitys
     */
    @Query("SELECT e FROM Entity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Entity> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Check if Entity exists by name.
     * 
     * @param name the name to check
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Count Entitys by name pattern.
     * 
     * @param namePattern the name pattern to count
     * @return count of matching Entitys
     */
    @Query("SELECT COUNT(e) FROM Entity e WHERE e.name LIKE :namePattern")
    long countByNamePattern(@Param("namePattern") String namePattern);
}
