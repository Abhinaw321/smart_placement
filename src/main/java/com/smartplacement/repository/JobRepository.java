package com.smartplacement.repository;

import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA repository for Job posting persistence and dynamic queries.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    @Query("SELECT j FROM Job j " +
            "JOIN FETCH j.company c " +
            "LEFT JOIN FETCH j.eligibilityCriteria " +
            "WHERE j.id = :id")
    Optional<Job> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT j FROM Job j " +
            "JOIN j.company c " +
            "WHERE (:status IS NULL OR j.status = :status) " +
            "AND (:jobType IS NULL OR j.jobType = :jobType) " +
            "AND (:companyId IS NULL OR c.id = :companyId) " +
            "AND (:searchTerm IS NULL OR :searchTerm = '' OR " +
            "     LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(j.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Job> searchJobs(
            @Param("status") JobStatus status,
            @Param("jobType") JobType jobType,
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT j FROM Job j " +
            "WHERE j.status = 'PUBLISHED' " +
            "AND j.applicationDeadline >= :now")
    Page<Job> findActiveOpenJobs(@Param("now") LocalDateTime now, Pageable pageable);
}
