package com.smartplacement.repository;

import com.smartplacement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Student entities.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Look up student profile by linked User ID.
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * Look up student profile by unique university roll number.
     */
    Optional<Student> findByRollNumber(String rollNumber);

    /**
     * Check existence by roll number to prevent duplicates.
     */
    boolean existsByRollNumber(String rollNumber);

    /**
     * Look up student profile by linked User ID with skills eagerly fetched.
     */
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.skills WHERE s.user.id = :userId")
    Optional<Student> findByUserIdWithSkills(@Param("userId") Long userId);

    /**
     * Retrieve all students with skills eagerly fetched to prevent LazyInitializationException.
     */
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.skills")
    List<Student> findAllWithSkills();

    /**
     * Flexible multi-criteria search for TPO Admin with pagination.
     */
    @Query("SELECT s FROM Student s WHERE " +
           "(:branch IS NULL OR LOWER(s.branch) = LOWER(:branch)) AND " +
           "(:gradYear IS NULL OR s.graduationYear = :gradYear) AND " +
           "(:minCgpa IS NULL OR s.cgpa >= :minCgpa)")
    Page<Student> searchStudents(
            @Param("branch") String branch,
            @Param("gradYear") Integer gradYear,
            @Param("minCgpa") Double minCgpa,
            Pageable pageable
    );

    long countByIsPlacedTrue();

    @Query("SELECT s.branch, COUNT(s), SUM(CASE WHEN s.isPlaced = true THEN 1L ELSE 0L END) FROM Student s GROUP BY s.branch")
    List<Object[]> getBranchWisePlacementCounts();
}
