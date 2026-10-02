package com.smartplacement.repository;

import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Application entity persistence and workflow queries.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByJobIdAndStudentId(Long jobId, Long studentId);

    boolean existsByJobIdAndStudentId(Long jobId, Long studentId);

    long countByJobId(Long jobId);

    long countByJobIdAndStatus(Long jobId, ApplicationStatus status);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "WHERE a.id = :id")
    Optional<Application> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "WHERE a.job.id = :jobId " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Application> findJobApplicationsWithDetails(
            @Param("jobId") Long jobId,
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "WHERE a.student.id = :studentId " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Application> findStudentApplicationsWithDetails(
            @Param("studentId") Long studentId,
            @Param("status") ApplicationStatus status,
            Pageable pageable
    );
}
