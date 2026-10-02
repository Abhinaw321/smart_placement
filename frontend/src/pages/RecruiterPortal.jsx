import React, { useState, useEffect } from 'react';
import {
  jobApi,
  applicationApi,
  interviewApi,
  offerApi,
  analyticsApi,
  companyApi,
} from '../services/api';
import StatCard from '../components/StatCard';
import {
  Briefcase,
  Users,
  Calendar,
  Award,
  PlusCircle,
  Clock,
  MapPin,
  DollarSign,
  CheckCircle,
  XCircle,
  AlertCircle,
  ChevronRight,
  ExternalLink,
  Search,
  Filter,
  Send,
  Building2,
  FileText,
  UserCheck,
  Sparkles,
} from 'lucide-react';

export default function RecruiterPortal() {
  const [tab, setTab] = useState('overview'); // overview, postJob, jobs, pipeline, interviews, offers
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [offers, setOffers] = useState([]);
  const [companyProfile, setCompanyProfile] = useState(null);

  // Selected filters
  const [selectedJobFilter, setSelectedJobFilter] = useState('ALL');
  const [selectedStatusFilter, setSelectedStatusFilter] = useState('ALL');

  // Modals state
  const [eligibleModalOpen, setEligibleModalOpen] = useState(false);
  const [eligibleStudents, setEligibleStudents] = useState([]);
  const [currentJobForEligible, setCurrentJobForEligible] = useState(null);
  const [loadingEligible, setLoadingEligible] = useState(false);

  // Status Update Modal
  const [statusModalOpen, setStatusModalOpen] = useState(false);
  const [targetApplication, setTargetApplication] = useState(null);
  const [newStatus, setNewStatus] = useState('SHORTLISTED');
  const [statusRemarks, setStatusRemarks] = useState('');
  const [updatingStatus, setUpdatingStatus] = useState(false);

  // Interview Schedule Modal
  const [interviewModalOpen, setInterviewModalOpen] = useState(false);
  const [schedulingApp, setSchedulingApp] = useState(null);
  const [scheduleForm, setScheduleForm] = useState({
    roundNumber: 1,
    roundName: 'Technical Round 1',
    interviewType: 'ONLINE_MEET',
    scheduledAt: '',
    meetingLinkOrVenue: '',
    interviewerName: '',
    interviewerEmail: '',
  });
  const [schedulingLoading, setSchedulingLoading] = useState(false);

  // Interview Result Modal
  const [resultModalOpen, setResultModalOpen] = useState(false);
  const [selectedInterview, setSelectedInterview] = useState(null);
  const [resultForm, setResultForm] = useState({
    status: 'PASSED',
    score: 85,
    feedback: '',
  });
  const [submittingResult, setSubmittingResult] = useState(false);

  // Offer Issue Modal
  const [offerModalOpen, setOfferModalOpen] = useState(false);
  const [offerTargetApp, setOfferTargetApp] = useState(null);
  const [offerForm, setOfferForm] = useState({
    ctcLpa: 12.0,
    designation: 'Associate Software Engineer',
    validUntil: '',
    joiningDate: '',
    notes: 'Welcome to the team!',
  });
  const [issuingOffer, setIssuingOffer] = useState(false);

  // New Job Drive Form State
  const [jobForm, setJobForm] = useState({
    title: '',
    jobType: 'FULL_TIME',
    salaryPackageLpa: 10.0,
    location: 'Bangalore / Remote',
    applicationDeadline: '',
    driveDate: '',
    description: '',
    eligibilityCriteria: {
      minCgpa: 7.0,
      maxActiveBacklogs: 0,
      maxHistoryBacklogs: 0,
      minTenthPercentage: 60.0,
      minTwelfthPercentage: 60.0,
      maxGapYears: 1,
      allowedBranches: ['CSE', 'IT', 'ECE'],
      allowedGradYears: [2026, 2027],
      requiredSkills: ['Java', 'Spring Boot', 'SQL'],
    },
  });
  const [skillInput, setSkillInput] = useState('');
  const [publishingJob, setPublishingJob] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  const loadData = async () => {
    setLoading(true);
    try {
      const [dash, jobsData, recruiterProf] = await Promise.all([
        analyticsApi.getRecruiterDashboard().catch(() => null),
        jobApi.getAll('size=100').catch(() => ({ content: [] })),
        companyApi.getMyProfile().catch(() => null),
      ]);

      setDashboard(dash);
      const jobsList = jobsData.content || [];
      setJobs(jobsList);
      setCompanyProfile(recruiterProf);

      // Load applications, interviews, offers for the company's jobs
      if (jobsList.length > 0) {
        const appPromises = jobsList.map((j) =>
          applicationApi.getByJob(j.id, 'size=100').catch(() => ({ content: [] }))
        );
        const intPromises = jobsList.map((j) =>
          interviewApi.getByJob(j.id, 'size=100').catch(() => ({ content: [] }))
        );
        const offPromises = jobsList.map((j) =>
          offerApi.getByJob(j.id, 'size=100').catch(() => ({ content: [] }))
        );

        const [appsRes, intsRes, offsRes] = await Promise.all([
          Promise.all(appPromises),
          Promise.all(intPromises),
          Promise.all(offPromises),
        ]);

        const allApps = appsRes.flatMap((r) => r.content || []);
        const allInts = intsRes.flatMap((r) => r.content || []);
        const allOffs = offsRes.flatMap((r) => r.content || []);

        setApplications(allApps);
        setInterviews(allInts);
        setOffers(allOffs);
      }
    } catch (err) {
      console.error('Failed to load recruiter data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const showNotification = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 5000);
  };

  // Branch & Grad Year toggles
  const availableBranches = ['CSE', 'IT', 'ECE', 'EEE', 'MECH', 'CIVIL', 'AIML', 'DS'];
  const availableGradYears = [2024, 2025, 2026, 2027];

  const toggleBranch = (branch) => {
    const current = [...jobForm.eligibilityCriteria.allowedBranches];
    const exists = current.includes(branch);
    const updated = exists ? current.filter((b) => b !== branch) : [...current, branch];
    setJobForm({
      ...jobForm,
      eligibilityCriteria: { ...jobForm.eligibilityCriteria, allowedBranches: updated },
    });
  };

  const toggleGradYear = (year) => {
    const current = [...jobForm.eligibilityCriteria.allowedGradYears];
    const exists = current.includes(year);
    const updated = exists ? current.filter((y) => y !== year) : [...current, year];
    setJobForm({
      ...jobForm,
      eligibilityCriteria: { ...jobForm.eligibilityCriteria, allowedGradYears: updated },
    });
  };

  const addSkillToJob = (e) => {
    e.preventDefault();
    if (!skillInput.trim()) return;
    if (jobForm.eligibilityCriteria.requiredSkills.includes(skillInput.trim())) return;
    setJobForm({
      ...jobForm,
      eligibilityCriteria: {
        ...jobForm.eligibilityCriteria,
        requiredSkills: [...jobForm.eligibilityCriteria.requiredSkills, skillInput.trim()],
      },
    });
    setSkillInput('');
  };

  const removeSkillFromJob = (skill) => {
    setJobForm({
      ...jobForm,
      eligibilityCriteria: {
        ...jobForm.eligibilityCriteria,
        requiredSkills: jobForm.eligibilityCriteria.requiredSkills.filter((s) => s !== skill),
      },
    });
  };

  const handlePublishJob = async (e) => {
    e.preventDefault();
    setPublishingJob(true);
    try {
      const payload = {
        ...jobForm,
        salaryPackageLpa: parseFloat(jobForm.salaryPackageLpa),
        applicationDeadline: jobForm.applicationDeadline.includes('T')
          ? jobForm.applicationDeadline
          : `${jobForm.applicationDeadline}T23:59:59`,
        driveDate: jobForm.driveDate || null,
        eligibilityCriteria: {
          ...jobForm.eligibilityCriteria,
          minCgpa: parseFloat(jobForm.eligibilityCriteria.minCgpa),
          maxActiveBacklogs: parseInt(jobForm.eligibilityCriteria.maxActiveBacklogs, 10),
          maxHistoryBacklogs: parseInt(jobForm.eligibilityCriteria.maxHistoryBacklogs, 10),
          minTenthPercentage: parseFloat(jobForm.eligibilityCriteria.minTenthPercentage),
          minTwelfthPercentage: parseFloat(jobForm.eligibilityCriteria.minTwelfthPercentage),
          maxGapYears: parseInt(jobForm.eligibilityCriteria.maxGapYears, 10),
        },
      };

      await jobApi.create(payload);
      showNotification('Campus recruitment drive published successfully!');
      setTab('jobs');
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setPublishingJob(false);
    }
  };

  // Check Eligible Candidates pool for a job
  const handleViewEligiblePool = async (job) => {
    setCurrentJobForEligible(job);
    setLoadingEligible(true);
    setEligibleModalOpen(true);
    try {
      const candidates = await jobApi.getEligibleCandidates(job.id);
      setEligibleStudents(candidates || []);
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setLoadingEligible(false);
    }
  };

  // Handle Application Status Update
  const handleOpenStatusModal = (app) => {
    setTargetApplication(app);
    setNewStatus(app.status || 'SHORTLISTED');
    setStatusRemarks('');
    setStatusModalOpen(true);
  };

  const handleSubmitStatusUpdate = async (e) => {
    e.preventDefault();
    if (!targetApplication) return;
    setUpdatingStatus(true);
    try {
      await applicationApi.updateStatus(targetApplication.id, {
        newStatus,
        remarks: statusRemarks,
      });
      showNotification(`Candidate status updated to ${newStatus}`);
      setStatusModalOpen(false);
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setUpdatingStatus(false);
    }
  };

  // Handle Schedule Interview
  const handleOpenInterviewModal = (app) => {
    setSchedulingApp(app);
    setScheduleForm({
      roundNumber: 1,
      roundName: 'Technical Round 1',
      interviewType: 'ONLINE_MEET',
      scheduledAt: new Date(Date.now() + 86400000 * 2).toISOString().slice(0, 16),
      meetingLinkOrVenue: 'https://meet.google.com/spms-recruitment',
      interviewerName: companyProfile?.fullName || 'Lead Technical Interviewer',
      interviewerEmail: companyProfile?.userEmail || 'interviews@company.com',
    });
    setInterviewModalOpen(true);
  };

  const handleSubmitScheduleInterview = async (e) => {
    e.preventDefault();
    if (!schedulingApp) return;
    setSchedulingLoading(true);
    try {
      const payload = {
        applicationId: schedulingApp.id,
        roundNumber: parseInt(scheduleForm.roundNumber, 10),
        roundName: scheduleForm.roundName,
        interviewType: scheduleForm.interviewType,
        scheduledAt: scheduleForm.scheduledAt.length === 16 ? `${scheduleForm.scheduledAt}:00` : scheduleForm.scheduledAt,
        meetingLinkOrVenue: scheduleForm.meetingLinkOrVenue,
        interviewerName: scheduleForm.interviewerName,
        interviewerEmail: scheduleForm.interviewerEmail,
      };

      await interviewApi.schedule(payload);
      showNotification('Interview session scheduled and candidate notified!');
      setInterviewModalOpen(false);
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setSchedulingLoading(false);
    }
  };

  // Submit Interview Result
  const handleOpenResultModal = (interview) => {
    setSelectedInterview(interview);
    setResultForm({
      status: 'PASSED',
      score: 85,
      feedback: 'Excellent problem solving skills and strong fundamentals in system design.',
    });
    setResultModalOpen(true);
  };

  const handleSubmitInterviewResult = async (e) => {
    e.preventDefault();
    if (!selectedInterview) return;
    setSubmittingResult(true);
    try {
      await interviewApi.submitResult(selectedInterview.id, {
        status: resultForm.status,
        score: parseInt(resultForm.score, 10),
        feedback: resultForm.feedback,
      });
      showNotification('Interview evaluation recorded and candidate notified!');
      setResultModalOpen(false);
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setSubmittingResult(false);
    }
  };

  // Issue Offer
  const handleOpenOfferModal = (app) => {
    setOfferTargetApp(app);
    const jobCtc = app.jobSalaryPackageLpa || 12.0;
    const defaultValidUntil = new Date(Date.now() + 86400000 * 14).toISOString().slice(0, 10);
    const defaultJoining = new Date(Date.now() + 86400000 * 90).toISOString().slice(0, 10);

    setOfferForm({
      ctcLpa: jobCtc,
      designation: app.jobTitle || 'Software Engineer',
      validUntil: defaultValidUntil,
      joiningDate: defaultJoining,
      notes: 'Congratulations! We are delighted to extend this placement offer to join our engineering division.',
    });
    setOfferModalOpen(true);
  };

  const handleSubmitOffer = async (e) => {
    e.preventDefault();
    if (!offerTargetApp) return;
    setIssuingOffer(true);
    try {
      const payload = {
        applicationId: offerTargetApp.id,
        ctcLpa: parseFloat(offerForm.ctcLpa),
        designation: offerForm.designation,
        validUntil: offerForm.validUntil,
        joiningDate: offerForm.joiningDate || null,
        notes: offerForm.notes,
      };

      await offerApi.issue(payload);
      showNotification('Placement offer letter issued! Student can now review and accept.');
      setOfferModalOpen(false);
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    } finally {
      setIssuingOffer(false);
    }
  };

  // Revoke Offer
  const handleRevokeOffer = async (offerId) => {
    const reason = prompt('Please enter the reason for revoking this offer:');
    if (!reason) return;
    try {
      await offerApi.revoke(offerId, reason);
      showNotification('Offer has been revoked.');
      loadData();
    } catch (err) {
      showNotification(err.message, 'error');
    }
  };

  // Status Badge Helper
  const getAppStatusBadge = (status) => {
    switch (status) {
      case 'OFFERED':
      case 'ACCEPTED':
        return <span className="badge badge-success">{status}</span>;
      case 'SHORTLISTED':
      case 'INTERVIEW_SCHEDULED':
        return <span className="badge badge-primary">{status.replace('_', ' ')}</span>;
      case 'REJECTED':
      case 'WITHDRAWN':
        return <span className="badge badge-danger">{status}</span>;
      case 'UNDER_REVIEW':
        return <span className="badge badge-warning">UNDER REVIEW</span>;
      default:
        return <span className="badge badge-neutral">{status || 'APPLIED'}</span>;
    }
  };

  // Filtered applications
  const filteredApplications = applications.filter((app) => {
    const matchesJob = selectedJobFilter === 'ALL' || app.jobId?.toString() === selectedJobFilter;
    const matchesStatus = selectedStatusFilter === 'ALL' || app.status === selectedStatusFilter;
    return matchesJob && matchesStatus;
  });

  return (
    <div className="app-container" style={{ padding: '2rem 1.5rem 4rem' }}>
      {/* Toast Alert */}
      {feedbackMsg.text && (
        <div
          style={{
            position: 'fixed',
            bottom: '2rem',
            right: '2rem',
            zIndex: 100,
            background: feedbackMsg.type === 'error' ? 'rgba(239, 68, 68, 0.95)' : 'rgba(16, 185, 129, 0.95)',
            color: '#fff',
            padding: '1rem 1.5rem',
            borderRadius: '12px',
            boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.4)',
            backdropFilter: 'blur(8px)',
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            fontWeight: 600,
            animation: 'fadeIn 0.3s ease',
          }}
        >
          {feedbackMsg.type === 'error' ? <AlertCircle size={20} /> : <CheckCircle size={20} />}
          {feedbackMsg.text}
        </div>
      )}

      {/* Hero Welcome Banner */}
      <div
        className="glass-card"
        style={{
          padding: '2rem',
          marginBottom: '2rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1.5rem',
          background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(168, 85, 247, 0.1) 100%)',
          border: '1px solid rgba(99, 102, 241, 0.3)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <span className="badge badge-primary">
              <Building2 size={12} /> {companyProfile?.companyName || 'Corporate Partner'}
            </span>
            <span className="badge badge-success">
              <CheckCircle size={12} /> Verified Recruiter
            </span>
          </div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, margin: '0 0 0.5rem', color: '#fff' }}>
            Corporate Hiring Portal
          </h1>
          <p style={{ color: '#94a3b8', margin: 0, fontSize: '0.95rem' }}>
            Publish automated placement drives, evaluate qualified applicants, schedule interview rounds, and extend job offers.
          </p>
        </div>

        <button
          id="post-new-drive-header-btn"
          className="btn btn-primary"
          onClick={() => setTab('postJob')}
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.75rem 1.5rem' }}
        >
          <PlusCircle size={18} /> Publish New Drive
        </button>
      </div>

      {/* Navigation Tabs */}
      <div
        style={{
          display: 'flex',
          gap: '0.5rem',
          borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
          marginBottom: '2rem',
          overflowX: 'auto',
          paddingBottom: '0.5rem',
        }}
      >
        {[
          { id: 'overview', label: 'Dashboard Overview', icon: Briefcase },
          { id: 'postJob', label: 'Post Job Drive', icon: PlusCircle },
          { id: 'jobs', label: `My Job Drives (${jobs.length})`, icon: Building2 },
          { id: 'pipeline', label: `Candidate Pipeline (${applications.length})`, icon: Users },
          { id: 'interviews', label: `Interviews (${interviews.length})`, icon: Calendar },
          { id: 'offers', label: `Offers Extended (${offers.length})`, icon: Award },
        ].map((t) => {
          const Icon = t.icon;
          const isActive = tab === t.id;
          return (
            <button
              key={t.id}
              id={`recruiter-tab-${t.id}`}
              onClick={() => setTab(t.id)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                padding: '0.75rem 1.25rem',
                borderRadius: '10px',
                fontWeight: 600,
                fontSize: '0.9rem',
                border: 'none',
                cursor: 'pointer',
                transition: 'all 0.2s',
                background: isActive ? 'rgba(99, 102, 241, 0.2)' : 'transparent',
                color: isActive ? '#818cf8' : '#94a3b8',
                borderBottom: isActive ? '2px solid #818cf8' : '2px solid transparent',
              }}
            >
              <Icon size={16} />
              {t.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: OVERVIEW */}
      {tab === 'overview' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          {/* Key Metrics */}
          <div className="grid-responsive" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))' }}>
            <StatCard
              title="Active Job Drives"
              value={dashboard?.activeJobsCount ?? jobs.length}
              subtitle="Open for university applications"
              icon={Briefcase}
              color="indigo"
            />
            <StatCard
              title="Total Applicants"
              value={dashboard?.totalApplicationsCount ?? applications.length}
              subtitle="Received across all active roles"
              icon={Users}
              color="cyan"
            />
            <StatCard
              title="Interviews Conducted"
              value={dashboard?.interviewsConductedCount ?? interviews.length}
              subtitle="Technical & HR assessments"
              icon={Calendar}
              color="amber"
            />
            <StatCard
              title="Offers Extended"
              value={dashboard?.offersExtendedCount ?? offers.length}
              subtitle={`${offers.filter((o) => o.status === 'OFFER_ACCEPTED').length} accepted`}
              icon={Award}
              color="emerald"
            />
          </div>

          {/* Quick Applicant Pipeline Snapshot */}
          <div className="glass-card" style={{ padding: '1.75rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <div>
                <h3 style={{ margin: '0 0 0.25rem', fontSize: '1.15rem', color: '#fff' }}>
                  Recent Applications
                </h3>
                <p style={{ margin: 0, fontSize: '0.85rem', color: '#94a3b8' }}>
                  Latest candidates who applied to your open placement drives
                </p>
              </div>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setTab('pipeline')}
                style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}
              >
                View Full Pipeline <ChevronRight size={14} />
              </button>
            </div>

            {applications.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Users size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No student applications received yet.</p>
                <button className="btn btn-primary btn-sm" onClick={() => setTab('postJob')}>
                  Publish a Drive to Attract Talent
                </button>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Candidate</th>
                      <th>Roll No / Branch</th>
                      <th>Target Role</th>
                      <th>Academic Merit</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {applications.slice(0, 6).map((app) => (
                      <tr key={app.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>
                            {app.studentName || 'Student Candidate'}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            {app.studentEmail}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 500 }}>{app.studentRollNumber || 'N/A'}</div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{app.studentBranch}</div>
                        </td>
                        <td>
                          <span style={{ fontWeight: 600, color: '#818cf8' }}>{app.jobTitle}</span>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem' }}>
                            CGPA: <strong style={{ color: '#10b981' }}>{app.studentCgpa ?? 'N/A'}</strong>
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            Backlogs: {app.studentActiveBacklogs ?? 0}
                          </div>
                        </td>
                        <td>{getAppStatusBadge(app.status)}</td>
                        <td>
                          <div style={{ display: 'flex', gap: '0.4rem' }}>
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => handleOpenStatusModal(app)}
                              title="Update Status"
                            >
                              Review
                            </button>
                            <button
                              className="btn btn-primary btn-sm"
                              onClick={() => handleOpenInterviewModal(app)}
                              title="Schedule Interview"
                            >
                              Interview
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: POST JOB DRIVE */}
      {tab === 'postJob' && (
        <div className="glass-card" style={{ padding: '2rem', maxWidth: '900px', margin: '0 auto' }}>
          <div style={{ marginBottom: '1.5rem', borderBottom: '1px solid rgba(255, 255, 255, 0.08)', paddingBottom: '1rem' }}>
            <h2 style={{ margin: '0 0 0.5rem', fontSize: '1.5rem', color: '#fff' }}>
              Publish Campus Placement Drive
            </h2>
            <p style={{ margin: 0, color: '#94a3b8', fontSize: '0.9rem' }}>
              Set comprehensive qualifications. Our automated Strategy Pattern Eligibility Engine will automatically screen all applicant submissions.
            </p>
          </div>

          <form onSubmit={handlePublishJob} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {/* Basic Info */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1rem' }}>
              <div>
                <label className="form-label">Job Title *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Software Engineer - Backend"
                  className="input-field"
                  value={jobForm.title}
                  onChange={(e) => setJobForm({ ...jobForm, title: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Employment Type *</label>
                <select
                  className="input-field"
                  value={jobForm.jobType}
                  onChange={(e) => setJobForm({ ...jobForm, jobType: e.target.value })}
                >
                  <option value="FULL_TIME">Full Time (FTE)</option>
                  <option value="INTERNSHIP">Internship</option>
                  <option value="INTERN_PLUS_FULL_TIME">Internship + FTE Conversion</option>
                </select>
              </div>

              <div>
                <label className="form-label">Salary Package / CTC (LPA) *</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  required
                  placeholder="e.g. 12.5"
                  className="input-field"
                  value={jobForm.salaryPackageLpa}
                  onChange={(e) => setJobForm({ ...jobForm, salaryPackageLpa: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Work Location</label>
                <input
                  type="text"
                  placeholder="e.g. Bangalore / Hybrid"
                  className="input-field"
                  value={jobForm.location}
                  onChange={(e) => setJobForm({ ...jobForm, location: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Application Deadline *</label>
                <input
                  type="datetime-local"
                  required
                  className="input-field"
                  value={jobForm.applicationDeadline}
                  onChange={(e) => setJobForm({ ...jobForm, applicationDeadline: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Proposed Drive Date</label>
                <input
                  type="date"
                  className="input-field"
                  value={jobForm.driveDate}
                  onChange={(e) => setJobForm({ ...jobForm, driveDate: e.target.value })}
                />
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="form-label">Job Description & Responsibilities *</label>
              <textarea
                required
                rows={4}
                placeholder="Describe role responsibilities, core technologies, and perks..."
                className="input-field"
                value={jobForm.description}
                onChange={(e) => setJobForm({ ...jobForm, description: e.target.value })}
              />
            </div>

            {/* Eligibility Rule Builder */}
            <div
              style={{
                background: 'rgba(255, 255, 255, 0.02)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: '12px',
                padding: '1.5rem',
              }}
            >
              <h3 style={{ margin: '0 0 1rem', fontSize: '1.1rem', color: '#818cf8', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Sparkles size={18} /> Automated Eligibility Rules
              </h3>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1.25rem' }}>
                <div>
                  <label className="form-label">Minimum CGPA (0-10)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    max="10"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.minCgpa}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, minCgpa: e.target.value },
                      })
                    }
                  />
                </div>

                <div>
                  <label className="form-label">Max Active Backlogs</label>
                  <input
                    type="number"
                    min="0"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.maxActiveBacklogs}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, maxActiveBacklogs: e.target.value },
                      })
                    }
                  />
                </div>

                <div>
                  <label className="form-label">Max History Backlogs</label>
                  <input
                    type="number"
                    min="0"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.maxHistoryBacklogs}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, maxHistoryBacklogs: e.target.value },
                      })
                    }
                  />
                </div>

                <div>
                  <label className="form-label">Min 10th Marks (%)</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.minTenthPercentage}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, minTenthPercentage: e.target.value },
                      })
                    }
                  />
                </div>

                <div>
                  <label className="form-label">Min 12th Marks (%)</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.minTwelfthPercentage}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, minTwelfthPercentage: e.target.value },
                      })
                    }
                  />
                </div>

                <div>
                  <label className="form-label">Max Academic Gap Years</label>
                  <input
                    type="number"
                    min="0"
                    className="input-field"
                    value={jobForm.eligibilityCriteria.maxGapYears}
                    onChange={(e) =>
                      setJobForm({
                        ...jobForm,
                        eligibilityCriteria: { ...jobForm.eligibilityCriteria, maxGapYears: e.target.value },
                      })
                    }
                  />
                </div>
              </div>

              {/* Allowed Branches */}
              <div style={{ marginBottom: '1.25rem' }}>
                <label className="form-label">Eligible Engineering Branches</label>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  {availableBranches.map((branch) => {
                    const isSelected = jobForm.eligibilityCriteria.allowedBranches.includes(branch);
                    return (
                      <button
                        key={branch}
                        type="button"
                        onClick={() => toggleBranch(branch)}
                        style={{
                          padding: '0.35rem 0.8rem',
                          borderRadius: '6px',
                          border: isSelected ? '1px solid #6366f1' : '1px solid rgba(255, 255, 255, 0.1)',
                          background: isSelected ? 'rgba(99, 102, 241, 0.25)' : 'rgba(255, 255, 255, 0.03)',
                          color: isSelected ? '#a5b4fc' : '#94a3b8',
                          fontSize: '0.8rem',
                          cursor: 'pointer',
                        }}
                      >
                        {isSelected ? '✓ ' : '+ '}
                        {branch}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Allowed Graduation Years */}
              <div style={{ marginBottom: '1.25rem' }}>
                <label className="form-label">Eligible Graduation Batches</label>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  {availableGradYears.map((year) => {
                    const isSelected = jobForm.eligibilityCriteria.allowedGradYears.includes(year);
                    return (
                      <button
                        key={year}
                        type="button"
                        onClick={() => toggleGradYear(year)}
                        style={{
                          padding: '0.35rem 0.8rem',
                          borderRadius: '6px',
                          border: isSelected ? '1px solid #10b981' : '1px solid rgba(255, 255, 255, 0.1)',
                          background: isSelected ? 'rgba(16, 185, 129, 0.25)' : 'rgba(255, 255, 255, 0.03)',
                          color: isSelected ? '#6ee7b7' : '#94a3b8',
                          fontSize: '0.8rem',
                          cursor: 'pointer',
                        }}
                      >
                        {isSelected ? '✓ ' : '+ '}
                        Batch of {year}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Required Skills */}
              <div>
                <label className="form-label">Required Technical Skills</label>
                <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.75rem' }}>
                  <input
                    type="text"
                    placeholder="e.g. React, Docker, Python..."
                    className="input-field"
                    value={skillInput}
                    onChange={(e) => setSkillInput(e.target.value)}
                  />
                  <button type="button" className="btn btn-secondary" onClick={addSkillToJob}>
                    Add
                  </button>
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
                  {jobForm.eligibilityCriteria.requiredSkills.map((sk) => (
                    <span
                      key={sk}
                      className="badge badge-primary"
                      style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.3rem' }}
                      onClick={() => removeSkillFromJob(sk)}
                      title="Click to remove"
                    >
                      {sk} ×
                    </span>
                  ))}
                </div>
              </div>
            </div>

            <button
              id="publish-job-submit-btn"
              type="submit"
              disabled={publishingJob}
              className="btn btn-primary"
              style={{ padding: '0.85rem', fontSize: '1rem', fontWeight: 700 }}
            >
              {publishingJob ? 'Publishing Drive...' : 'Publish Campus Placement Drive'}
            </button>
          </form>
        </div>
      )}

      {/* TAB 3: MY JOB DRIVES */}
      {tab === 'jobs' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ margin: 0, fontSize: '1.4rem', color: '#fff' }}>
              Published Job Drives
            </h2>
            <button className="btn btn-primary btn-sm" onClick={() => setTab('postJob')}>
              <PlusCircle size={14} /> Post Another Drive
            </button>
          </div>

          {jobs.length === 0 ? (
            <div className="glass-card" style={{ padding: '3rem', textAlign: 'center', color: '#94a3b8' }}>
              <Building2 size={48} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
              <p>You have not published any campus placement drives yet.</p>
              <button className="btn btn-primary" onClick={() => setTab('postJob')}>
                Publish Your First Drive
              </button>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
              {jobs.map((job) => {
                const jobApps = applications.filter((a) => a.jobId === job.id);
                return (
                  <div key={job.id} className="glass-card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                      <span className="badge badge-success">{job.status || 'PUBLISHED'}</span>
                      <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                        Type: {job.jobType?.replace('_', ' ')}
                      </span>
                    </div>

                    <h3 style={{ margin: '0 0 0.5rem', fontSize: '1.2rem', color: '#fff' }}>{job.title}</h3>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.85rem', color: '#cbd5e1', marginBottom: '1rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <DollarSign size={14} color="#10b981" />
                        Package: <strong style={{ color: '#10b981' }}>{job.salaryPackageLpa} LPA</strong>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <MapPin size={14} color="#38bdf8" />
                        Location: {job.location || 'Pan-India'}
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Clock size={14} color="#f59e0b" />
                        Deadline: {job.applicationDeadline ? new Date(job.applicationDeadline).toLocaleDateString() : 'N/A'}
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Users size={14} color="#a855f7" />
                        Applicants: <strong>{jobApps.length} students applied</strong>
                      </div>
                    </div>

                    {/* Cutoff summary */}
                    {job.eligibilityCriteria && (
                      <div
                        style={{
                          background: 'rgba(255, 255, 255, 0.02)',
                          padding: '0.6rem 0.8rem',
                          borderRadius: '8px',
                          fontSize: '0.75rem',
                          color: '#94a3b8',
                          marginBottom: '1.25rem',
                        }}
                      >
                        Cutoffs: Min CGPA {job.eligibilityCriteria.minCgpa} | Max {job.eligibilityCriteria.maxActiveBacklogs} Backlogs | Branches: {job.eligibilityCriteria.allowedBranches?.join(', ') || 'All'}
                      </div>
                    )}

                    <div style={{ marginTop: 'auto', display: 'flex', gap: '0.5rem' }}>
                      <button
                        className="btn btn-secondary btn-sm"
                        style={{ flex: 1 }}
                        onClick={() => handleViewEligiblePool(job)}
                      >
                        Eligible Talent Pool
                      </button>
                      <button
                        className="btn btn-primary btn-sm"
                        style={{ flex: 1 }}
                        onClick={() => {
                          setSelectedJobFilter(job.id.toString());
                          setTab('pipeline');
                        }}
                      >
                        View Applicants
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* TAB 4: CANDIDATE PIPELINE */}
      {tab === 'pipeline' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Filter Bar */}
          <div
            className="glass-card"
            style={{
              padding: '1.25rem',
              display: 'flex',
              gap: '1rem',
              flexWrap: 'wrap',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
              <div>
                <label className="form-label" style={{ marginBottom: '0.25rem', fontSize: '0.75rem' }}>
                  Filter by Drive
                </label>
                <select
                  className="input-field"
                  style={{ minWidth: '200px' }}
                  value={selectedJobFilter}
                  onChange={(e) => setSelectedJobFilter(e.target.value)}
                >
                  <option value="ALL">All Job Postings ({applications.length})</option>
                  {jobs.map((j) => (
                    <option key={j.id} value={j.id.toString()}>
                      {j.title}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="form-label" style={{ marginBottom: '0.25rem', fontSize: '0.75rem' }}>
                  Filter by Status
                </label>
                <select
                  className="input-field"
                  style={{ minWidth: '180px' }}
                  value={selectedStatusFilter}
                  onChange={(e) => setSelectedStatusFilter(e.target.value)}
                >
                  <option value="ALL">All Statuses</option>
                  <option value="APPLIED">Applied</option>
                  <option value="SHORTLISTED">Shortlisted</option>
                  <option value="INTERVIEW_SCHEDULED">Interview Scheduled</option>
                  <option value="OFFERED">Offered</option>
                  <option value="ACCEPTED">Offer Accepted</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
            </div>

            <div style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
              Showing <strong>{filteredApplications.length}</strong> matching candidates
            </div>
          </div>

          {/* Applications Table */}
          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {filteredApplications.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Users size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No candidates match the selected filters.</p>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Candidate Info</th>
                      <th>Academics</th>
                      <th>Applied Drive</th>
                      <th>Current Stage</th>
                      <th>Applied Date</th>
                      <th style={{ textAlign: 'right' }}>Candidate Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredApplications.map((app) => (
                      <tr key={app.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>
                            {app.studentName || 'Student'}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            {app.studentEmail}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#818cf8' }}>
                            Roll: {app.studentRollNumber || 'N/A'}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem' }}>
                            Branch: <strong>{app.studentBranch || 'N/A'}</strong>
                          </div>
                          <div style={{ fontSize: '0.85rem' }}>
                            CGPA: <strong style={{ color: '#10b981' }}>{app.studentCgpa ?? 'N/A'}</strong>
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            Backlogs: {app.studentActiveBacklogs ?? 0}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 600, color: '#e2e8f0' }}>{app.jobTitle}</div>
                          <div style={{ fontSize: '0.75rem', color: '#10b981' }}>
                            {app.jobSalaryPackageLpa} LPA
                          </div>
                        </td>
                        <td>{getAppStatusBadge(app.status)}</td>
                        <td style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                          {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : 'Recent'}
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          <div style={{ display: 'flex', gap: '0.4rem', justifyContent: 'flex-end', flexWrap: 'wrap' }}>
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => handleOpenStatusModal(app)}
                              title="Update Status"
                            >
                              Update Status
                            </button>
                            <button
                              className="btn btn-primary btn-sm"
                              onClick={() => handleOpenInterviewModal(app)}
                              title="Schedule Interview"
                            >
                              Schedule
                            </button>
                            <button
                              className="btn btn-success btn-sm"
                              onClick={() => handleOpenOfferModal(app)}
                              title="Extend Placement Offer"
                              style={{
                                background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                                color: '#fff',
                              }}
                            >
                              Extend Offer
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 5: INTERVIEWS */}
      {tab === 'interviews' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ margin: 0, fontSize: '1.4rem', color: '#fff' }}>
              Scheduled Assessment & Interview Rounds
            </h2>
          </div>

          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {interviews.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Calendar size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No interview rounds scheduled yet.</p>
                <button className="btn btn-primary btn-sm" onClick={() => setTab('pipeline')}>
                  Go to Candidate Pipeline to Schedule
                </button>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Candidate</th>
                      <th>Round Name</th>
                      <th>Type & Venue / Link</th>
                      <th>Date & Time</th>
                      <th>Interviewer</th>
                      <th>Status</th>
                      <th style={{ textAlign: 'right' }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {interviews.map((intv) => (
                      <tr key={intv.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>
                            {intv.studentName || 'Student'}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            {intv.jobTitle}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 600, color: '#818cf8' }}>
                            Round {intv.roundNumber}: {intv.roundName}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem' }}>
                            {intv.interviewType?.replace('_', ' ')}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#38bdf8' }}>
                            {intv.meetingLinkOrVenue}
                          </div>
                        </td>
                        <td style={{ fontSize: '0.85rem', color: '#f59e0b' }}>
                          {intv.scheduledAt ? new Date(intv.scheduledAt).toLocaleString() : 'N/A'}
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem', fontWeight: 500 }}>
                            {intv.interviewerName}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            {intv.interviewerEmail}
                          </div>
                        </td>
                        <td>
                          {intv.status === 'COMPLETED' ? (
                            <span className="badge badge-success">COMPLETED</span>
                          ) : intv.status === 'CANCELLED' ? (
                            <span className="badge badge-danger">CANCELLED</span>
                          ) : (
                            <span className="badge badge-primary">SCHEDULED</span>
                          )}
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          {intv.status === 'SCHEDULED' && (
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => handleOpenResultModal(intv)}
                            >
                              Submit Result
                            </button>
                          )}
                          {intv.status === 'COMPLETED' && (
                            <span style={{ fontSize: '0.8rem', color: '#10b981', fontWeight: 600 }}>
                              Score: {intv.score ?? 'N/A'}/100
                            </span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 6: OFFERS EXTENDED */}
      {tab === 'offers' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ margin: 0, fontSize: '1.4rem', color: '#fff' }}>
              Official Placement Offers
            </h2>
          </div>

          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {offers.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Award size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No job offers have been extended yet.</p>
                <button className="btn btn-primary btn-sm" onClick={() => setTab('pipeline')}>
                  Go to Candidate Pipeline to Issue Offers
                </button>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Candidate</th>
                      <th>Designation</th>
                      <th>Offered Package</th>
                      <th>Valid Until</th>
                      <th>Status</th>
                      <th style={{ textAlign: 'right' }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {offers.map((off) => (
                      <tr key={off.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>
                            {off.studentName || 'Student'}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            {off.studentEmail}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 600, color: '#e2e8f0' }}>{off.designation}</div>
                          <div style={{ fontSize: '0.75rem', color: '#818cf8' }}>
                            Joining: {off.joiningDate || 'TBD'}
                          </div>
                        </td>
                        <td>
                          <strong style={{ color: '#10b981', fontSize: '1rem' }}>
                            {off.ctcLpa} LPA
                          </strong>
                        </td>
                        <td style={{ fontSize: '0.85rem', color: '#f59e0b' }}>
                          {off.validUntil || 'N/A'}
                        </td>
                        <td>
                          {off.status === 'OFFER_ACCEPTED' ? (
                            <span className="badge badge-success">ACCEPTED</span>
                          ) : off.status === 'OFFER_DECLINED' ? (
                            <span className="badge badge-danger">DECLINED</span>
                          ) : off.status === 'OFFER_REVOKED' ? (
                            <span className="badge badge-danger">REVOKED</span>
                          ) : (
                            <span className="badge badge-primary">EXTENDED (AWAITING)</span>
                          )}
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          {off.status === 'OFFER_EXTENDED' && (
                            <button
                              className="btn btn-danger btn-sm"
                              onClick={() => handleRevokeOffer(off.id)}
                            >
                              Revoke Offer
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODAL: ELIGIBLE TALENT POOL */}
      {eligibleModalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: '750px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <div>
                <h3 style={{ margin: '0 0 0.25rem', color: '#fff' }}>
                  Eligible Talent Pool Preview
                </h3>
                <p style={{ margin: 0, fontSize: '0.85rem', color: '#94a3b8' }}>
                  Matching students for drive: <strong style={{ color: '#818cf8' }}>{currentJobForEligible?.title}</strong>
                </p>
              </div>
              <button className="btn btn-secondary btn-sm" onClick={() => setEligibleModalOpen(false)}>
                ✕ Close
              </button>
            </div>

            {loadingEligible ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                Scanning campus talent database against eligibility criteria...
              </div>
            ) : eligibleStudents.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                No registered students currently satisfy the specified cutoff criteria.
              </div>
            ) : (
              <div className="table-container" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Candidate</th>
                      <th>Branch & Batch</th>
                      <th>CGPA</th>
                      <th>Backlogs</th>
                    </tr>
                  </thead>
                  <tbody>
                    {eligibleStudents.map((st) => (
                      <tr key={st.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>{st.fullName}</div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{st.rollNumber}</div>
                        </td>
                        <td>
                          <div>{st.departmentBranch}</div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Batch {st.graduationYear}</div>
                        </td>
                        <td style={{ color: '#10b981', fontWeight: 600 }}>{st.cgpa}</td>
                        <td>{st.activeBacklogs} active</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODAL: UPDATE STATUS */}
      {statusModalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: '500px' }}>
            <h3 style={{ margin: '0 0 0.5rem', color: '#fff' }}>Update Application Stage</h3>
            <p style={{ color: '#94a3b8', fontSize: '0.85rem', margin: '0 0 1.25rem' }}>
              Candidate: <strong>{targetApplication?.studentName}</strong> ({targetApplication?.jobTitle})
            </p>

            <form onSubmit={handleSubmitStatusUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label className="form-label">Next Application Status</label>
                <select
                  className="input-field"
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                >
                  <option value="SHORTLISTED">SHORTLISTED</option>
                  <option value="TECHNICAL_ROUND">TECHNICAL ROUND</option>
                  <option value="HR_ROUND">HR ROUND</option>
                  <option value="REJECTED">REJECTED</option>
                  <option value="UNDER_REVIEW">UNDER REVIEW</option>
                </select>
              </div>

              <div>
                <label className="form-label">Internal Remarks / Feedback</label>
                <textarea
                  rows={3}
                  className="input-field"
                  placeholder="e.g. Cleared coding assessment with high score..."
                  value={statusRemarks}
                  onChange={(e) => setStatusRemarks(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setStatusModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={updatingStatus}
                  className="btn btn-primary"
                >
                  {updatingStatus ? 'Updating...' : 'Save Status'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: SCHEDULE INTERVIEW */}
      {interviewModalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: '550px' }}>
            <h3 style={{ margin: '0 0 0.5rem', color: '#fff' }}>Schedule Assessment / Interview</h3>
            <p style={{ color: '#94a3b8', fontSize: '0.85rem', margin: '0 0 1.25rem' }}>
              Candidate: <strong>{schedulingApp?.studentName}</strong> ({schedulingApp?.jobTitle})
            </p>

            <form onSubmit={handleSubmitScheduleInterview} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1rem' }}>
                <div>
                  <label className="form-label">Round #</label>
                  <input
                    type="number"
                    min="1"
                    required
                    className="input-field"
                    value={scheduleForm.roundNumber}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, roundNumber: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Round Name</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Technical Round 1"
                    className="input-field"
                    value={scheduleForm.roundName}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, roundName: e.target.value })}
                  />
                </div>
              </div>

              <div>
                <label className="form-label">Interview Type</label>
                <select
                  className="input-field"
                  value={scheduleForm.interviewType}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, interviewType: e.target.value })}
                >
                  <option value="ONLINE_MEET">Online Virtual Meeting</option>
                  <option value="OFFLINE_CAMPUS">On-Campus Physical Round</option>
                  <option value="TELEPHONIC">Telephonic Screen</option>
                </select>
              </div>

              <div>
                <label className="form-label">Scheduled Date & Time *</label>
                <input
                  type="datetime-local"
                  required
                  className="input-field"
                  value={scheduleForm.scheduledAt}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, scheduledAt: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Meeting URL or Campus Venue *</label>
                <input
                  type="text"
                  required
                  placeholder="https://meet.google.com/... or Campus Block A, Lab 3"
                  className="input-field"
                  value={scheduleForm.meetingLinkOrVenue}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, meetingLinkOrVenue: e.target.value })}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label className="form-label">Interviewer Name *</label>
                  <input
                    type="text"
                    required
                    className="input-field"
                    value={scheduleForm.interviewerName}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, interviewerName: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Interviewer Email</label>
                  <input
                    type="email"
                    className="input-field"
                    value={scheduleForm.interviewerEmail}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, interviewerEmail: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setInterviewModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={schedulingLoading}
                  className="btn btn-primary"
                >
                  {schedulingLoading ? 'Scheduling...' : 'Confirm & Notify Student'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: SUBMIT INTERVIEW RESULT */}
      {resultModalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: '500px' }}>
            <h3 style={{ margin: '0 0 0.5rem', color: '#fff' }}>Submit Interview Assessment</h3>
            <p style={{ color: '#94a3b8', fontSize: '0.85rem', margin: '0 0 1.25rem' }}>
              Candidate: <strong>{selectedInterview?.studentName}</strong> ({selectedInterview?.roundName})
            </p>

            <form onSubmit={handleSubmitInterviewResult} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label className="form-label">Evaluation Verdict</label>
                <select
                  className="input-field"
                  value={resultForm.status}
                  onChange={(e) => setResultForm({ ...resultForm, status: e.target.value })}
                >
                  <option value="PASSED">PASSED (Advance to next round/offer)</option>
                  <option value="FAILED">FAILED</option>
                </select>
              </div>

              <div>
                <label className="form-label">Assessment Score (0 - 100)</label>
                <input
                  type="number"
                  min="0"
                  max="100"
                  required
                  className="input-field"
                  value={resultForm.score}
                  onChange={(e) => setResultForm({ ...resultForm, score: e.target.value })}
                />
              </div>

              <div>
                <label className="form-label">Interviewer Feedback & Notes</label>
                <textarea
                  rows={3}
                  className="input-field"
                  placeholder="Detail candidate problem-solving, behavioral feedback..."
                  value={resultForm.feedback}
                  onChange={(e) => setResultForm({ ...resultForm, feedback: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setResultModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submittingResult}
                  className="btn btn-primary"
                >
                  {submittingResult ? 'Submitting...' : 'Submit Evaluation'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: EXTEND PLACEMENT OFFER */}
      {offerModalOpen && (
        <div className="modal-backdrop">
          <div className="modal-content" style={{ maxWidth: '550px' }}>
            <h3 style={{ margin: '0 0 0.5rem', color: '#fff' }}>Extend Official Job Offer</h3>
            <p style={{ color: '#94a3b8', fontSize: '0.85rem', margin: '0 0 1.25rem' }}>
              Candidate: <strong>{offerTargetApp?.studentName}</strong> | Drive: <strong>{offerTargetApp?.jobTitle}</strong>
            </p>

            <form onSubmit={handleSubmitOffer} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label className="form-label">Offered CTC (LPA) *</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    required
                    className="input-field"
                    value={offerForm.ctcLpa}
                    onChange={(e) => setOfferForm({ ...offerForm, ctcLpa: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Designation / Title *</label>
                  <input
                    type="text"
                    required
                    className="input-field"
                    value={offerForm.designation}
                    onChange={(e) => setOfferForm({ ...offerForm, designation: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div>
                  <label className="form-label">Offer Validity Deadline *</label>
                  <input
                    type="date"
                    required
                    className="input-field"
                    value={offerForm.validUntil}
                    onChange={(e) => setOfferForm({ ...offerForm, validUntil: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Expected Joining Date</label>
                  <input
                    type="date"
                    className="input-field"
                    value={offerForm.joiningDate}
                    onChange={(e) => setOfferForm({ ...offerForm, joiningDate: e.target.value })}
                  />
                </div>
              </div>

              <div>
                <label className="form-label">Notes & Welcome Message</label>
                <textarea
                  rows={3}
                  className="input-field"
                  value={offerForm.notes}
                  onChange={(e) => setOfferForm({ ...offerForm, notes: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setOfferModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={issuingOffer}
                  className="btn btn-success"
                  style={{ background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)', color: '#fff' }}
                >
                  {issuingOffer ? 'Issuing Offer...' : 'Extend Formal Placement Offer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
