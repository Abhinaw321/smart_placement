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
import Button from '../components/ui/Button';
import Card, { CardHeader, CardTitle } from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import StatCard from '../components/ui/StatCard';
import EmptyState from '../components/ui/EmptyState';
import Stepper from '../components/ui/Stepper';
import {
  Send,
  CheckCircle2,
  Calendar,
  Award,
  Briefcase,
  FileText,
  ExternalLink,
  MapPin,
  Clock,
  Check,
  X,
  Upload,
  Plus,
  Trash2,
} from 'lucide-react';

function ProfileRing({ percentage = 100, size = 48, strokeWidth = 3.5 }) {
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (percentage / 100) * circumference;

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
      <div style={{ position: 'relative', width: size, height: size }}>
        <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="var(--border)"
            strokeWidth={strokeWidth}
            fill="transparent"
          />
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="var(--accent)"
            strokeWidth={strokeWidth}
            fill="transparent"
            strokeDasharray={circumference}
            strokeDashoffset={offset}
            strokeLinecap="round"
            style={{ transition: 'stroke-dashoffset 0.5s ease' }}
          />
        </svg>
        <div
          style={{
            position: 'absolute',
            inset: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '0.72rem',
            fontWeight: 700,
            fontFamily: 'var(--font-heading)',
            color: 'var(--text)',
          }}
        >
          {percentage}%
        </div>
      </div>
      <div>
        <div style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--text)' }}>
          Profile Strength
        </div>
        <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
          Ready for applications
        </div>
      </div>
    </div>
  );
}

