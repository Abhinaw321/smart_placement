package com.smartplacement.repository;

import com.smartplacement.entity.Interview;
import com.smartplacement.entity.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Interview entity operations and candidate round tracking.
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationIdOrderByRoundNumberAsc(Long applicationId);

    long countByApplicationJobIdAndStatus(Long jobId, InterviewStatus status);

    @Query("SELECT i FROM Interview i " +
            "JOIN FETCH i.application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "WHERE i.id = :id")
    Optional<Interview> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT i FROM Interview i " +
            "JOIN FETCH i.application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "WHERE a.student.id = :studentId " +
            "ORDER BY i.scheduledAt DESC")
    Page<Interview> findStudentInterviewsWithDetails(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT i FROM Interview i " +
            "JOIN FETCH i.application a " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "WHERE a.job.id = :jobId " +
            "ORDER BY i.scheduledAt DESC")
    Page<Interview> findJobInterviewsWithDetails(@Param("jobId") Long jobId, Pageable pageable);
}
