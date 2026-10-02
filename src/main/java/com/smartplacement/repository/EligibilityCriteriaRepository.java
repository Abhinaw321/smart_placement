package com.smartplacement.repository;

import com.smartplacement.entity.EligibilityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for EligibilityCriteria persistence.
 */
@Repository
public interface EligibilityCriteriaRepository extends JpaRepository<EligibilityCriteria, Long> {

    Optional<EligibilityCriteria> findByJobId(Long jobId);
}