export default function StudentPortal({ activeTab = 'overview', onTabChange }) {
  const [dashboard, setDashboard] = useState(null);
  const [profile, setProfile] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Action states
  const [applyingJobId, setApplyingJobId] = useState(null);
  const [respondingOfferId, setRespondingOfferId] = useState(null);
  const [resumeFile, setResumeFile] = useState(null);
  const [uploadingResume, setUploadingResume] = useState(false);
  const [newSkill, setNewSkill] = useState('');

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

  const checkEligibility = (job) => {
    if (!profile) return { eligible: true };
    const crit = job.eligibilityCriteria;
    if (!crit) return { eligible: true };

    if (crit.minCgpa && profile.cgpa < crit.minCgpa) {
      return {
        eligible: false,
        reason: `Not this one. Your CGPA is ${profile.cgpa}, they want ${crit.minCgpa}.`,
      };
    }
    if (crit.maxActiveBacklogs !== null && profile.activeBacklogs > crit.maxActiveBacklogs) {
      return {
        eligible: false,
        reason: `Requires max ${crit.maxActiveBacklogs} active backlogs (you have ${profile.activeBacklogs}).`,
      };
    }
    if (crit.allowedBranches && crit.allowedBranches.length > 0) {
      const pBranch = (profile.branch || '').toUpperCase();
      const matched = crit.allowedBranches.some((b) =>
        pBranch.includes(b.toUpperCase()) || b.toUpperCase().includes(pBranch)
      );
      if (!matched) {
        return {
          eligible: false,
          reason: `Branch mismatch. Open for ${crit.allowedBranches.join(', ')}.`,
        };
      }
    }
    return { eligible: true };
  };

  const handleApply = async (jobId) => {
    setApplyingJobId(jobId);
    try {
      await applicationApi.apply(jobId);
      await loadData();
    } catch (err) {
      alert(err.message || 'Failed to submit application.');
    } finally {
      setApplyingJobId(null);
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
          particleCount: 80,
          spread: 70,
          origin: { y: 0.6 },
          colors: ['#C6FF3D', '#FFFFFF', '#4ADE80'],
        });
      }
      await loadData();
    } catch (err) {
      alert(err.message || 'Action failed.');
    } finally {
      setRespondingOfferId(null);
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

  if (loading && !profile) {
    return (
      <div style={{ padding: '4rem 1rem', textAlign: 'center', color: 'var(--text-muted)' }}>
        Loading student dashboard...
      </div>
    );
  }

  // Calculate greeting subline counts
  const matchingDrivesCount = jobs.filter((j) => checkEligibility(j).eligible).length;
  const upcomingInterviewsCount = interviews.length;
  const appliedJobIds = new Set(applications.map((a) => a.jobId));

  // --- TAB 1: OVERVIEW PAGE ---
  if (activeTab === 'overview') {
    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {/* 1. Greeting Row */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '1rem',
          }}
        >
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 700, letterSpacing: '-0.03em' }}>
              Hey {profile?.firstName || 'Alex'}
            </h1>
            <p style={{ fontSize: '0.86rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
              {matchingDrivesCount} drives match you. {upcomingInterviewsCount} interview
              {upcomingInterviewsCount === 1 ? '' : 's'} coming up.
            </p>
          </div>

          <ProfileRing percentage={dashboard?.profileCompletionPercentage || 100} />
        </div>

        {/* 2. Stat Grid (4 cards, same neutral surface, white numbers, muted icons, ~115px tall) */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatCard
            label="Applied"
            value={dashboard?.totalApplicationsSubmitted ?? applications.length}
            icon={Send}
            subtext="Submitted applications"
          />
          <StatCard
            label="Shortlisted"
            value={
              dashboard?.shortlistedCount ??
              applications.filter((a) => a.status === 'SHORTLISTED' || a.status === 'TECHNICAL_INTERVIEW').length
            }
            icon={CheckCircle2}
            subtext="Advanced to rounds"
          />
          <StatCard
            label="Interviews"
            value={dashboard?.interviewsScheduled ?? interviews.length}
            icon={Calendar}
            subtext="Upcoming & conducted"
          />
          <StatCard
            label="Offers"
            value={dashboard?.offersReceived ?? offers.length}
            icon={Award}
            subtext={profile?.isPlaced ? 'Placement locked' : 'Official offer letters'}
          />
        </div>

        {/* 3. Two Columns Below (lg:grid-cols-3) */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column (span 2): "Your pipeline" */}
          <div className="lg:col-span-2">
            <Card style={{ height: '100%' }}>
              <CardHeader>
                <div>
                  <CardTitle>Your pipeline</CardTitle>
                  <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '0.15rem' }}>
                    Track active drive recruitment stages in real time
                  </p>
                </div>
                <Badge variant="accent">{applications.length} Active</Badge>
              </CardHeader>

              {applications.length === 0 ? (
                <EmptyState
                  icon={FileText}
                  title="No applications yet"
                  description="No applications yet. Go apply, future SDE."
                  actionLabel="Browse Drives"
                  onAction={() => onTabChange && onTabChange('drives')}
                />
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {applications.map((app) => (
                    <div
                      key={app.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '0.85rem 1rem',
                        background: 'var(--surface-elevated)',
                        border: '1px solid var(--border)',
                        borderRadius: 'var(--radius-md)',
                        gap: '1rem',
                        flexWrap: 'wrap',
                      }}
                    >
                      {/* Left: Logo Initial + Role & Company */}
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', minWidth: '180px' }}>
                        <div
                          style={{
                            width: '36px',
                            height: '36px',
                            borderRadius: 'var(--radius-md)',
                            background: 'var(--surface)',
                            border: '1px solid var(--border)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '0.9rem',
                            fontWeight: 700,
                            fontFamily: 'var(--font-heading)',
                            color: 'var(--text)',
                            flexShrink: 0,
                          }}
                        >
                          {app.companyName ? app.companyName.charAt(0).toUpperCase() : 'C'}
                        </div>
                        <div>
                          <div style={{ fontSize: '0.86rem', fontWeight: 600, color: 'var(--text)' }}>
                            {app.jobTitle}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            {app.companyName}
                          </div>
                        </div>
                      </div>

                      {/* Middle: Horizontal Stepper */}
                      <Stepper
                        currentStep={app.currentRound || app.status}
                        status={app.status}
                        style={{ flexShrink: 0 }}
                      />

                      {/* Right: Status Badge */}
                      <Badge
                        variant={
                          app.status === 'OFFER_MADE'
                            ? 'green'
                            : app.status === 'REJECTED'
                            ? 'red'
                            : app.status === 'SHORTLISTED' || app.status === 'TECHNICAL_INTERVIEW'
                            ? 'amber'
                            : 'grey'
                        }
                      >
                        {app.status === 'TECHNICAL_INTERVIEW'
                          ? 'Interview'
                          : app.status === 'OFFER_MADE'
                          ? 'Offer'
                          : app.status === 'SHORTLISTED'
                          ? 'Shortlisted'
                          : app.status === 'REJECTED'
                          ? 'Rejected'
                          : 'Applied'}
                      </Badge>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>

          {/* Right Column (span 1): "Next up" and "Offers" */}
          <div className="lg:col-span-1" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            {/* Card 1: Next up */}
            <Card>
              <CardHeader style={{ marginBottom: '0.75rem' }}>
                <CardTitle>Next up</CardTitle>
                <Calendar size={15} style={{ color: 'var(--text-muted)' }} />
              </CardHeader>

              {interviews.length === 0 ? (
                <EmptyState
                  icon={Calendar}
                  title="No interviews yet"
                  description="No interviews lined up. Time to grind LeetCode."
                  style={{ padding: '1.5rem 1rem' }}
                />
              ) : (
                <div>
                  {interviews.slice(0, 1).map((item) => {
                    const d = new Date(item.scheduledAt);
                    const month = d.toLocaleString('en-US', { month: 'short' }).toUpperCase();
                    const day = d.getDate();
                    const time = d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' });

                    return (
                      <div key={item.id}>
                        <div style={{ display: 'flex', gap: '0.85rem', alignItems: 'center' }}>
                          {/* Clean date block */}
                          <div
                            style={{
                              width: '46px',
                              height: '46px',
                              borderRadius: 'var(--radius-md)',
                              background: 'var(--surface-elevated)',
                              border: '1px solid var(--border)',
                              display: 'flex',
                              flexDirection: 'column',
                              alignItems: 'center',
                              justifyContent: 'center',
                              flexShrink: 0,
                            }}
                          >
                            <span style={{ fontSize: '0.62rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                              {month}
                            </span>
                            <span
                              style={{
                                fontSize: '1.05rem',
                                fontWeight: 700,
                                fontFamily: 'var(--font-heading)',
                                color: 'var(--text)',
                                lineHeight: 1,
                              }}
                            >
                              {day}
                            </span>
                          </div>

                          <div style={{ overflow: 'hidden' }}>
                            <div
                              style={{
                                fontSize: '0.86rem',
                                fontWeight: 600,
                                color: 'var(--text)',
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                              }}
                            >
                              {item.roundName}
                            </div>
                            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                              {item.companyName} • {time}
                            </div>
                          </div>
                        </div>

                        <Button
                          variant="secondary"
                          size="sm"
                          icon={ExternalLink}
                          style={{ width: '100%', marginTop: '0.85rem' }}
                          onClick={() => {
                            if (item.meetingLinkOrVenue) {
                              window.open(item.meetingLinkOrVenue, '_blank');
                            }
                          }}
                        >
                          Join meeting
                        </Button>
                      </div>
                    );
                  })}
                </div>
              )}
            </Card>

            {/* Card 2: Offers */}
            <Card>
              <CardHeader style={{ marginBottom: '0.75rem' }}>
                <CardTitle>Offers</CardTitle>
                <Award size={15} style={{ color: 'var(--text-muted)' }} />
              </CardHeader>

              {offers.length === 0 ? (
                <EmptyState
                  icon={Award}
                  title="No offers yet"
                  description="No offers yet. The grind continues."
                  style={{ padding: '1.5rem 1rem' }}
                />
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
                  <p style={{ fontSize: '0.78rem', color: 'var(--accent)', fontWeight: 500 }}>
                    You got an offer. Go call your mom.
                  </p>

                  {offers.map((off) => (
                    <div
                      key={off.id}
                      style={{
                        padding: '0.85rem',
                        background: 'var(--surface-elevated)',
                        border: '1px solid var(--border)',
                        borderRadius: 'var(--radius-md)',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                          <div style={{ fontSize: '0.88rem', fontWeight: 600, color: 'var(--text)' }}>
                            {off.companyName}
                          </div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            {off.designation}
                          </div>
                        </div>
                        <div
                          style={{
                            fontFamily: 'var(--font-heading)',
                            fontSize: '1.15rem',
                            fontWeight: 700,
                            color: '#FFFFFF',
                          }}
                        >
                          {off.ctcLpa} LPA
                        </div>
                      </div>

                      {off.status === 'PENDING' && (
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginTop: '0.85rem' }}>
                          <Button
                            variant="primary"
                            size="sm"
                            loading={respondingOfferId === off.id}
                            onClick={() => handleOfferResponse(off.id, 'ACCEPTED')}
                          >
                            Accept
                          </Button>
                          <Button
                            variant="secondary"
                            size="sm"
                            loading={respondingOfferId === off.id}
                            onClick={() => handleOfferResponse(off.id, 'DECLINED')}
                          >
                            Decline
                          </Button>
                        </div>
                      )}

                      {off.status === 'ACCEPTED' && (
                        <div style={{ marginTop: '0.65rem' }}>
                          <Badge variant="green">Offer Accepted</Badge>
                        </div>
                      )}

                      {off.status === 'DECLINED' && (
                        <div style={{ marginTop: '0.65rem' }}>
                          <Badge variant="red">Offer Declined</Badge>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>
        </div>

        {/* 4. Below: "Drives for you" */}
        <div style={{ marginTop: '0.5rem' }}>
          <div style={{ marginBottom: '1rem' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 600, letterSpacing: '-0.02em' }}>
              Drives for you
            </h2>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              Eligible openings tailored to your branch and academic profile
            </p>
          </div>

          {jobs.length === 0 ? (
            <EmptyState
              icon={Briefcase}
              title="No active placement drives"
              description="Check back soon as recruitment partners publish new drives."
            />
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {jobs.map((job) => {
                const { eligible, reason } = checkEligibility(job);
                const isApplied = appliedJobIds.has(job.id);

                return (
                  <Card
                    key={job.id}
                    style={{
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between',
                      height: '100%',
                    }}
                  >
                    <div>
                      {/* Company Logo Initial + Name */}
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '0.75rem' }}>
                        <div
                          style={{
                            width: '32px',
                            height: '32px',
                            borderRadius: 'var(--radius-md)',
                            background: 'var(--surface-elevated)',
                            border: '1px solid var(--border)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '0.82rem',
                            fontWeight: 700,
                            fontFamily: 'var(--font-heading)',
                            color: 'var(--text)',
                            flexShrink: 0,
                          }}
                        >
                          {job.companyName ? job.companyName.charAt(0).toUpperCase() : 'C'}
                        </div>
                        <div style={{ overflow: 'hidden' }}>
                          <div
                            style={{
                              fontSize: '0.75rem',
                              color: 'var(--text-muted)',
                              whiteSpace: 'nowrap',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                            }}
                          >
                            {job.companyName}
                          </div>
                          <div
                            style={{
                              fontSize: '0.9rem',
                              fontWeight: 600,
                              color: 'var(--text)',
                              whiteSpace: 'nowrap',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                            }}
                          >
                            {job.title}
                          </div>
                        </div>
                      </div>

                      {/* Package */}
                      <div
                        style={{
                          fontFamily: 'var(--font-heading)',
                          fontSize: '1.25rem',
                          fontWeight: 700,
                          color: '#FFFFFF',
                          marginBottom: '0.45rem',
                        }}
                      >
                        {job.salaryPackageLpa} LPA
                      </div>

                      {/* Location & Job Type */}
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.85rem' }}>
                        {job.location} • {job.jobType ? job.jobType.replace('_', ' ') : 'Full Time'}
                      </div>

                      {/* Eligibility Badge */}
                      <div style={{ marginBottom: '1rem' }}>
                        {eligible ? (
                          <Badge variant="green">Eligible</Badge>
                        ) : (
                          <div>
                            <Badge variant="red">Not eligible</Badge>
                            <div
                              style={{
                                fontSize: '0.72rem',
                                color: 'var(--text-muted)',
                                marginTop: '0.35rem',
                                lineHeight: 1.35,
                              }}
                            >
                              {reason}
                            </div>
                          </div>
                        )}
                      </div>
                    </div>

                    {/* Apply Button */}
                    <div>
                      {isApplied ? (
                        <Button variant="secondary" size="sm" disabled style={{ width: '100%' }}>
                          Applied
                        </Button>
                      ) : (
                        <Button
                          variant="primary"
                          size="sm"
                          disabled={!eligible}
                          loading={applyingJobId === job.id}
                          onClick={() => handleApply(job.id)}
                          style={{ width: '100%' }}
                        >
                          {eligible ? 'Apply now' : 'Not eligible'}
                        </Button>
                      )}
                    </div>
                  </Card>
                );
              })}
            </div>
          )}
        </div>
      </div>
    );
  }

  // --- SUB-TABS (Clean minimal views using design system) ---
  return (
    <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Drives Tab */}
      {activeTab === 'drives' && (
        <div>
          <div style={{ marginBottom: '1rem' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>All Placement Drives</h2>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              Browse active campus hiring drives across visiting companies
            </p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {jobs.map((job) => {
              const { eligible, reason } = checkEligibility(job);
              const isApplied = appliedJobIds.has(job.id);
              return (
                <Card key={job.id} style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                  <div>
                    <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{job.companyName}</div>
                    <div style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--text)', margin: '0.2rem 0 0.5rem' }}>
                      {job.title}
                    </div>
                    <div style={{ fontFamily: 'var(--font-heading)', fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '0.5rem' }}>
                      {job.salaryPackageLpa} LPA
                    </div>
                    <div style={{ fontSize: '0.76rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                      {job.location} • {job.jobType}
                    </div>
                    <div style={{ marginBottom: '1rem' }}>
                      {eligible ? (
                        <Badge variant="green">Eligible</Badge>
                      ) : (
                        <div>
                          <Badge variant="red">Not eligible</Badge>
                          <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>{reason}</div>
                        </div>
                      )}
                    </div>
                  </div>
                  <Button
                    variant="primary"
                    size="sm"
                    disabled={!eligible || isApplied}
                    loading={applyingJobId === job.id}
                    onClick={() => handleApply(job.id)}
                    style={{ width: '100%' }}
                  >
                    {isApplied ? 'Applied' : eligible ? 'Apply now' : 'Not eligible'}
                  </Button>
                </Card>
              );
            })}
          </div>
        </div>
      )}

      {/* Applications Tab */}
      {activeTab === 'applications' && (
        <Card>
          <CardHeader>
            <CardTitle>Application History</CardTitle>
            <Badge variant="accent">{applications.length} Total</Badge>
          </CardHeader>
          {applications.length === 0 ? (
            <EmptyState
              icon={FileText}
              title="No applications yet"
              description="No applications yet. Go apply, future SDE."
            />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {applications.map((app) => (
                <div
                  key={app.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.85rem 1rem',
                    background: 'var(--surface-elevated)',
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-md)',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, color: 'var(--text)' }}>{app.jobTitle}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{app.companyName}</div>
                  </div>
                  <Stepper currentStep={app.currentRound || app.status} status={app.status} />
                  <Badge variant={app.status === 'OFFER_MADE' ? 'green' : app.status === 'REJECTED' ? 'red' : 'grey'}>
                    {app.status}
                  </Badge>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Interviews Tab */}
      {activeTab === 'interviews' && (
        <Card>
          <CardHeader>
            <CardTitle>Scheduled Interviews</CardTitle>
            <Badge variant="accent">{interviews.length} Scheduled</Badge>
          </CardHeader>
          {interviews.length === 0 ? (
            <EmptyState
              icon={Calendar}
              title="No interviews scheduled"
              description="No interviews lined up. Time to grind LeetCode."
            />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {interviews.map((item) => (
                <div
                  key={item.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.85rem 1rem',
                    background: 'var(--surface-elevated)',
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-md)',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, color: 'var(--text)' }}>{item.roundName}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      {item.companyName} • {new Date(item.scheduledAt).toLocaleString()}
                    </div>
                  </div>
                  {item.meetingLinkOrVenue && (
                    <Button
                      variant="secondary"
                      size="sm"
                      icon={ExternalLink}
                      onClick={() => window.open(item.meetingLinkOrVenue, '_blank')}
                    >
                      Join
                    </Button>
                  )}
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Offers Tab */}
      {activeTab === 'offers' && (
        <Card>
          <CardHeader>
            <CardTitle>Offer Letters</CardTitle>
            <Badge variant="accent">{offers.length} Received</Badge>
          </CardHeader>
          {offers.length === 0 ? (
            <EmptyState
              icon={Award}
              title="No offers yet"
              description="No offers yet. The grind continues."
            />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
              {offers.map((off) => (
                <div
                  key={off.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.85rem 1rem',
                    background: 'var(--surface-elevated)',
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-md)',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, color: 'var(--text)' }}>{off.companyName}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{off.designation}</div>
                  </div>
                  <div style={{ fontFamily: 'var(--font-heading)', fontSize: '1.2rem', fontWeight: 700, color: '#FFFFFF' }}>
                    {off.ctcLpa} LPA
                  </div>
                  <Badge variant={off.status === 'ACCEPTED' ? 'green' : 'amber'}>{off.status}</Badge>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Profile Tab */}
      {activeTab === 'profile' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <CardTitle style={{ marginBottom: '1rem' }}>Academic Record</CardTitle>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.65rem', fontSize: '0.84rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
                <span style={{ color: 'var(--text-muted)' }}>CGPA</span>
                <strong style={{ color: '#FFFFFF', fontFamily: 'var(--font-heading)' }}>{profile?.cgpa} / 10.0</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
                <span style={{ color: 'var(--text-muted)' }}>Branch</span>
                <span style={{ color: 'var(--text)' }}>{profile?.branch}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
                <span style={{ color: 'var(--text-muted)' }}>Class Of</span>
                <span style={{ color: 'var(--text)' }}>{profile?.graduationYear}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
                <span style={{ color: 'var(--text-muted)' }}>Active Backlogs</span>
                <span style={{ color: profile?.activeBacklogs > 0 ? 'var(--status-red-fg)' : 'var(--status-green-fg)' }}>
                  {profile?.activeBacklogs ?? 0}
                </span>
              </div>
            </div>
          </Card>

          <Card>
            <CardTitle style={{ marginBottom: '1rem' }}>Verified Skills</CardTitle>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem', marginBottom: '1rem' }}>
              {profile?.skills?.map((s) => (
                <Badge key={s.id} variant="accent" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  {s.skillName}
                  <button
                    onClick={() => handleDeleteSkill(s.id)}
                    style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', padding: 0 }}
                  >
                    <X size={11} />
                  </button>
                </Badge>
              ))}
            </div>

            <form onSubmit={handleAddSkill} style={{ display: 'flex', gap: '0.5rem' }}>
              <input
                className="input-base"
                placeholder="Add skill (e.g. Next.js)..."
                value={newSkill}
                onChange={(e) => setNewSkill(e.target.value)}
              />
              <Button type="submit" variant="secondary" size="sm">
                Add
              </Button>
            </form>
          </Card>
        </div>
      )}
    </div>
  );
}
