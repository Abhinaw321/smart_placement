package com.smartplacement.repository;

import com.smartplacement.entity.JobOffer;
import com.smartplacement.entity.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for JobOffer entity operations.
 */
@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    Optional<JobOffer> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);

    long countByStatus(OfferStatus status);

    @Query("SELECT o FROM JobOffer o " +
            "JOIN FETCH o.application a " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "WHERE o.id = :id")
    Optional<JobOffer> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT o FROM JobOffer o " +
            "JOIN FETCH o.application a " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "WHERE s.id = :studentId " +
            "ORDER BY o.createdAt DESC")
    Page<JobOffer> findByStudentIdWithDetails(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT o FROM JobOffer o " +
            "JOIN FETCH o.application a " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "WHERE j.id = :jobId " +
            "ORDER BY o.createdAt DESC")
    Page<JobOffer> findByJobIdWithDetails(@Param("jobId") Long jobId, Pageable pageable);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, OfferStatus status);

    long countByJobCompanyId(Long companyId);

    long countByJobCompanyIdAndStatus(Long companyId, OfferStatus status);

    @Query("SELECT MAX(o.ctcLpa) FROM JobOffer o WHERE o.status = com.smartplacement.entity.OfferStatus.ACCEPTED")
    Double findMaxAcceptedCtc();

    @Query("SELECT AVG(o.ctcLpa) FROM JobOffer o WHERE o.status = com.smartplacement.entity.OfferStatus.ACCEPTED")
    Double findAvgAcceptedCtc();

    @Query("SELECT o.ctcLpa FROM JobOffer o WHERE o.status = com.smartplacement.entity.OfferStatus.ACCEPTED ORDER BY o.ctcLpa ASC")
    java.util.List<Double> findAcceptedCtcList();

    @Query("SELECT o FROM JobOffer o " +
            "JOIN FETCH o.application a " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH a.job j " +
            "JOIN FETCH j.company c " +
            "ORDER BY o.createdAt DESC")
    java.util.List<JobOffer> findAllWithFullDetails();
}
