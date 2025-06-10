package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user session in the eKYC system.
 * This entity stores information about active user sessions, including
 * authentication details, IP address, and session expiration.
 *
 * @author eKYC Team
 * @version 1.0
 */
@Entity
@Table(name = "user