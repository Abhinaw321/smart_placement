I want you to help me build a real-world, production-style **Smart Placement Management System** for a college/university.

IMPORTANT:
Do NOT create a basic tutorial-level CRUD project.
The project should look like a serious final-year/portfolio project that demonstrates strong **Java, Spring Boot, REST API, MySQL, authentication, role-based access control, database design, and software engineering** skills.

## 1. PROJECT OBJECTIVE

Build a centralized platform where:

- Students can manage their placement profile and apply for jobs.
- TPO/Admin can manage companies, eligibility criteria, applications, interviews, and placement statistics.
- Recruiters can create job openings, view eligible candidates, shortlist candidates, and update recruitment status.

The system should solve the real-world problem of colleges managing placement activities through spreadsheets, WhatsApp messages, forms, and disconnected systems.

## 2. USER ROLES

Implement three major roles:

### STUDENT
Features:
- Register/Login
- JWT authentication
- Complete profile
- Personal information
- Education details
- CGPA
- Backlog information
- Skills
- Certifications
- Projects
- Resume upload
- View available companies
- Check eligibility
- Apply for eligible jobs
- Track application status
- View test/interview schedules
- View interview results
- View offers
- Placement history/dashboard

### TPO / ADMIN
Features:
- Secure admin login
- Dashboard
- Manage students
- Manage recruiters/companies
- Add/edit/delete companies
- Create job drives
- Define eligibility criteria
- Automatically calculate eligible students
- View applicants
- Shortlist/reject candidates
- Schedule tests
- Schedule interviews
- Update results
- Track offers
- Generate placement statistics
- Export reports
- View audit logs

### RECRUITER
Features:
- Recruiter registration/login
- Company profile
- Create job openings
- Define requirements
- Define salary/package
- Define eligibility criteria
- View eligible candidates
- View candidate profiles/resumes
- Shortlist candidates
- Reject candidates
- Schedule interviews
- Update candidate status
- View recruitment analytics

## 3. MOST IMPORTANT FEATURE — ELIGIBILITY ENGINE

Build an actual eligibility engine instead of simply displaying job listings.

Example:

Company requirements:

- Minimum CGPA: 7.5
- Maximum backlogs: 0
- Branch: CSE / IT
- Required skills: Java, SQL
- Graduation year: 2028

The backend should automatically evaluate student profiles against these requirements.

Example:

Student:
- CGPA: 8.1
- Backlogs: 0
- Branch: CSE
- Skills: Java, SQL, Python
- Graduation Year: 2028

Result:

ELIGIBLE

Another student who fails one or more mandatory criteria should receive:

NOT ELIGIBLE

The system should also explain WHY a student is not eligible.

Example:

"Not eligible because CGPA requirement is 7.5 and your CGPA is 6.9."

Design this eligibility system cleanly so that new criteria can be added later without rewriting the entire application.

## 4. APPLICATION WORKFLOW

Implement a proper recruitment workflow:

JOB CREATED
↓
ELIGIBILITY CALCULATION
↓
STUDENT APPLICATION
↓
APPLICATION REVIEW
↓
SHORTLISTED
↓
ONLINE TEST
↓
TECHNICAL INTERVIEW
↓
HR INTERVIEW
↓
SELECTED / REJECTED
↓
OFFER

Every status change should be stored in the database.

## 5. DASHBOARDS

### Student Dashboard
Show:
- Profile completion percentage
- Eligible jobs
- Applied jobs
- Application status
- Upcoming tests
- Upcoming interviews
- Offers
- Placement statistics

### TPO Dashboard
Show:
- Total students
- Eligible students
- Active companies
- Active job drives
- Applications
- Shortlisted students
- Interviews
- Offers
- Overall placement percentage

Include charts for:
- Department-wise placements
- Company-wise hiring
- Package distribution
- Monthly placement activity

### Recruiter Dashboard
Show:
- Active job openings
- Total applicants
- Eligible candidates
- Shortlisted candidates
- Interviews
- Selected candidates
- Hiring statistics

## 6. TECH STACK

Backend:
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Hibernate
- Maven

Database:
- MySQL

Frontend:
- React
- Tailwind CSS

Tools:
- Git
- GitHub
- Postman

API:
- RESTful APIs
- JSON

## 7. SECURITY

Implement proper security:

- JWT authentication
- Password hashing using BCrypt
- Role-based authorization
- Student/Admin/Recruiter permissions
- Protected endpoints
- Input validation
- Global exception handling
- Secure file upload
- Prevent unauthorized access to resumes and student data

