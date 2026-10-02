package com.smartplacement.repository;

import com.smartplacement.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for AuditLog entity records.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<AuditLog> findByActorEmailOrderByTimestampDesc(String actorEmail, Pageable pageable);

    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId, Pageable pageable);
}
