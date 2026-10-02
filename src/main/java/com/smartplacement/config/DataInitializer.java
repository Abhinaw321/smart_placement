package com.smartplacement.config;

import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Interview;
import com.smartplacement.entity.InterviewStatus;
import com.smartplacement.entity.InterviewType;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobOffer;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.Notification;
import com.smartplacement.entity.NotificationType;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.EligibilityCriteriaRepository;
import com.smartplacement.repository.InterviewRepository;
import com.smartplacement.repository.JobOfferRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.NotificationRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentSkillRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Initializes essential default system data and comprehensive demonstration datasets upon startup.
 * Skips rich demo data seeding in H2 in-memory test databases to ensure strict test isolation.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final StudentRepository studentRepository;
    private final StudentSkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EligibilityCriteriaRepository eligibilityCriteriaRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final JobOfferRepository jobOfferRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    public DataInitializer(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           RecruiterRepository recruiterRepository,
                           StudentRepository studentRepository,
                           StudentSkillRepository skillRepository,
                           JobRepository jobRepository,
                           EligibilityCriteriaRepository eligibilityCriteriaRepository,
                           ApplicationRepository applicationRepository,
                           InterviewRepository interviewRepository,
                           JobOfferRepository jobOfferRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder,
                           DataSource dataSource) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.recruiterRepository = recruiterRepository;
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.eligibilityCriteriaRepository = eligibilityCriteriaRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.jobOfferRepository = jobOfferRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        seedDefaultAdminIfAbsent();

        if (isTestDatabase()) {
            log.debug("Skipping rich demonstration dataset in test database context.");
            return;
        }

        seedDemoDatasetIfAbsent();
    }

    private boolean isTestDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            return url != null && url.contains(":h2:mem:");
        } catch (Exception e) {
            return false;
        }
    }

    private void seedDefaultAdminIfAbsent() {
        String adminEmail = "admin@smartplacement.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    adminEmail,
                    passwordEncoder.encode("Admin@123"),
                    Role.ROLE_TPO_ADMIN,
                    UserStatus.ACTIVE
            );

            userRepository.save(admin);
            log.info("Initialized default TPO Administrator account: {} (Password: Admin@123)", adminEmail);
        }
    }

    private void seedDemoDatasetIfAbsent() {
        String studentEmail = "student@smartplacement.com";
        String recruiterEmail = "recruiter@google.com";

        if (userRepository.existsByEmail(studentEmail) && userRepository.existsByEmail(recruiterEmail)) {
            log.debug("Demo recruitment dataset already initialized.");
            return;
        }

        log.info("Seeding rich campus placement demonstration environment...");

        // 1. Seed Verified Companies
        Company google = new Company(
                "Google LLC",
                "Cloud, Search & Generative AI Systems",
                "https://careers.google.com",
                "Technology",
                null,
                "Bangalore"
        );
        google.setVerified(true);
        google = companyRepository.save(google);

        Company microsoft = new Company(
                "Microsoft Corporation",
                "Cloud Infrastructure & Developer Tools",
                "https://careers.microsoft.com",
                "Enterprise Software",
                null,
                "Hyderabad"
        );
        microsoft.setVerified(true);
        microsoft = companyRepository.save(microsoft);

        Company stripe = new Company(
                "Stripe Inc",
                "Financial Technology & Global Payments",
                "https://stripe.com/jobs",
                "Fintech",
                null,
                "Remote"
        );
        stripe.setVerified(false); // Pending verification for TPO admin demonstration!
        companyRepository.save(stripe);

        // 2. Seed Recruiter Account
        User recruiterUser = new User(
                recruiterEmail,
                passwordEncoder.encode("Recruiter@123"),
                Role.ROLE_RECRUITER,
                UserStatus.ACTIVE
        );
        recruiterUser = userRepository.save(recruiterUser);

        Recruiter recruiter = new Recruiter(
                recruiterUser,
                google,
                "Head of University Talent Acquisition",
                "+91 9876543210"
        );
        recruiter = recruiterRepository.save(recruiter);

        // 3. Seed Placement Job Drives
        Job googleJob = new Job(
                google,
                recruiter,
                "Software Engineer - Cloud Systems",
                "Build scalable multi-tenant distributed cloud infrastructure serving billions of queries.",
                JobType.FULL_TIME,
                "Bangalore / Hybrid",
                28.5,
                LocalDateTime.now().plusMonths(3),
                LocalDate.now().plusMonths(1),
                JobStatus.PUBLISHED
        );
        EligibilityCriteria googleCriteria = new EligibilityCriteria(
                googleJob,
                7.5,
                0,
                0,
                70.0,
                70.0,
                1
        );
        googleCriteria.setAllowedBranches(List.of("CSE", "IT", "ECE"));
        googleCriteria.setAllowedGradYears(List.of(2026, 2027));
        googleCriteria.setRequiredSkills(List.of("Java", "Spring Boot", "SQL"));
        googleJob.setEligibilityCriteria(googleCriteria);
        googleJob = jobRepository.save(googleJob);

        Job msftJob = new Job(
                microsoft,
                recruiter,
                "Software Engineer - Full Stack Web",
                "Design modern developer-facing enterprise web experiences using React, TypeScript, and Azure.",
                JobType.FULL_TIME,
                "Hyderabad",
                24.0,
                LocalDateTime.now().plusMonths(2),
                LocalDate.now().plusWeeks(3),
                JobStatus.PUBLISHED
        );
        EligibilityCriteria msftCriteria = new EligibilityCriteria(
                msftJob,
                7.0,
                0,
                1,
                65.0,
                65.0,
                1
        );
        msftCriteria.setAllowedBranches(List.of("CSE", "IT"));
        msftCriteria.setAllowedGradYears(List.of(2026, 2027));
        msftCriteria.setRequiredSkills(List.of("React", "TypeScript", "Node.js"));
        msftJob.setEligibilityCriteria(msftCriteria);
        jobRepository.save(msftJob);

        // 4. Seed Primary Demo Student (Alex Rivera)
        User studentUser = new User(
                studentEmail,
                passwordEncoder.encode("Student@123"),
                Role.ROLE_STUDENT,
                UserStatus.ACTIVE
        );
        studentUser = userRepository.save(studentUser);

        Student alex = new Student(
                studentUser,
                "2023CS0101",
                "Alex",
                "Rivera",
                "+91 9123456780",
                "MALE",
                "CSE",
                2026,
                8.75
        );
        alex.setTenthPercentage(92.5);
        alex.setTwelfthPercentage(90.0);
        alex.setActiveBacklogs(0);
        alex.setHistoryBacklogs(0);
        alex.setGapYears(0);
        alex.setResumeUrl("/uploads/resumes/alex_rivera_sample_resume.pdf");
        alex.setResumeFilename("alex_rivera_sample_resume.pdf");
        alex.setIsPlaced(false);
        alex = studentRepository.save(alex);

        // Add skills for Alex
        skillRepository.save(new StudentSkill(alex, "Java", SkillProficiency.EXPERT));
        skillRepository.save(new StudentSkill(alex, "Spring Boot", SkillProficiency.ADVANCED));
        skillRepository.save(new StudentSkill(alex, "React", SkillProficiency.ADVANCED));
        skillRepository.save(new StudentSkill(alex, "PostgreSQL", SkillProficiency.ADVANCED));
        skillRepository.save(new StudentSkill(alex, "Docker", SkillProficiency.INTERMEDIATE));

        // 5. Seed Placed Student (Priya Sharma)
        User priyaUser = new User(
                "priya.sharma@campus.edu",
                passwordEncoder.encode("Student@123"),
                Role.ROLE_STUDENT,
                UserStatus.ACTIVE
        );
        priyaUser = userRepository.save(priyaUser);

        Student priya = new Student(
                priyaUser,
                "2023IT0142",
                "Priya",
                "Sharma",
                "+91 9988776655",
                "FEMALE",
                "IT",
                2026,
                9.20
        );
        priya.setTenthPercentage(95.0);
        priya.setTwelfthPercentage(94.0);
        priya.setIsPlaced(true);
        studentRepository.save(priya);

        // 6. Seed Application & Scheduled Interview for Alex Rivera
        Application application = new Application(
                googleJob,
                alex,
                alex.getResumeUrl()
        );
        application.setStatus(ApplicationStatus.TECHNICAL_INTERVIEW);
        application.setCurrentRound("Technical Round 1");
        application = applicationRepository.save(application);

        Interview interview = new Interview(
                application,
                1,
                "Technical System Design & Algorithms",
                InterviewType.ONLINE_MEET,
                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
                "https://meet.google.com/spms-recruitment-demo",
                "Sarah Jenkins",
                recruiterEmail
        );
        interviewRepository.save(interview);

        // 7. Seed Job Offer for Alex Rivera
        JobOffer offer = new JobOffer(
                application,
                alex,
                googleJob,
                28.5,
                "Software Engineer - Cloud Systems",
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                LocalDate.now().plusMonths(3),
                null,
                "Congratulations Alex! We were thoroughly impressed by your system architecture acumen."
        );
        offer = jobOfferRepository.save(offer);

        // 8. Seed Welcome Notifications
        Notification notif1 = new Notification(
                studentUser,
                "Application Shortlisted!",
                "Your application for Google LLC (Software Engineer - Cloud Systems) has been shortlisted.",
                NotificationType.APPLICATION_UPDATE,
                "APPLICATION",
                application.getId()
        );
        notificationRepository.save(notif1);

        Notification notif2 = new Notification(
                studentUser,
                "Official Placement Offer Extended!",
                "Google LLC has issued you an official placement offer of 28.5 LPA. Please review and respond.",
                NotificationType.OFFER_RECEIVED,
                "JOB_OFFER",
                offer.getId()
        );
        notificationRepository.save(notif2);

        log.info("Successfully seeded demo campus environment (Alex Rivera, Google LLC, Active Drives & Offers).");
    }
}