Do NOT store passwords in plain text.

## 8. DATABASE DESIGN

Design a normalized relational database.

Possible entities:

- User
- Student
- Education
- Skill
- Certification
- Project
- Resume
- Company
- Recruiter
- Job
- EligibilityCriteria
- Application
- Shortlist
- Test
- Interview
- Offer
- Notification
- AuditLog

Define proper relationships between entities.

Before coding, provide:

1. ER diagram description
2. Database schema
3. Primary keys
4. Foreign keys
5. Relationships
6. Important indexes
7. Constraints

## 9. API DESIGN

Create clean REST APIs.

Examples:

POST /api/auth/register
POST /api/auth/login

GET /api/students/profile
PUT /api/students/profile

GET /api/jobs
GET /api/jobs/{id}

POST /api/jobs
PUT /api/jobs/{id}

GET /api/jobs/{id}/eligibility
POST /api/jobs/{id}/apply

GET /api/applications
PUT /api/applications/{id}/status

POST /api/interviews
PUT /api/interviews/{id}

GET /api/admin/dashboard

Do not blindly follow these exact endpoints if a better REST architecture is appropriate. Explain your API design decisions.

## 10. NOTIFICATION SYSTEM

Implement notifications for:

- New eligible job
- Application submitted
- Application shortlisted
- Test scheduled
- Interview scheduled
- Result published
- Offer received

Start with in-app notifications.

Design the system so email notifications can be added later.

## 11. RESUME SYSTEM

Students should be able to upload their resume.

Store:
- File metadata
- Upload date
- Student ID
- File path/storage reference

Allow authorized recruiters/TPOs to view/download resumes.

Do not expose resume files publicly.

## 12. AUDIT LOGGING

Create an audit system.

Track important actions such as:

- Login
- Job creation
- Job update
- Application
- Shortlisting
- Rejection
- Interview scheduling
- Result update
- Offer creation
- Admin actions

Store:
- User
- Action
- Timestamp
- Entity affected
- Previous status
- New status

## 13. CODE QUALITY

Follow professional development practices:

- Clean architecture
- Controller → Service → Repository structure
- DTOs instead of exposing entities directly
- Proper validation
- Global exception handling
- Meaningful naming
- Avoid duplicated code
- Proper HTTP status codes
- Environment variables
- Configuration separation
- Logging
- Pagination
- Sorting
- Filtering

Do NOT put business logic directly inside controllers.

## 14. DEVELOPMENT APPROACH

DO NOT generate the entire project blindly in one response.

Build it incrementally.

First give me:

PHASE 1:
- Complete architecture
- Folder structure
- Database ER design
- Entity relationships
- API plan
- Authentication architecture
- Feature roadmap

Then wait for my confirmation.

After confirmation, build:

PHASE 2:
- Spring Boot project setup
- Maven dependencies
- Configuration
- Database connection
- Base architecture

PHASE 3:
- Authentication
- JWT
- User roles
- Spring Security

PHASE 4:
- Student module

PHASE 5:
- Company/Recruiter module

PHASE 6:
- Job and eligibility engine

PHASE 7:
- Application workflow

PHASE 8:
- Tests and interviews

PHASE 9:
- Offers and notifications

PHASE 10:
- Admin/TPO dashboard

PHASE 11:
- React frontend

PHASE 12:
- Integration and testing

PHASE 13:
- Deployment

## 15. GITHUB / RESUME QUALITY

The final project should be GitHub-ready.

Create:
- Professional README
- Architecture diagram
- ER diagram
- API documentation
- Screenshots
- Setup instructions
- Environment variable documentation
- Database setup instructions
- Postman collection
- Future improvements

The final project should be something I can confidently discuss in a Java/SDE placement interview.

## 16. IMPORTANT CONSTRAINT

I am currently learning Java.

Therefore, whenever you introduce a new Java/Spring Boot concept, explain:
1. What it is
2. Why we need it
3. How it works
4. Then show the code

Do not assume that I already know advanced Java or Spring Boot.

Teach me while building the project.

## START NOW

Start with ONLY:

1. Project architecture
2. Complete feature list
3. User roles and permissions
4. Database/ER design
5. Backend folder structure
6. API architecture
7. Development roadmap
8. Recommended MVP vs advanced features

Do NOT write the complete implementation yet.
First make the architecture production-quality.