package com.smartplacement.service;

import com.smartplacement.dto.audit.AuditLogResponseDto;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.security.UserPrincipal;

/**
 * Service contract managing immutable compliance logs and audit trails.
 */
public interface AuditLogService {

    void logAction(Long actorId, String actorEmail, String actorRole, String action,
                   String entityName, Long entityId, String details, String ipAddress);

    void logAction(UserPrincipal actor, String action, String entityName, Long entityId, String details);

    PagedResponse<AuditLogResponseDto> getAuditLogs(int page, int size, String action);
}
