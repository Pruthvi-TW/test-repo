package com.ekyc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an audit log entry for tracking system activities.
 * This entity stores detailed information about actions performed in the system,
 * including the actor, action type, and relevant context information.
 *
 * @author eKYC Team
 * @version 1.0
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of possible audit event types.
     */
    public enum EventType {
        EKYC_REQUEST_CREATED,
        EKYC_REQUEST_UPDATED,
        OTP_VERIFICATION_INITIATED,
        OTP_VERIFICATION_SUCCEEDED,
        OTP_VERIFICATION_FAILED,
        EXTERNAL_API_CALL,
        EXTERNAL_API_RESPONSE,
        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        SYSTEM_ERROR
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    @NotNull(message = "Event type is required")
    private EventType eventType;

    @Column(name = "event_timestamp", nullable = false)
    @NotNull(message = "Event timestamp is required")
    private LocalDateTime eventTimestamp;

    @Column(name = "actor", length = 100)
    private String actor;

    @Column(name = "action", nullable = false, length = 255)
    @NotBlank(message = "Action is required")
    @Size(max = 255, message = "Action cannot exceed 255 characters")
    private String action;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "request_details", length = 4000)
    private String requestDetails;

    @Column(name = "response_details", length = 4000)
    private String responseDetails;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * Gets the type of event being audited.
     *
     * @return The event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets the type of event being audited.
     *
     * @param eventType The event type
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the timestamp when the event occurred.
     *
     * @return The event timestamp
     */
    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    /**
     * Sets the timestamp when the event occurred.
     *
     * @param eventTimestamp The event timestamp
     */
    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    /**
     * Gets the actor who performed the action.
     *
     * @return The actor
     */
    public String getActor() {
        return actor;
    }

    /**
     * Sets the actor who performed the action.
     *
     * @param actor The actor
     */
    public void setActor(String actor) {
        this.actor = actor;
    }

    /**
     * Gets the description of the action performed.
     *
     * @return The action description
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the description of the action performed.
     *
     * @param action The action description
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the reference ID related to this audit event.
     *
     * @return The reference ID
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the reference ID related to this audit event.
     *
     * @param referenceId The reference ID
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Gets the session ID in which this event occurred.
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID in which this event occurred.
     *
     * @param sessionId The session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the IP address from which the action was performed.
     *
     * @return The IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address from which the action was performed.
     *
     * @param ipAddress The IP address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the user agent information.
     *
     * @return The user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the user agent information.
     *
     * @param userAgent The user agent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Gets the details of the request that triggered this event.
     * PII data should be masked or excluded.
     *
     * @return The request details
     */
    public String getRequestDetails() {
        return requestDetails;
    }

    /**
     * Sets the details of the request that triggered this event.
     * PII data should be masked or excluded.
     *
     * @param requestDetails The request details
     */
    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    /**
     * Gets the details of the response for this event.
     * PII data should be masked or excluded.
     *
     * @return The response details
     */
    public String getResponseDetails() {
        return responseDetails;
    }

    /**
     * Sets the details of the response for this event.
     * PII data should be masked or excluded.
     *
     * @param responseDetails The response details
     */
    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }

    /**
     * Gets the status of the event.
     *
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the event.
     *
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the error message if the event resulted in an error.
     *
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message if the event resulted in an error.
     *
     * @param errorMessage The error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(getId(), auditLog.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + getId() +
                ", eventType=" + eventType +
                ", eventTimestamp=" + eventTimestamp +
                ", action='" + action + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}