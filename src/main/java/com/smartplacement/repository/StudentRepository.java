package com.smartplacement.repository;

import com.smartplacement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
