package com.smartplacement.service.impl;

import com.smartplacement.dto.analytics.DepartmentPlacementStatDto;
import com.smartplacement.dto.analytics.RecruiterDashboardDto;
import com.smartplacement.dto.analytics.SalaryDistributionDto;
import com.smartplacement.dto.analytics.StudentDashboardDto;
import com.smartplacement.dto.analytics.TpoDashboardDto;
import com.smartplacement.engine.eligibility.EligibilityEngine;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobOffer;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.OfferStatus;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Student;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.InterviewRepository;
import com.smartplacement.repository.JobOfferRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.AnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Production implementation of {@link AnalyticsService}.
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final JobOfferRepository jobOfferRepository;
    private final EligibilityEngine eligibilityEngine;

    public AnalyticsServiceImpl(StudentRepository studentRepository,
                                RecruiterRepository recruiterRepository,
                                CompanyRepository companyRepository,
                                JobRepository jobRepository,
                                ApplicationRepository applicationRepository,
                                InterviewRepository interviewRepository,
                                JobOfferRepository jobOfferRepository,
                                EligibilityEngine eligibilityEngine) {
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.jobOfferRepository = jobOfferRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardDto getStudentDashboard(UserPrincipal currentUser) {
        Student student = studentRepository.findByUserIdWithSkills(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId()));

        long totalApps = applicationRepository.countByStudentId(student.getId());
        long shortlisted = applicationRepository.countByStudentIdAndStatus(student.getId(), ApplicationStatus.SHORTLISTED);
        long interviews = interviewRepository.countByApplicationStudentId(student.getId());
        long offers = jobOfferRepository.countByStudentId(student.getId());
        boolean isPlaced = Boolean.TRUE.equals(student.getIsPlaced());

        // Profile completion calculation
        int completion = 20; // Basic account created
        if (student.getCgpa() != null && student.getCgpa() > 0.0) completion += 20;
        if (student.getTenthPercentage() != null && student.getTenthPercentage() > 0.0) completion += 10;
        if (student.getResumeUrl() != null && !student.getResumeUrl().isBlank()) completion += 25;
        if (student.getSkills() != null && !student.getSkills().isEmpty()) completion += 25;
        completion = Math.min(100, completion);

        // Eligible jobs count
        Page<Job> openJobs = jobRepository.findActiveOpenJobs(LocalDateTime.now(), PageRequest.of(0, 100));
        long eligibleCount = openJobs.getContent().stream()
                .filter(job -> eligibilityEngine.evaluate(student, job.getEligibilityCriteria()).isEligible())
                .count();

        return new StudentDashboardDto(
                totalApps,
                shortlisted,
                interviews,
                offers,
                isPlaced,
                completion,
                eligibleCount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterDashboardDto getRecruiterDashboard(UserPrincipal currentUser) {
        Recruiter recruiter = recruiterRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + currentUser.getId()));

        Long companyId = recruiter.getCompany().getId();
        String companyName = recruiter.getCompany().getName();

        long totalJobs = jobRepository.countByCompanyId(companyId);
        long activeDrives = jobRepository.countByCompanyIdAndStatus(companyId, JobStatus.PUBLISHED);
        long totalApps = applicationRepository.countByJobCompanyId(companyId);
        long interviews = interviewRepository.countByApplicationJobCompanyId(companyId);
        long offers = jobOfferRepository.countByJobCompanyId(companyId);
        long offersAccepted = jobOfferRepository.countByJobCompanyIdAndStatus(companyId, OfferStatus.ACCEPTED);

        return new RecruiterDashboardDto(
                companyName,
                totalJobs,
                activeDrives,
                totalApps,
                interviews,
                offers,
                offersAccepted
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TpoDashboardDto getTpoDashboard() {
        TpoDashboardDto dto = new TpoDashboardDto();

        long totalStudents = studentRepository.count();
        long placedStudents = studentRepository.countByIsPlacedTrue();
        double placementPct = totalStudents > 0 ? (placedStudents * 100.0) / totalStudents : 0.0;

        dto.setTotalRegisteredStudents(totalStudents);
        dto.setTotalPlacedStudents(placedStudents);
        dto.setOverallPlacementPercentage(Math.round(placementPct * 100.0) / 100.0);

        dto.setTotalRegisteredCompanies(companyRepository.count());
        dto.setTotalVerifiedCompanies(companyRepository.countByVerified(true));
        dto.setTotalJobsPublished(jobRepository.countByStatus(JobStatus.PUBLISHED));

        dto.setTotalOffersExtended(jobOfferRepository.count());
        long acceptedOffers = jobOfferRepository.countByStatus(OfferStatus.ACCEPTED);
        dto.setTotalOffersAccepted(acceptedOffers);

        Double maxCtc = jobOfferRepository.findMaxAcceptedCtc();
        Double avgCtc = jobOfferRepository.findAvgAcceptedCtc();
        dto.setHighestPackageLpa(maxCtc != null ? Math.round(maxCtc * 100.0) / 100.0 : 0.0);
        dto.setAveragePackageLpa(avgCtc != null ? Math.round(avgCtc * 100.0) / 100.0 : 0.0);

        // Median CTC Calculation
        List<Double> ctcList = jobOfferRepository.findAcceptedCtcList();
        if (ctcList.isEmpty()) {
            dto.setMedianPackageLpa(0.0);
        } else {
            int n = ctcList.size();
            double median = (n % 2 != 0) ? ctcList.get(n / 2) : (ctcList.get((n - 1) / 2) + ctcList.get(n / 2)) / 2.0;
            dto.setMedianPackageLpa(Math.round(median * 100.0) / 100.0);
        }

        // Branch-wise placement aggregations
        List<Object[]> branchData = studentRepository.getBranchWisePlacementCounts();
        List<DepartmentPlacementStatDto> deptStats = new ArrayList<>();
        for (Object[] row : branchData) {
            String branch = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Long placed = ((Number) row[2]).longValue();
            double pct = count > 0 ? (placed * 100.0) / count : 0.0;
            deptStats.add(new DepartmentPlacementStatDto(
                    branch != null ? branch : "General",
                    count,
                    placed,
                    Math.round(pct * 100.0) / 100.0,
                    0.0
            ));
        }
        dto.setDepartmentStats(deptStats);

        // Salary distribution tiers
        long tier1 = 0L;
        long tier2 = 0L;
        long tier3 = 0L;
        long tier4 = 0L;
        for (Double ctc : ctcList) {
            if (ctc < 6.0) tier1++;
            else if (ctc <= 12.0) tier2++;
            else if (ctc <= 20.0) tier3++;
            else tier4++;
        }
        dto.setSalaryDistribution(new SalaryDistributionDto(tier1, tier2, tier3, tier4));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPlacementsCsv() {
        List<JobOffer> offers = jobOfferRepository.findAllWithFullDetails();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Write RFC 4180 CSV Header
            writer.println("Offer ID,Roll Number,Student Name,Student Email,Branch,Graduation Year,CGPA,Company,Job Title,Designation,CTC (LPA),Status,Issue Date,Joining Date");

            for (JobOffer offer : offers) {
                Student s = offer.getStudent();
                String rollNo = s != null ? s.getRollNumber() : "N/A";
                String name = s != null ? escapeCsv(s.getFirstName() + " " + s.getLastName()) : "N/A";
                String email = s != null && s.getUser() != null ? s.getUser().getEmail() : "N/A";
                String branch = s != null ? escapeCsv(s.getBranch()) : "N/A";
                String gradYear = s != null && s.getGraduationYear() != null ? s.getGraduationYear().toString() : "N/A";
                String cgpa = s != null && s.getCgpa() != null ? s.getCgpa().toString() : "0.0";
                String company = offer.getJob() != null && offer.getJob().getCompany() != null ? escapeCsv(offer.getJob().getCompany().getName()) : "N/A";
                String jobTitle = offer.getJob() != null ? escapeCsv(offer.getJob().getTitle()) : "N/A";
                String designation = escapeCsv(offer.getDesignation());
                String ctc = offer.getCtcLpa() != null ? offer.getCtcLpa().toString() : "0.0";
                String status = offer.getStatus() != null ? offer.getStatus().name() : "N/A";
                String issueDate = offer.getIssueDate() != null ? offer.getIssueDate().toString() : "N/A";
                String joiningDate = offer.getJoiningDate() != null ? offer.getJoiningDate().toString() : "N/A";

                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        offer.getId(), rollNo, name, email, branch, gradYear, cgpa,
                        company, jobTitle, designation, ctc, status, issueDate, joiningDate);
            }
        }

        return out.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
