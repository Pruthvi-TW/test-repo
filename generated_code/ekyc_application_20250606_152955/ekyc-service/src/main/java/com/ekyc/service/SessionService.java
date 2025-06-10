package com.ekyc.service;

import com.ekyc.exception.SessionExpiredException;
import com.ekyc.model.SessionData;
import com.ekyc.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing user sessions.
 * Handles session creation, validation, and cleanup.
 */
@Service
public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    
    private final SessionRepository sessionRepository;
    private final AuditService auditService;
    
    @Value("${session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;
    
    @Autowired
    public SessionService(SessionRepository sessionRepository, AuditService auditService) {
        this.sessionRepository = sessionRepository;
        this.auditService = auditService;
    }
    
    /**
     * Creates a new session.
     * 
     * @param sessionId The session ID
     * @return The created SessionData
     */
    @Transactional
    public SessionData createSession(String sessionId) {
        logger.info("Creating new session: {}", sessionId);
        
        // Check if session already exists
        sessionRepository.findBySessionId(sessionId).ifPresent(existingSession -> {
            logger.info("Session already exists, updating expiry time: {}", sessionId);
            existingSession.setExpiryTime(calculateExpiryTime());
            sessionRepository.save(existingSession);
        });
        
        // Create new session if it doesn't exist
        SessionData sessionData = new SessionData();
        sessionData.setSessionId(sessionId);
        sessionData.setCreatedAt(LocalDateTime.now());
        sessionData.setExpiryTime(calculateExpiryTime());
        
        sessionRepository.save(sessionData);
        auditService.logInfo("Session created", sessionId, null);
        
        return sessionData;
    }
    
    /**
     * Validates a session.
     * 
     * @param sessionId The session ID to validate
     * @throws SessionExpiredException if the session is expired or doesn't exist
     */
    @Transactional
    public void validateSession(String sessionId) {
        logger.debug("Validating session: {}", sessionId);
        
        SessionData sessionData = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> {
                    auditService.logFailure("Session not found", sessionId, null, "Session does not exist");
                    return new SessionExpiredException("Session does not exist");
                });
        
        if (sessionData.getExpiryTime().isBefore(LocalDateTime.now())) {
            auditService.logFailure("Session expired", sessionId, null, "Session has expired");
            throw new SessionExpiredException("Session has expired");
        }
        
        // Extend session expiry time
        sessionData.setExpiryTime(calculateExpiryTime());
        sessionRepository.save(sessionData);
        
        logger.debug("Session validated and extended: {}", sessionId);
    }
    
    /**
     * Invalidates a session.
     * 
     * @param sessionId The session ID to invalidate
     */
    @Transactional
    public void invalidateSession(String sessionId) {
        logger.info("Invalidating session: {}", sessionId);
        
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            sessionRepository.delete(session);
            auditService.logInfo("Session invalidated", sessionId, null);
        });
    }
    
    /**
     * Calculates the expiry time for a session.
     * 
     * @return The expiry time
     */
    private LocalDateTime calculateExpiryTime() {
        return LocalDateTime.now().plusMinutes(sessionTimeoutMinutes);
    }
    
    /**
     * Scheduled task to clean up expired sessions.
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void cleanupExpiredSessions() {
        logger.info("Running scheduled cleanup of expired sessions");
        
        LocalDateTime now = LocalDateTime.now();
        List<SessionData> expiredSessions = sessionRepository.findByExpiryTimeBefore(now);
        
        if (!expiredSessions.isEmpty()) {
            logger.info("Found {} expired sessions to clean up", expiredSessions.size());
            sessionRepository.deleteAll(expiredSessions);
            auditService.logInfo("Cleaned up " + expiredSessions.size() + " expired sessions", null, null);
        } else {
            logger.info("No expired sessions found");
        }
    }
}