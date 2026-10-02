import React, { useState, useEffect } from 'react';
import confetti from 'canvas-confetti';
import {
  studentApi,
  jobApi,
  applicationApi,
  interviewApi,
  offerApi,
  analyticsApi,
} from '../services/api';
import StatCard from '../components/StatCard';
import EligibilityModal from '../components/EligibilityModal';
import {
  Briefcase,
  FileText,
  Calendar,
  Award,
  CheckCircle,
  Clock,
  Sparkles,
  Upload,
  Send,
  ExternalLink,
  Plus,
  Trash2,
  AlertCircle,
  Building2,
  DollarSign,
  MapPin,
  ChevronRight,
} from 'lucide-react';

export default function StudentPortal() {
  const [tab, setTab] = useState('overview'); // overview, profile, jobs, applications, interviews, offers
  const [dashboard, setDashboard] = useState(null);
  const [profile, setProfile] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Eligibility Modal state
  const [selectedJob, setSelectedJob] = useState(null);
  const [eligibilityReport, setEligibilityReport] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [applying, setApplying] = useState(false);

  // Resume upload state
  const [resumeFile, setResumeFile] = useState(null);
  const [uploadingResume, setUploadingResume] = useState(false);
  const [newSkill, setNewSkill] = useState('');

  // Offer response state
  const [respondingOfferId, setRespondingOfferId] = useState(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const [dashData, profData, jobsData, appsData, intData, offData] = await Promise.all([
        analyticsApi.getStudentDashboard().catch(() => null),
        studentApi.getProfile().catch(() => null),
        jobApi.getAll('size=50').catch(() => ({ content: [] })),
        applicationApi.getMyApplications('size=50').catch(() => ({ content: [] })),
        interviewApi.getMyInterviews('size=50').catch(() => ({ content: [] })),
        offerApi.getMyOffers('size=50').catch(() => ({ content: [] })),
      ]);

      setDashboard(dashData);
      setProfile(profData);
      setJobs(jobsData.content || []);
      setApplications(appsData.content || []);
      setInterviews(intData.content || []);
      setOffers(offData.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCheckEligibility = async (job) => {
    try {
      const report = await jobApi.checkMyEligibility(job.id);
      setSelectedJob(job);
      setEligibilityReport(report);
      setModalOpen(true);
    } catch (err) {
      alert(err.message);
    }
  };

  const handleApply = async (jobId) => {
    setApplying(true);
    try {
      await applicationApi.apply(jobId);
      setModalOpen(false);
      await loadData();
      alert('Application submitted successfully!');
    } catch (err) {
      alert(err.message);
    } finally {
      setApplying(false);
    }
  };

  const handleResumeUpload = async (e) => {
    e.preventDefault();
    if (!resumeFile) return;
    setUploadingResume(true);
    try {
      await studentApi.uploadResume(resumeFile);
      setResumeFile(null);
      await loadData();
      alert('Resume uploaded successfully!');
    } catch (err) {
      alert(err.message);
    } finally {
      setUploadingResume(false);
    }
  };

  const handleAddSkill = async (e) => {
    e.preventDefault();
    if (!newSkill.trim()) return;
    try {
      await studentApi.addSkill({ skillName: newSkill.trim(), proficiency: 'INTERMEDIATE' });
      setNewSkill('');
      const updated = await studentApi.getProfile();
      setProfile(updated);
    } catch (err) {
      alert(err.message);
    }
  };

  const handleDeleteSkill = async (id) => {
    try {
      await studentApi.deleteSkill(id);
      const updated = await studentApi.getProfile();
      setProfile(updated);
    } catch (err) {
      alert(err.message);
    }
  };

  const handleOfferResponse = async (offerId, decision) => {
    setRespondingOfferId(offerId);
    try {
      await offerApi.respond(offerId, {
        decision,
        remarks: decision === 'ACCEPTED' ? 'Accepted through student portal' : 'Declined through student portal',
      });

      if (decision === 'ACCEPTED') {
        confetti({
          particleCount: 120,
          spread: 80,
          origin: { y: 0.6 },
        });
      }

      await loadData();
      alert(`Offer ${decision.toLowerCase()} successfully!`);
    } catch (err) {
      alert(err.message);
    } finally {
      setRespondingOfferId(null);
    }
  };

  return (
    <div className="app-container main-content">
      {/* Placement Header Banner */}
      <div
        className="glass-card"
        style={{
          marginBottom: '2rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1rem',
          background: profile?.isPlaced
            ? 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(6, 182, 212, 0.1) 100%)'
            : 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(139, 92, 246, 0.1) 100%)',
          borderColor: profile?.isPlaced ? 'rgba(16, 185, 129, 0.4)' : 'rgba(99, 102, 241, 0.3)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '0.25rem' }}>
            <h2 style={{ margin: 0 }}>
              Welcome back, {profile?.firstName || 'Candidate'}!
            </h2>
            {profile?.isPlaced && (
              <span className="badge badge-success" style={{ fontSize: '0.8rem' }}>
                <Sparkles size={14} /> Confirmed Placed
              </span>
            )}
          </div>
          <p style={{ margin: 0, fontSize: '0.9rem' }}>
            Roll No: <strong style={{ color: '#fff' }}>{profile?.rollNumber || 'N/A'}</strong> | {profile?.branch || 'Department'} (Class of {profile?.graduationYear || '2026'})
          </p>
        </div>

        {/* Profile Completion Bar */}
        <div style={{ minWidth: '220px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: '0.35rem' }}>
            <span style={{ color: '#94a3b8' }}>Profile Strength</span>
            <span style={{ fontWeight: 700, color: '#818cf8' }}>{dashboard?.profileCompletionPercentage || 0}%</span>
          </div>
          <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.1)', borderRadius: '4px', overflow: 'hidden' }}>
            <div
              style={{
                width: `${dashboard?.profileCompletionPercentage || 0}%`,
                height: '100%',
                background: 'linear-gradient(90deg, #6366f1 0%, #10b981 100%)',
                borderRadius: '4px',
                transition: 'width 0.5s ease',
              }}
            />
          </div>
        </div>
      </div>

      {/* Navigation Sub-Tabs */}
      <div
        style={{
          display: 'flex',
          gap: '0.5rem',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          paddingBottom: '0.75rem',
          marginBottom: '2rem',
          overflowX: 'auto',
        }}
      >
        {[
          { id: 'overview', label: 'Overview', icon: Briefcase },
          { id: 'profile', label: 'Profile & Resume', icon: FileText },
          { id: 'jobs', label: `Drives (${jobs.length})`, icon: Building2 },
          { id: 'applications', label: `Applications (${applications.length})`, icon: Send },
          { id: 'interviews', label: `Interviews (${interviews.length})`, icon: Calendar },
          { id: 'offers', label: `Offers (${offers.length})`, icon: Award },
        ].map(t => {
          const Icon = t.icon;
          const active = tab === t.id;
          return (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className="btn btn-secondary btn-sm"
              style={{
                background: active ? 'rgba(99, 102, 241, 0.2)' : 'transparent',
                borderColor: active ? '#6366f1' : 'transparent',
                color: active ? '#fff' : '#94a3b8',
              }}
            >
              <Icon size={16} color={active ? '#818cf8' : '#94a3b8'} /> {t.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: OVERVIEW */}
      {tab === 'overview' && (
        <div>
          <div className="grid-cols-4" style={{ marginBottom: '2rem' }}>
            <StatCard
              title="Applications"
              value={dashboard?.totalApplicationsSubmitted || 0}
              subtitle="Total drives applied"
              icon={Send}
              color="indigo"
            />
            <StatCard
              title="Shortlisted"
              value={dashboard?.shortlistedCount || 0}
              subtitle="Advanced to rounds"
              icon={CheckCircle}
              color="cyan"
            />
            <StatCard
              title="Interviews"
              value={dashboard?.interviewsScheduled || 0}
              subtitle="Upcoming & conducted"
              icon={Calendar}
              color="amber"
            />
            <StatCard
              title="Job Offers"
              value={dashboard?.offersReceived || 0}
              subtitle={profile?.isPlaced ? 'Placement Locked' : 'Formal offers'}
              icon={Award}
              color="emerald"
            />
          </div>

          {/* Quick Active Items Grid */}
          <div className="grid-cols-2">
            {/* Active Interviews Card */}
            <div className="glass-card">
              <h3 style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Calendar size={18} color="#f59e0b" /> Upcoming Interview Schedule
              </h3>
              {interviews.length === 0 ? (
                <p style={{ fontSize: '0.9rem' }}>No interview rounds scheduled yet.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {interviews.slice(0, 3).map(item => (
                    <div
                      key={item.id}
                      style={{
                        padding: '0.85rem',
                        borderRadius: '8px',
                        background: 'rgba(255, 255, 255, 0.03)',
                        border: '1px solid rgba(255, 255, 255, 0.05)',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                        <strong style={{ color: '#fff' }}>{item.roundName}</strong>
                        <span className="badge badge-warning">{item.status}</span>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                        Company: {item.companyName} | {new Date(item.scheduledAt).toLocaleString()}
                      </div>
                      {item.meetingLinkOrVenue && (
                        <div style={{ marginTop: '0.35rem', fontSize: '0.8rem' }}>
                          <span style={{ color: '#818cf8' }}>Link/Venue:</span> {item.meetingLinkOrVenue}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Recent Offers Card */}
            <div className="glass-card">
              <h3 style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Award size={18} color="#10b981" /> Received Job Offers
              </h3>
              {offers.length === 0 ? (
                <p style={{ fontSize: '0.9rem' }}>No offers received yet.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {offers.map(off => (
                    <div
                      key={off.id}
                      style={{
                        padding: '0.85rem',
                        borderRadius: '8px',
                        background: 'rgba(16, 185, 129, 0.05)',
                        border: '1px solid rgba(16, 185, 129, 0.2)',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <h4 style={{ margin: 0, color: '#fff' }}>{off.companyName}</h4>
                          <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                            {off.designation} — <strong style={{ color: '#34d399' }}>{off.ctcLpa} LPA</strong>
                          </div>
                        </div>
                        <span className={`badge ${off.status === 'ACCEPTED' ? 'badge-success' : off.status === 'PENDING' ? 'badge-warning' : 'badge-neutral'}`}>
                          {off.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: PROFILE & RESUME */}
      {tab === 'profile' && (
        <div className="grid-cols-2">
          {/* Academic & Personal Card */}
          <div className="glass-card">
            <h3 style={{ marginBottom: '1.25rem' }}>Academic Profile</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255, 255, 255, 0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Cumulative CGPA:</span>
                <strong style={{ color: '#fff' }}>{profile?.cgpa || '0.0'} / 10.0</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255, 255, 255, 0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>Active Backlogs:</span>
                <strong style={{ color: profile?.activeBacklogs > 0 ? '#ef4444' : '#10b981' }}>{profile?.activeBacklogs || 0}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255, 255, 255, 0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>10th Standard %:</span>
                <strong style={{ color: '#fff' }}>{profile?.tenthPercentage || '0.0'}%</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255, 255, 255, 0.05)', paddingBottom: '0.5rem' }}>
                <span style={{ color: '#94a3b8' }}>12th / Diploma %:</span>
                <strong style={{ color: '#fff' }}>{profile?.twelfthPercentage || profile?.diplomaPercentage || 'N/A'}%</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#94a3b8' }}>Academic Gap Years:</span>
                <strong style={{ color: '#fff' }}>{profile?.gapYears || 0}</strong>
              </div>
            </div>

            {/* Skills Tags */}
            <div style={{ marginTop: '1.75rem' }}>
              <h4 style={{ marginBottom: '0.75rem' }}>Verified Technical Skills</h4>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '1rem' }}>
                {profile?.skills && profile.skills.length > 0 ? (
                  profile.skills.map(s => (
                    <span
                      key={s.id}
                      className="badge badge-primary"
                      style={{ padding: '0.35rem 0.65rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}
                    >
                      {s.skillName}
                      <Trash2
                        size={12}
                        onClick={() => handleDeleteSkill(s.id)}
                        style={{ cursor: 'pointer', opacity: 0.7 }}
                      />
                    </span>
                  ))
                ) : (
                  <span style={{ color: '#64748b', fontSize: '0.85rem' }}>No skills added yet.</span>
                )}
              </div>

              {/* Add Skill Form */}
              <form onSubmit={handleAddSkill} style={{ display: 'flex', gap: '0.5rem' }}>
                <input
                  type="text"
                  placeholder="e.g. Docker, Python, AWS"
                  value={newSkill}
                  onChange={e => setNewSkill(e.target.value)}
                  className="form-input"
                  style={{ padding: '0.5rem 0.85rem' }}
                />
                <button type="submit" className="btn btn-primary btn-sm">
                  <Plus size={16} /> Add
                </button>
              </form>
            </div>
          </div>

          {/* Secure Resume Storage Card */}
          <div className="glass-card">
            <h3 style={{ marginBottom: '1.25rem' }}>Resume Management</h3>
            {profile?.hasResume ? (
              <div
                style={{
                  padding: '1.25rem',
                  borderRadius: '10px',
                  background: 'rgba(16, 185, 129, 0.1)',
                  border: '1px solid rgba(16, 185, 129, 0.3)',
                  marginBottom: '1.5rem',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                  <FileText size={24} color="#10b981" />
                  <div>
                    <strong style={{ color: '#fff', fontSize: '0.95rem' }}>{profile.resumeFilename}</strong>
                    <div style={{ fontSize: '0.8rem', color: '#a7f3d0' }}>Active campus placement resume verified</div>
                  </div>
                </div>
                <a
                  href={studentApi.downloadResume()}
                  target="_blank"
                  rel="noreferrer"
                  className="btn btn-secondary btn-sm"
                  style={{ marginTop: '0.5rem', display: 'inline-flex', gap: '0.4rem' }}
                >
                  <ExternalLink size={14} /> View / Download Stored PDF
                </a>
              </div>
            ) : (
              <div
                style={{
                  padding: '1rem',
                  borderRadius: '10px',
                  background: 'rgba(245, 158, 11, 0.1)',
                  border: '1px solid rgba(245, 158, 11, 0.3)',
                  color: '#fbbf24',
                  fontSize: '0.85rem',
                  marginBottom: '1.5rem',
                  display: 'flex',
                  gap: '0.5rem',
                  alignItems: 'center',
                }}
              >
                <AlertCircle size={20} />
                <span>Upload a PDF resume (required before applying to campus drives).</span>
              </div>
            )}

            {/* Upload PDF Form */}
            <form onSubmit={handleResumeUpload} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Upload New PDF Resume (Max 5MB)</label>
                <input
                  type="file"
                  accept="application/pdf"
                  required
                  onChange={e => setResumeFile(e.target.files[0])}
                  className="form-input"
                  style={{ padding: '0.5rem' }}
                />
              </div>
              <button
                type="submit"
                disabled={uploadingResume || !resumeFile}
                className="btn btn-primary"
              >
                <Upload size={16} />
                {uploadingResume ? 'Encrypting & Uploading...' : 'Upload & Snapshot Resume'}
              </button>
            </form>
          </div>
        </div>
      )}

      {/* TAB 3: PLACEMENT DRIVES (JOBS) */}
      {tab === 'jobs' && (
        <div>
          <div style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ margin: 0 }}>Published Campus Placement Drives</h3>
            <p style={{ fontSize: '0.85rem' }}>Browse open opportunities and run real-time automated eligibility checks.</p>
          </div>

          <div className="grid-cols-2">
            {jobs.map(job => (
              <div key={job.id} className="glass-card interactive">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                  <div>
                    <h4 style={{ margin: 0, color: '#fff', fontSize: '1.15rem' }}>{job.title}</h4>
                    <div style={{ fontSize: '0.85rem', color: '#818cf8', fontWeight: 600 }}>
                      {job.company?.name || 'Partner Company'}
                    </div>
                  </div>
                  <span className="badge badge-primary">{job.jobType}</span>
                </div>

                <div style={{ display: 'flex', gap: '1rem', margin: '0.75rem 0', fontSize: '0.85rem', color: '#cbd5e1' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                    <DollarSign size={14} color="#10b981" /> <strong>{job.salaryPackageLpa} LPA</strong>
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                    <MapPin size={14} color="#06b6d4" /> {job.location}
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                    <Clock size={14} color="#f59e0b" /> Deadline: {new Date(job.applicationDeadline).toLocaleDateString()}
                  </span>
                </div>

                <p style={{ fontSize: '0.85rem', color: '#94a3b8', marginBottom: '1rem', lineHeight: 1.4 }}>
                  {job.description?.slice(0, 120)}...
                </p>

                {/* Eligibility Check Action */}
                <button
                  id={`check-eligibility-btn-${job.id}`}
                  onClick={() => handleCheckEligibility(job)}
                  className="btn btn-outline-primary"
                  style={{ width: '100%' }}
                >
                  <Sparkles size={16} /> Check Eligibility & Apply <ChevronRight size={16} />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 4: MY APPLICATIONS */}
      {tab === 'applications' && (
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.25rem' }}>My Application Tracking Timeline</h3>
          <div className="table-responsive">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Job Title</th>
                  <th>Company</th>
                  <th>Applied On</th>
                  <th>Recruitment Stage</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {applications.length === 0 ? (
                  <tr>
                    <td colSpan="5" style={{ textAlign: 'center', color: '#64748b' }}>
                      No applications submitted yet.
                    </td>
                  </tr>
                ) : (
                  applications.map(app => (
                    <tr key={app.id}>
                      <td style={{ fontWeight: 600, color: '#fff' }}>{app.jobTitle}</td>
                      <td>{app.companyName}</td>
                      <td>{new Date(app.appliedAt).toLocaleDateString()}</td>
                      <td>
                        <span style={{ color: '#cbd5e1', fontSize: '0.85rem' }}>
                          {app.currentRound || 'Application Received'}
                        </span>
                      </td>
                      <td>
                        <span className={`badge ${
                          app.status === 'OFFER_ACCEPTED' ? 'badge-success' :
                          app.status === 'OFFER_EXTENDED' ? 'badge-warning' :
                          app.status === 'REJECTED' ? 'badge-danger' :
                          app.status === 'TECHNICAL_INTERVIEW' ? 'badge-primary' : 'badge-neutral'
                        }`}>
                          {app.status}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: INTERVIEW CALENDAR */}
      {tab === 'interviews' && (
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.25rem' }}>Scheduled Assessment & Interview Slots</h3>
          <div className="table-responsive">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Round</th>
                  <th>Job & Company</th>
                  <th>Mode / Venue</th>
                  <th>Interviewer</th>
                  <th>Date & Time</th>
                  <th>Result</th>
                </tr>
              </thead>
              <tbody>
                {interviews.length === 0 ? (
                  <tr>
                    <td colSpan="6" style={{ textAlign: 'center', color: '#64748b' }}>
                      No interview rounds scheduled.
                    </td>
                  </tr>
                ) : (
                  interviews.map(i => (
                    <tr key={i.id}>
                      <td style={{ fontWeight: 600, color: '#fff' }}>{i.roundName}</td>
                      <td>
                        {i.jobTitle} <br />
                        <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{i.companyName}</span>
                      </td>
                      <td>{i.meetingLinkOrVenue}</td>
                      <td>{i.interviewerName}</td>
                      <td>{new Date(i.scheduledAt).toLocaleString()}</td>
                      <td>
                        <span className={`badge ${i.result === 'CLEARED' ? 'badge-success' : i.result === 'REJECTED' ? 'badge-danger' : 'badge-warning'}`}>
                          {i.result}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 6: JOB OFFERS */}
      {tab === 'offers' && (
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.25rem' }}>Formal Employment & Internship Offers</h3>
          {offers.length === 0 ? (
            <p style={{ color: '#64748b' }}>No employment offers extended yet.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              {offers.map(off => (
                <div
                  key={off.id}
                  style={{
                    padding: '1.5rem',
                    borderRadius: '12px',
                    background: off.status === 'ACCEPTED' ? 'rgba(16, 185, 129, 0.08)' : 'rgba(255, 255, 255, 0.03)',
                    border: `1px solid ${off.status === 'ACCEPTED' ? 'rgba(16, 185, 129, 0.4)' : 'rgba(255, 255, 255, 0.1)'}`,
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1rem' }}>
                    <div>
                      <span className="badge badge-primary" style={{ marginBottom: '0.4rem' }}>Official Offer Letter</span>
                      <h3 style={{ margin: 0, color: '#fff' }}>{off.designation}</h3>
                      <div style={{ fontSize: '0.95rem', color: '#818cf8', fontWeight: 600 }}>{off.companyName}</div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#34d399', fontFamily: 'var(--font-display)' }}>
                        {off.ctcLpa} LPA
                      </div>
                      <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                        Valid Until: {off.validUntil}
                      </div>
                    </div>
                  </div>

                  {off.notes && (
                    <p style={{ fontSize: '0.85rem', color: '#cbd5e1', marginBottom: '1rem', fontStyle: 'italic' }}>
                      "{off.notes}"
                    </p>
                  )}

                  {/* Accept / Decline Action Buttons */}
                  {off.status === 'PENDING' && (
                    <div style={{ display: 'flex', gap: '0.75rem', borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: '1rem' }}>
                      <button
                        id={`accept-offer-btn-${off.id}`}
                        onClick={() => handleOfferResponse(off.id, 'ACCEPTED')}
                        disabled={respondingOfferId === off.id}
                        className="btn btn-success"
                      >
                        <CheckCircle size={16} /> Accept Employment Offer
                      </button>
                      <button
                        id={`decline-offer-btn-${off.id}`}
                        onClick={() => handleOfferResponse(off.id, 'DECLINED')}
                        disabled={respondingOfferId === off.id}
                        className="btn btn-danger"
                      >
                        Decline Offer
                      </button>
                    </div>
                  )}

                  {off.status === 'ACCEPTED' && (
                    <div style={{ color: '#34d399', fontWeight: 600, fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      <CheckCircle size={18} /> Offer Accepted on {new Date(off.responseDate).toLocaleDateString()}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Real-Time Automated Eligibility Evaluation Modal */}
      <EligibilityModal
        job={selectedJob}
        report={eligibilityReport}
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onApply={handleApply}
        applying={applying}
      />
    </div>
  );
}
