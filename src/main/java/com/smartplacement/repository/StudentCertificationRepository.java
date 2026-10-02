package com.smartplacement.repository;

import com.smartplacement.entity.StudentCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCertificationRepository extends JpaRepository<StudentCertification, Long> {
    List<StudentCertification> findByStudentId(Long studentId);
    Optional<StudentCertification> findByIdAndStudentId(Long id, Long studentId);
}
