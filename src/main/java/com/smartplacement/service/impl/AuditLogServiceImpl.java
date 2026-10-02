package com.smartplacement.service.impl;

import com.smartplacement.dto.audit.AuditLogResponseDto;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.entity.AuditLog;
import com.smartplacement.repository.AuditLogRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link AuditLogService}.
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(Long actorId, String actorEmail, String actorRole, String action,
                          String entityName, Long entityId, String details, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog(
                    actorId,
                    actorEmail,
                    actorRole,
                    action,
                    entityName,
                    entityId,
                    details,
                    ipAddress
            );
            auditLogRepository.save(auditLog);
            log.info("AUDIT: [{}] by {} ({}) on {} [ID: {}]", action, actorEmail, actorRole, entityName, entityId);
        } catch (Exception ex) {
            log.error("Failed to persist audit log for action: {}", action, ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(UserPrincipal actor, String action, String entityName, Long entityId, String details) {
        if (actor == null) {
            logAction(null, "SYSTEM", "ROLE_SYSTEM", action, entityName, entityId, details, null);
        } else {
            logAction(
                    actor.getId(),
                    actor.getUsername(),
                    actor.getRole().name(),
                    action,
                    entityName,
                    entityId,
                    details,
                    null
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponseDto> getAuditLogs(int page, int size, String action) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logPage;

        if (action != null && !action.trim().isEmpty()) {
            logPage = auditLogRepository.findByActionOrderByTimestampDesc(action.trim(), pageable);
        } else {
            logPage = auditLogRepository.findAllByOrderByTimestampDesc(pageable);
        }

        List<AuditLogResponseDto> content = logPage.getContent().stream()
                .map(AuditLogResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                logPage.getNumber(),
                logPage.getSize(),
                logPage.getTotalElements(),
                logPage.getTotalPages(),
                logPage.isLast()
        );
    }
}
