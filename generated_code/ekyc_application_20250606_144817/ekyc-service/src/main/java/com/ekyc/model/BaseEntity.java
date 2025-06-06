package com.ekyc.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base entity class that provides common audit fields for all entities.
 * This class implements common fields like creation timestamp, last update timestamp,
 * and the users who created or last modified the entity.
 *
 * @author eKYC Team
 * @version 1.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * Gets the unique identifier of the entity.
     *
     * @return The entity's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the entity.
     *
     * @param id The entity's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the timestamp when the entity was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the entity was created.
     *
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the entity was last updated.
     *
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the entity was last updated.
     *
     * @param updatedAt The last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the identifier of the user who created the entity.
     *
     * @return The creator's identifier
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the identifier of the user who created the entity.
     *
     * @param createdBy The creator's identifier
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the identifier of the user who last updated the entity.
     *
     * @return The last modifier's identifier
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the identifier of the user who last updated the entity.
     *
     * @param updatedBy The last modifier's identifier
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}