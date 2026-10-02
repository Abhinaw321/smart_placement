package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.student.StudentAcademicUpdateDto;
import com.smartplacement.dto.student.StudentCertificationDto;
import com.smartplacement.dto.student.StudentPersonalInfoUpdateDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.dto.student.StudentProjectDto;
import com.smartplacement.dto.student.StudentSkillDto;
import com.smartplacement.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for student profile, academic records, portfolio, and resume operations.
 */
public interface StudentService {

    StudentProfileResponseDto getMyProfile(UserPrincipal principal);

    StudentProfileResponseDto updateMyPersonalInfo(UserPrincipal principal, StudentPersonalInfoUpdateDto request);

    StudentProfileResponseDto updateMyAcademicInfo(UserPrincipal principal, StudentAcademicUpdateDto request);

    StudentProfileResponseDto getStudentById(Long studentId, UserPrincipal principal);

    PagedResponse<StudentProfileResponseDto> searchStudents(String branch, Integer gradYear, Double minCgpa, int page, int size, String sortBy, String sortDir);

    StudentSkillDto addSkill(UserPrincipal principal, StudentSkillDto request);

    void deleteSkill(UserPrincipal principal, Long skillId);

    StudentProjectDto addProject(UserPrincipal principal, StudentProjectDto request);

    void deleteProject(UserPrincipal principal, Long projectId);

    StudentCertificationDto addCertification(UserPrincipal principal, StudentCertificationDto request);

    void deleteCertification(UserPrincipal principal, Long certId);

    StudentProfileResponseDto uploadResume(UserPrincipal principal, MultipartFile file);

    Resource downloadMyResume(UserPrincipal principal);

    Resource downloadStudentResume(Long studentId, UserPrincipal principal);

    String getResumeFilename(Long studentId);
}
