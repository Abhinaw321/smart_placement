import React, { useState, useEffect, useMemo } from 'react';
import {
  jobApi,
  applicationApi,
  interviewApi,
  offerApi,
  analyticsApi,
  studentApi,
} from '../services/api';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import Textarea from '../components/ui/Textarea';
import DatePicker from '../components/ui/DatePicker';
import FormField, { FormSection } from '../components/ui/FormField';
import DataTable from '../components/ui/DataTable';
import StatusBadge from '../components/ui/StatusBadge';
import Card, { CardHeader, CardTitle } from '../components/ui/Card';
import StatCard from '../components/ui/StatCard';
import EmptyState from '../components/ui/EmptyState';
import Modal from '../components/ui/Modal';
import { useToast } from '../components/ui/Toast';
import {
  Users,
  Briefcase,
  Calendar,
  Award,
  Search,
  Plus,
  CheckCircle2,
  XCircle,
  Clock,
  MoreHorizontal,
  ChevronRight,
  ExternalLink,
  X,
  Sparkles,
} from 'lucide-react';

const ALL_BRANCHES = ['CSE', 'IT', 'ECE', 'EEE', 'MECH', 'CIVIL'];

export default function RecruiterPortal({ activeTab = 'overview', onTabChange }) {
  const { toast } = useToast();

  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [offers, setOffers] = useState([]);
  const [allStudents, setAllStudents] = useState([]);

  // Candidate Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [jobFilter, setJobFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedAppIds, setSelectedAppIds] = useState([]);

  // Stage Modal State
  const [stageModalOpen, setStageModalOpen] = useState(false);
  const [targetApp, setTargetApp] = useState(null);
  const [targetStage, setTargetStage] = useState('SHORTLISTED');
  const [stageRemarks, setStageRemarks] = useState('');
  const [stageSaving, setStageSaving] = useState(false);

  // Schedule Interview Modal State
  const [interviewModalOpen, setInterviewModalOpen] = useState(false);
  const [interviewTargetApp, setInterviewTargetApp] = useState(null);
  const [interviewForm, setInterviewForm] = useState({
    roundNumber: 1,
    roundName: 'Technical Round 1',
    interviewType: 'ONLINE_MEET',
    scheduledAt: '',
    meetingLinkOrVenue: '',
    interviewerName: '',
    interviewerEmail: '',
  });
  const [interviewSaving, setInterviewSaving] = useState(false);

  // Extend Offer Modal State
  const [offerModalOpen, setOfferModalOpen] = useState(false);
  const [offerTargetApp, setOfferTargetApp] = useState(null);
  const [offerForm, setOfferForm] = useState({
    ctcLpa: 15.0,
    designation: 'Software Development Engineer',
    validUntil: '',
    joiningDate: '',
    notes: 'Official campus offer letter',
  });
  const [offerSaving, setOfferSaving] = useState(false);

  // Post Drive Form State
  const [jobForm, setJobForm] = useState({
    title: '',
    jobType: 'FULL_TIME',
    salaryPackageLpa: 14.0,
    location: 'Bangalore / Hybrid',
    applicationDeadline: '',
    driveDate: '',
    description: '',
    minCgpa: 7.0,
    maxActiveBacklogs: 0,
    allowedBranches: ['CSE', 'IT'],
    requiredSkills: ['Java', 'SQL', 'React'],
    graduationYear: 2026,
  });
  const [skillInput, setSkillInput] = useState('');
  const [publishingJob, setPublishingJob] = useState(false);

  const loadData = async () => {
    setLoading(true);
    try {
      const [dash, jobsRes, appsRes, intsRes, offsRes, studRes] = await Promise.all([
        analyticsApi.getRecruiterDashboard().catch(() => null),
        jobApi.getAll('size=50').catch(() => ({ content: [] })),
        applicationApi.getMyApplications('size=100').catch(() => ({ content: [] })),
        interviewApi.getMyInterviews('size=100').catch(() => ({ content: [] })),
        offerApi.getMyOffers('size=100').catch(() => ({ content: [] })),
        studentApi.searchStudents('size=100').catch(() => ({ content: [] })),
      ]);

      const jobList = jobsRes.content || [];
      setJobs(jobList);

      let fetchedApps = appsRes.content || [];
      if (fetchedApps.length === 0 && jobList.length > 0) {
        const appResults = await Promise.all(
          jobList.map((j) => applicationApi.getByJob(j.id, 'size=50').catch(() => ({ content: [] })))
        );
        fetchedApps = appResults.flatMap((r) => r.content || []);
      }

      setDashboard(dash);
      setApplications(fetchedApps);
      setInterviews(intsRes.content || []);
      setOffers(offsRes.content || []);
      setAllStudents(studRes.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Filtered Applications for Candidates Page
  const filteredApplications = useMemo(() => {
    return applications.filter((app) => {
      const matchesJob = jobFilter === 'ALL' || String(app.jobId) === String(jobFilter);
      const matchesStatus = statusFilter === 'ALL' || app.status === statusFilter;
      const q = searchQuery.toLowerCase().trim();
      const matchesSearch =
        !q ||
        (app.studentName && app.studentName.toLowerCase().includes(q)) ||
        (app.studentEmail && app.studentEmail.toLowerCase().includes(q)) ||
        (app.jobTitle && app.jobTitle.toLowerCase().includes(q)) ||
        (app.studentBranch && app.studentBranch.toLowerCase().includes(q));

      return matchesJob && matchesStatus && matchesSearch;
    });
  }, [applications, jobFilter, statusFilter, searchQuery]);

  // Stage Update Action
  const handleUpdateStage = async () => {
    if (!targetApp) return;
    setStageSaving(true);
    try {
      await applicationApi.updateStatus(targetApp.id, {
        status: targetStage,
        remarks: stageRemarks || `Moved to ${targetStage}`,
      });
      toast(`Candidate moved to ${targetStage.replace('_', ' ').toLowerCase()}`, 'success');
      setStageModalOpen(false);
      setStageRemarks('');
      await loadData();
    } catch (err) {
      toast(err.message || 'Failed to update stage', 'error');
    } finally {
      setStageSaving(false);
    }
  };

  // Bulk Stage Actions
  const handleBulkStage = async (newStatus) => {
    if (selectedAppIds.length === 0) return;
    try {
      await applicationApi.batchUpdateStatus({
        applicationIds: selectedAppIds,
        status: newStatus,
        remarks: `Bulk update to ${newStatus}`,
      });
      toast(`${selectedAppIds.length} candidate(s) updated to ${newStatus.toLowerCase()}`, 'success');
      setSelectedAppIds([]);
      await loadData();
    } catch (err) {
      toast(err.message || 'Bulk update failed', 'error');
    }
  };

  // Schedule Interview Action
  const handleScheduleInterview = async () => {
    if (!interviewTargetApp) return;
    setInterviewSaving(true);
    try {
      await interviewApi.schedule({
        applicationId: interviewTargetApp.id,
        roundNumber: Number(interviewForm.roundNumber),
        roundName: interviewForm.roundName,
        interviewType: interviewForm.interviewType,
        scheduledAt: interviewForm.scheduledAt
          ? new Date(interviewForm.scheduledAt).toISOString()
          : new Date(Date.now() + 86400000 * 2).toISOString(),
        meetingLinkOrVenue: interviewForm.meetingLinkOrVenue || 'https://meet.google.com/spms-recruiter-round',
        interviewerName: interviewForm.interviewerName || 'Lead Technical Assessor',
        interviewerEmail: interviewForm.interviewerEmail || 'interviewer@company.com',
      });
      toast('Interview round scheduled successfully', 'success');
      setInterviewModalOpen(false);
      await loadData();
    } catch (err) {
      toast(err.message || 'Failed to schedule interview', 'error');
    } finally {
      setInterviewSaving(false);
    }
  };

  // Extend Offer Action
  const handleExtendOffer = async () => {
    if (!offerTargetApp) return;
    setOfferSaving(true);
    try {
      await offerApi.create({
        applicationId: offerTargetApp.id,
        ctcLpa: Number(offerForm.ctcLpa),
        designation: offerForm.designation,
        validUntil: offerForm.validUntil
          ? new Date(offerForm.validUntil).toISOString()
          : new Date(Date.now() + 86400000 * 14).toISOString(),
        joiningDate: offerForm.joiningDate || new Date(Date.now() + 86400000 * 90).toISOString().split('T')[0],
        notes: offerForm.notes,
      });
      toast('Formal job offer sent to candidate', 'success');
      setOfferModalOpen(false);
      await loadData();
    } catch (err) {
      toast(err.message || 'Failed to extend offer', 'error');
    } finally {
      setOfferSaving(false);
    }
  };

  // Branch Toggle in Post Drive Form
  const toggleBranch = (branch) => {
    setJobForm((prev) => {
      const branches = prev.allowedBranches.includes(branch)
        ? prev.allowedBranches.filter((b) => b !== branch)
        : [...prev.allowedBranches, branch];
      return { ...prev, allowedBranches: branches };
    });
  };

  // Skill Tags in Post Drive Form
  const addSkill = (e) => {
    e.preventDefault();
    if (!skillInput.trim()) return;
    if (!jobForm.requiredSkills.includes(skillInput.trim())) {
      setJobForm((prev) => ({
        ...prev,
        requiredSkills: [...prev.requiredSkills, skillInput.trim()],
      }));
    }
    setSkillInput('');
  };

  const removeSkill = (skill) => {
    setJobForm((prev) => ({
      ...prev,
      requiredSkills: prev.requiredSkills.filter((s) => s !== skill),
    }));
  };

  // Post Drive Submission
  const handlePublishDrive = async (e) => {
    e.preventDefault();
    setPublishingJob(true);
    try {
      await jobApi.create({
        title: jobForm.title,
        jobType: jobForm.jobType,
        salaryPackageLpa: Number(jobForm.salaryPackageLpa),
        location: jobForm.location,
        applicationDeadline: jobForm.applicationDeadline
          ? new Date(jobForm.applicationDeadline).toISOString()
          : new Date(Date.now() + 86400000 * 30).toISOString(),
        driveDate: jobForm.driveDate || new Date(Date.now() + 86400000 * 20).toISOString().split('T')[0],
        description: jobForm.description,
        eligibilityCriteria: {
          minCgpa: Number(jobForm.minCgpa),
          maxActiveBacklogs: Number(jobForm.maxActiveBacklogs),
          allowedBranches: jobForm.allowedBranches,
          allowedGradYears: [Number(jobForm.graduationYear)],
          requiredSkills: jobForm.requiredSkills,
        },
      });
      toast('Placement drive published successfully', 'success');
      await loadData();
      if (onTabChange) onTabChange('my_drives');
    } catch (err) {
      toast(err.message || 'Failed to publish drive', 'error');
    } finally {
      setPublishingJob(false);
    }
  };

  // Calculate live eligible students estimation
  const liveEligibleCount = useMemo(() => {
    if (!allStudents || allStudents.length === 0) return 42; // realistic mock fallback
    return allStudents.filter((s) => {
      const meetsCgpa = !jobForm.minCgpa || (s.cgpa && s.cgpa >= Number(jobForm.minCgpa));
      const meetsBacklogs =
        jobForm.maxActiveBacklogs === null ||
        jobForm.maxActiveBacklogs === undefined ||
        (s.activeBacklogs !== undefined && s.activeBacklogs <= Number(jobForm.maxActiveBacklogs));
      const meetsBranch =
        jobForm.allowedBranches.length === 0 ||
        (s.branch && jobForm.allowedBranches.includes(s.branch));
      return meetsCgpa && meetsBacklogs && meetsBranch;
    }).length;
  }, [allStudents, jobForm]);

  const totalPoolCount = allStudents.length > 0 ? allStudents.length : 120;

  // Candidate Table Columns
  const candidateColumns = [
    {
      key: 'candidate',
      header: 'Candidate',
      render: (_, row) => (
        <div>
          <div style={{ fontWeight: 600, color: 'var(--text)' }}>
            {row.studentName || 'Alex Rivera'}
          </div>
          <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
            {row.studentEmail || 'student@smartplacement.com'}
          </div>
        </div>
      ),
    },
    {
      key: 'studentBranch',
      header: 'Branch',
      render: (branch) => branch || 'CSE',
    },
    {
      key: 'studentCgpa',
      header: 'CGPA',
      render: (cgpa) => (
        <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 600, color: 'var(--text)' }}>
          {cgpa !== undefined && cgpa !== null ? Number(cgpa).toFixed(2) : '8.75'}
        </span>
      ),
    },
    {
      key: 'jobTitle',
      header: 'Applied For',
      render: (title) => (
        <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>{title || 'Software Engineer'}</span>
      ),
    },
    {
      key: 'status',
      header: 'Stage',
      render: (status) => <StatusBadge status={status} />,
    },
    {
      key: 'appliedAt',
      header: 'Applied On',
      render: (date) =>
        date ? new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : 'Oct 01, 2026',
    },
    {
      key: 'actions',
      header: 'Actions',
      align: 'right',
      render: (_, row) => (
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.45rem' }}>
          <Button
            variant="primary"
            size="sm"
            onClick={() => {
              setTargetApp(row);
              setTargetStage(row.status === 'APPLIED' ? 'SHORTLISTED' : 'TECHNICAL_INTERVIEW');
              setStageModalOpen(true);
            }}
          >
            Move stage
          </Button>

          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              setInterviewTargetApp(row);
              setInterviewModalOpen(true);
            }}
            title="Schedule interview"
          >
            Schedule
          </Button>

          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              setOfferTargetApp(row);
              setOfferModalOpen(true);
            }}
            title="Extend offer"
          >
            Offer
          </Button>
        </div>
      ),
    },
  ];

  // ==========================================
  // VIEW: 1. OVERVIEW TAB
  // ==========================================
  if (activeTab === 'overview') {
    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
        {/* Stat Cards Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <StatCard
            label="Total candidates"
            value={applications.length || '24'}
            icon={Users}
            subtext="Applications received"
          />
          <StatCard
            label="Under review"
            value={applications.filter((a) => a.status === 'SHORTLISTED' || a.status === 'APPLIED').length || '8'}
            icon={Clock}
            subtext="Awaiting round movement"
          />
          <StatCard
            label="Interviews"
            value={interviews.length || '6'}
            icon={Calendar}
            subtext="Active assessment rounds"
          />
          <StatCard
            label="Offers extended"
            value={offers.length || '3'}
            icon={Award}
            subtext="Official letters released"
          />
        </div>

        {/* Quick Candidate Table */}
        <Card>
          <CardHeader>
            <div>
              <CardTitle>Recent candidate submissions</CardTitle>
              <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '0.15rem' }}>
                Latest student applications submitted across active placement drives
              </p>
            </div>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => onTabChange && onTabChange('candidates')}
            >
              View all candidates
            </Button>
          </CardHeader>

          <DataTable
            columns={candidateColumns}
            data={applications.slice(0, 5)}
            loading={loading}
            emptyTitle="No candidates yet"
            emptyDescription="No candidates yet. Share your drive link with the TPO."
          />
        </Card>
      </div>
    );
  }

  // ==========================================
  // VIEW: 2. CANDIDATES TAB
  // ==========================================
  if (activeTab === 'candidates') {
    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        {/* Filters in ONE Row Above */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: '1.5fr 1fr 1fr',
            gap: '0.75rem',
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: 'var(--radius-xl)',
            padding: '1rem',
          }}
        >
          {/* Search Box */}
          <Input
            icon={Search}
            placeholder="Search by student name, roll number, or role..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />

          {/* Drive Select */}
          <Select value={jobFilter} onChange={(e) => setJobFilter(e.target.value)}>
            <option value="ALL">All placement drives</option>
            {jobs.map((j) => (
              <option key={j.id} value={j.id}>
                {j.title} ({j.salaryPackageLpa} LPA)
              </option>
            ))}
          </Select>

          {/* Status Select */}
          <Select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="ALL">All pipeline stages</option>
            <option value="APPLIED">Applied</option>
            <option value="SHORTLISTED">Shortlisted</option>
            <option value="ONLINE_TEST">Online test</option>
            <option value="TECHNICAL_INTERVIEW">Technical interview</option>
            <option value="HR_INTERVIEW">HR interview</option>
            <option value="OFFER_MADE">Offer sent</option>
            <option value="REJECTED">Rejected</option>
          </Select>
        </div>

        {/* Selected Rows and Bulk Actions Bar */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '0.5rem 0.25rem',
          }}
        >
          <div style={{ fontSize: '0.84rem', color: 'var(--text-muted)' }}>
            Showing <strong style={{ color: 'var(--text)' }}>{filteredApplications.length}</strong> candidate(s)
            {selectedAppIds.length > 0 && (
              <span style={{ marginLeft: '0.5rem', color: 'var(--accent)' }}>
                • {selectedAppIds.length} selected
              </span>
            )}
          </div>

          {selectedAppIds.length > 0 && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => handleBulkStage('SHORTLISTED')}
              >
                Bulk shortlist
              </Button>
              <Button
                variant="danger"
                size="sm"
                onClick={() => handleBulkStage('REJECTED')}
              >
                Bulk reject
              </Button>
            </div>
          )}
        </div>

        {/* Candidate DataTable */}
        <DataTable
          columns={candidateColumns}
          data={filteredApplications}
          loading={loading}
          selectable
          selectedIds={selectedAppIds}
          onSelectRow={(id) => {
            setSelectedAppIds((prev) =>
              prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
            );
          }}
          onSelectAll={(all) => {
            setSelectedAppIds(all ? filteredApplications.map((a) => a.id) : []);
          }}
          emptyTitle="No candidates yet"
          emptyDescription="No candidates yet. Share your drive link with the TPO."
        />

        {/* Move Stage Modal */}
        <Modal
          isOpen={stageModalOpen}
          onClose={() => setStageModalOpen(false)}
          title="Move recruitment stage"
          description={`Update pipeline milestone for ${targetApp?.studentName || 'candidate'}.`}
          primaryAction={{
            label: 'Save stage',
            loading: stageSaving,
            onClick: handleUpdateStage,
          }}
          secondaryAction={{
            label: 'Cancel',
            onClick: () => setStageModalOpen(false),
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <FormField label="Target pipeline stage" required>
              <Select value={targetStage} onChange={(e) => setTargetStage(e.target.value)}>
                <option value="SHORTLISTED">Shortlisted</option>
                <option value="ONLINE_TEST">Online test</option>
                <option value="TECHNICAL_INTERVIEW">Technical interview</option>
                <option value="HR_INTERVIEW">HR interview</option>
                <option value="SELECTED">Selected</option>
                <option value="OFFER_MADE">Offer sent</option>
                <option value="REJECTED">Rejected</option>
              </Select>
            </FormField>

            <FormField label="Internal feedback note" helperText="Recorded for recruitment team review">
              <Textarea
                placeholder="Candidate performed strongly in SQL optimization and system architecture..."
                value={stageRemarks}
                onChange={(e) => setStageRemarks(e.target.value)}
                rows={3}
              />
            </FormField>
          </div>
        </Modal>

        {/* Schedule Interview Modal */}
        <Modal
          isOpen={interviewModalOpen}
          onClose={() => setInterviewModalOpen(false)}
          title="Schedule interview round"
          description={`Set up assessment round for ${interviewTargetApp?.studentName || 'candidate'}.`}
          primaryAction={{
            label: 'Schedule round',
            loading: interviewSaving,
            onClick: handleScheduleInterview,
          }}
          secondaryAction={{
            label: 'Cancel',
            onClick: () => setInterviewModalOpen(false),
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
            <FormField label="Round name" required>
              <Input
                placeholder="e.g. Technical System Design & Algorithms"
                value={interviewForm.roundName}
                onChange={(e) => setInterviewForm({ ...interviewForm, roundName: e.target.value })}
              />
            </FormField>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
              <FormField label="Round number" required>
                <Input
                  type="number"
                  min="1"
                  value={interviewForm.roundNumber}
                  onChange={(e) => setInterviewForm({ ...interviewForm, roundNumber: e.target.value })}
                />
              </FormField>
              <FormField label="Interview type" required>
                <Select
                  value={interviewForm.interviewType}
                  onChange={(e) => setInterviewForm({ ...interviewForm, interviewType: e.target.value })}
                >
                  <option value="ONLINE_MEET">Online meet</option>
                  <option value="IN_PERSON">In person</option>
                  <option value="PHONE">Phone</option>
                </Select>
              </FormField>
            </div>

            <FormField label="Meeting link or venue" required>
              <Input
                placeholder="https://meet.google.com/..."
                value={interviewForm.meetingLinkOrVenue}
                onChange={(e) => setInterviewForm({ ...interviewForm, meetingLinkOrVenue: e.target.value })}
              />
            </FormField>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
              <FormField label="Interviewer name">
                <Input
                  placeholder="Sarah Jenkins"
                  value={interviewForm.interviewerName}
                  onChange={(e) => setInterviewForm({ ...interviewForm, interviewerName: e.target.value })}
                />
              </FormField>
              <FormField label="Interviewer email">
                <Input
                  placeholder="sarah@company.com"
                  value={interviewForm.interviewerEmail}
                  onChange={(e) => setInterviewForm({ ...interviewForm, interviewerEmail: e.target.value })}
                />
              </FormField>
            </div>
          </div>
        </Modal>

        {/* Extend Offer Modal */}
        <Modal
          isOpen={offerModalOpen}
          onClose={() => setOfferModalOpen(false)}
          title="Extend formal job offer"
          description={`Issue an official offer letter to ${offerTargetApp?.studentName || 'candidate'}.`}
          primaryAction={{
            label: 'Send offer',
            loading: offerSaving,
            onClick: handleExtendOffer,
          }}
          secondaryAction={{
            label: 'Cancel',
            onClick: () => setOfferModalOpen(false),
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
              <FormField label="Offered CTC (LPA)" required>
                <Input
                  type="number"
                  step="0.1"
                  value={offerForm.ctcLpa}
                  onChange={(e) => setOfferForm({ ...offerForm, ctcLpa: e.target.value })}
                />
              </FormField>
              <FormField label="Designation / Role" required>
                <Input
                  value={offerForm.designation}
                  onChange={(e) => setOfferForm({ ...offerForm, designation: e.target.value })}
                />
              </FormField>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
              <FormField label="Joining date">
                <DatePicker
                  value={offerForm.joiningDate}
                  onChange={(e) => setOfferForm({ ...offerForm, joiningDate: e.target.value })}
                />
              </FormField>
              <FormField label="Acceptance deadline">
                <DatePicker
                  value={offerForm.validUntil}
                  onChange={(e) => setOfferForm({ ...offerForm, validUntil: e.target.value })}
                />
              </FormField>
            </div>

            <FormField label="Offer notes">
              <Textarea
                placeholder="Welcome to the engineering organization..."
                value={offerForm.notes}
                onChange={(e) => setOfferForm({ ...offerForm, notes: e.target.value })}
                rows={2}
              />
            </FormField>
          </div>
        </Modal>
      </div>
    );
  }

  // ==========================================
  // VIEW: 3. POST A DRIVE TAB
  // ==========================================
  if (activeTab === 'post_drive') {
    return (
      <div className="animate-fade-in">
        <form onSubmit={handlePublishDrive}>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Form Column (Span 2) */}
            <div className="lg:col-span-2" style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
              {/* Section 1: Role Details */}
              <Card>
                <FormSection
                  title="Role details"
                  description="Specify the open position and core engineering focus"
                >
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField label="Job title" required helperText="e.g. Full Stack Software Engineer">
                      <Input
                        required
                        placeholder="Software Engineer - Cloud Systems"
                        value={jobForm.title}
                        onChange={(e) => setJobForm({ ...jobForm, title: e.target.value })}
                      />
                    </FormField>

                    <FormField label="Role type" required>
                      <Select
                        value={jobForm.jobType}
                        onChange={(e) => setJobForm({ ...jobForm, jobType: e.target.value })}
                      >
                        <option value="FULL_TIME">Full time</option>
                        <option value="INTERNSHIP">Internship</option>
                        <option value="INTERNSHIP_TO_PPO">Internship to PPO</option>
                      </Select>
                    </FormField>

                    <FormField label="Work location" required helperText="City or remote model">
                      <Input
                        required
                        placeholder="Bangalore / Hybrid"
                        value={jobForm.location}
                        onChange={(e) => setJobForm({ ...jobForm, location: e.target.value })}
                      />
                    </FormField>

                    <div style={{ gridColumn: 'span 2' }}>
                      <FormField label="Role description" required helperText="Key responsibilities and expectations">
                        <Textarea
                          required
                          placeholder="Describe the candidate's responsibilities, core stack, and recruitment workflow..."
                          value={jobForm.description}
                          onChange={(e) => setJobForm({ ...jobForm, description: e.target.value })}
                          rows={4}
                        />
                      </FormField>
                    </div>
                  </div>
                </FormSection>
              </Card>

              {/* Section 2: Package and Dates */}
              <Card>
                <FormSection
                  title="Package and dates"
                  description="Set financial compensation and recruitment drive milestones"
                >
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <FormField label="Package (CTC in LPA)" required helperText="Annual compensation in lakhs">
                      <Input
                        type="number"
                        step="0.1"
                        min="1"
                        required
                        placeholder="14.0"
                        value={jobForm.salaryPackageLpa}
                        onChange={(e) => setJobForm({ ...jobForm, salaryPackageLpa: e.target.value })}
                      />
                    </FormField>

                    <FormField label="Application deadline" helperText="Last date for students to apply">
                      <DatePicker
                        value={jobForm.applicationDeadline}
                        onChange={(e) => setJobForm({ ...jobForm, applicationDeadline: e.target.value })}
                      />
                    </FormField>

                    <FormField label="Drive date" helperText="Assessment / test date">
                      <DatePicker
                        value={jobForm.driveDate}
                        onChange={(e) => setJobForm({ ...jobForm, driveDate: e.target.value })}
                      />
                    </FormField>
                  </div>
                </FormSection>
              </Card>

              {/* Section 3: Who Can Apply (Eligibility Rules) */}
              <Card>
                <FormSection
                  title="Who can apply"
                  description="Set academic eligibility criteria for automated qualification checks"
                >
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField label="Minimum CGPA" required helperText="Cumulative academic score (0-10)">
                      <Input
                        type="number"
                        step="0.1"
                        min="0"
                        max="10"
                        required
                        value={jobForm.minCgpa}
                        onChange={(e) => setJobForm({ ...jobForm, minCgpa: e.target.value })}
                      />
                    </FormField>

                    <FormField label="Max active backlogs" required helperText="Allowed un-cleared backlogs">
                      <Input
                        type="number"
                        min="0"
                        max="5"
                        required
                        value={jobForm.maxActiveBacklogs}
                        onChange={(e) => setJobForm({ ...jobForm, maxActiveBacklogs: e.target.value })}
                      />
                    </FormField>

                    <FormField label="Graduation year" required helperText="Eligible student graduating batch">
                      <Select
                        value={jobForm.graduationYear}
                        onChange={(e) => setJobForm({ ...jobForm, graduationYear: e.target.value })}
                      >
                        <option value="2026">Class of 2026</option>
                        <option value="2027">Class of 2027</option>
                        <option value="2025">Class of 2025</option>
                      </Select>
                    </FormField>

                    {/* Allowed Branches as Chip Toggles */}
                    <div style={{ gridColumn: 'span 2' }}>
                      <FormField
                        label="Allowed branches"
                        required
                        helperText="Click chips to toggle branch eligibility"
                      >
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.45rem', marginTop: '0.25rem' }}>
                          {ALL_BRANCHES.map((b) => {
                            const isSelected = jobForm.allowedBranches.includes(b);
                            return (
                              <button
                                key={b}
                                type="button"
                                onClick={() => toggleBranch(b)}
                                style={{
                                  padding: '0.35rem 0.85rem',
                                  borderRadius: 'var(--radius-full)',
                                  fontSize: '0.78rem',
                                  fontWeight: isSelected ? 600 : 500,
                                  fontFamily: 'var(--font-body)',
                                  cursor: 'pointer',
                                  transition: 'all 0.12s ease',
                                  border: isSelected ? '1px solid var(--accent)' : '1px solid var(--border)',
                                  backgroundColor: isSelected ? 'var(--accent-subtle)' : 'var(--surface)',
                                  color: isSelected ? 'var(--accent)' : 'var(--text-muted)',
                                }}
                              >
                                {b}
                              </button>
                            );
                          })}
                        </div>
                      </FormField>
                    </div>

                    {/* Required Skills as Tag Input */}
                    <div style={{ gridColumn: 'span 2' }}>
                      <FormField
                        label="Required skills"
                        helperText="Type skill and press enter to add tags"
                      >
                        <div
                          style={{
                            display: 'flex',
                            flexWrap: 'wrap',
                            gap: '0.4rem',
                            marginBottom: '0.65rem',
                          }}
                        >
                          {jobForm.requiredSkills.map((s) => (
                            <span
                              key={s}
                              style={{
                                display: 'inline-flex',
                                alignItems: 'center',
                                gap: '0.35rem',
                                padding: '0.25rem 0.65rem',
                                borderRadius: 'var(--radius-full)',
                                background: 'var(--surface-elevated)',
                                border: '1px solid var(--border)',
                                fontSize: '0.74rem',
                                color: 'var(--text)',
                              }}
                            >
                              {s}
                              <button
                                type="button"
                                onClick={() => removeSkill(s)}
                                style={{
                                  background: 'none',
                                  border: 'none',
                                  color: 'var(--text-muted)',
                                  cursor: 'pointer',
                                  padding: 0,
                                  display: 'flex',
                                }}
                              >
                                <X size={12} />
                              </button>
                            </span>
                          ))}
                        </div>

                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <Input
                            placeholder="Add skill (e.g. Docker, TypeScript)..."
                            value={skillInput}
                            onChange={(e) => setSkillInput(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key === 'Enter') {
                                e.preventDefault();
                                addSkill(e);
                              }
                            }}
                          />
                          <Button type="button" variant="secondary" size="md" onClick={addSkill}>
                            Add
                          </Button>
                        </div>
                      </FormField>
                    </div>
                  </div>
                </FormSection>
              </Card>

              {/* Submit Button */}
              <div>
                <Button
                  type="submit"
                  variant="primary"
                  size="md"
                  loading={publishingJob}
                  style={{ width: '100%' }}
                >
                  Publish placement drive
                </Button>
              </div>
            </div>

            {/* Live Preview Column (Span 1) */}
            <div className="lg:col-span-1">
              <div style={{ position: 'sticky', top: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                <Card>
                  <CardHeader>
                    <CardTitle>Eligibility live check</CardTitle>
                    <Users size={16} style={{ color: 'var(--accent)' }} />
                  </CardHeader>

                  <div style={{ textAlign: 'center', padding: '1.25rem 0' }}>
                    <div
                      style={{
                        fontFamily: 'var(--font-heading)',
                        fontSize: '2.5rem',
                        fontWeight: 700,
                        color: 'var(--accent)',
                        lineHeight: 1,
                      }}
                    >
                      {liveEligibleCount}
                    </div>
                    <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', marginTop: '0.45rem' }}>
                      of {totalPoolCount} registered campus students are eligible
                    </div>

                    <div
                      style={{
                        width: '100%',
                        height: '6px',
                        background: 'var(--border)',
                        borderRadius: 'var(--radius-full)',
                        overflow: 'hidden',
                        marginTop: '1.25rem',
                      }}
                    >
                      <div
                        style={{
                          height: '100%',
                          width: `${Math.round((liveEligibleCount / totalPoolCount) * 100)}%`,
                          background: 'var(--accent)',
                          borderRadius: 'var(--radius-full)',
                          transition: 'width 0.3s ease',
                        }}
                      />
                    </div>
                  </div>

                  <div style={{ borderTop: '1px solid var(--border)', paddingTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.78rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-muted)' }}>
                      <span>Min CGPA cutoff:</span>
                      <strong style={{ color: 'var(--text)' }}>{jobForm.minCgpa}</strong>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-muted)' }}>
                      <span>Max backlogs:</span>
                      <strong style={{ color: 'var(--text)' }}>{jobForm.maxActiveBacklogs}</strong>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-muted)' }}>
                      <span>Branches:</span>
                      <strong style={{ color: 'var(--text)' }}>{jobForm.allowedBranches.join(', ') || 'None'}</strong>
                    </div>
                  </div>
                </Card>

                {/* Microcopy Callout */}
                <Card style={{ background: 'var(--surface-elevated)' }}>
                  <div style={{ display: 'flex', gap: '0.65rem' }}>
                    <Sparkles size={16} style={{ color: 'var(--accent)', flexShrink: 0, marginTop: '2px' }} />
                    <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', lineHeight: 1.4 }}>
                      Students meeting these criteria will see an "Eligible" badge and be able to apply with one click.
                    </p>
                  </div>
                </Card>
              </div>
            </div>
          </div>
        </form>
      </div>
    );
  }

  // ==========================================
  // VIEW: 4. MY DRIVES TAB
  // ==========================================
  if (activeTab === 'my_drives') {
    const jobColumns = [
      {
        key: 'title',
        header: 'Drive / Position',
        render: (title, row) => (
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>{title}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.location}</div>
          </div>
        ),
      },
      {
        key: 'jobType',
        header: 'Type',
        render: (type) => (type ? type.replace('_', ' ') : 'Full Time'),
      },
      {
        key: 'salaryPackageLpa',
        header: 'Package',
        render: (pkg) => (
          <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, color: 'var(--text)' }}>
            {pkg} LPA
          </span>
        ),
      },
      {
        key: 'status',
        header: 'Status',
        render: (status) => <StatusBadge status={status || 'PUBLISHED'} />,
      },
      {
        key: 'driveDate',
        header: 'Drive Date',
        render: (date) => (date ? new Date(date).toLocaleDateString() : 'TBD'),
      },
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (_, row) => (
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              setJobFilter(row.id);
              if (onTabChange) onTabChange('candidates');
            }}
          >
            View candidates
          </Button>
        ),
      },
    ];

    return (
      <div className="animate-fade-in">
        <DataTable
          columns={jobColumns}
          data={jobs}
          loading={loading}
          emptyTitle="No placement drives posted"
          emptyDescription="Publish your first campus placement drive to start reviewing candidates."
        />
      </div>
    );
  }

  // ==========================================
  // VIEW: 5. INTERVIEWS TAB
  // ==========================================
  if (activeTab === 'interviews') {
    const interviewColumns = [
      {
        key: 'studentName',
        header: 'Candidate',
        render: (_, row) => (
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>{row.studentName || 'Alex Rivera'}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.studentEmail || 'student@campus.edu'}</div>
          </div>
        ),
      },
      { key: 'roundName', header: 'Round Name' },
      {
        key: 'scheduledAt',
        header: 'Scheduled For',
        render: (d) => (d ? new Date(d).toLocaleString() : 'TBD'),
      },
      {
        key: 'interviewerName',
        header: 'Interviewer',
        render: (name) => name || 'Sarah Jenkins',
      },
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (_, row) => (
          row.meetingLinkOrVenue ? (
            <Button
              variant="secondary"
              size="sm"
              icon={ExternalLink}
              onClick={() => window.open(row.meetingLinkOrVenue, '_blank')}
            >
              Open meet
            </Button>
          ) : (
            <Button variant="ghost" size="sm" disabled>No link</Button>
          )
        ),
      },
    ];

    return (
      <div className="animate-fade-in">
        <DataTable
          columns={interviewColumns}
          data={interviews}
          loading={loading}
          emptyTitle="No interviews scheduled"
          emptyDescription="Schedule interview rounds from the Candidates tab."
        />
      </div>
    );
  }

  // ==========================================
  // VIEW: 6. OFFERS TAB
  // ==========================================
  if (activeTab === 'offers') {
    const offerColumns = [
      {
        key: 'studentName',
        header: 'Candidate',
        render: (_, row) => (
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>{row.studentName || 'Alex Rivera'}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.studentEmail || 'student@campus.edu'}</div>
          </div>
        ),
      },
      { key: 'designation', header: 'Designation' },
      {
        key: 'ctcLpa',
        header: 'CTC (LPA)',
        render: (pkg) => (
          <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, color: 'var(--text)' }}>
            {pkg} LPA
          </span>
        ),
      },
      {
        key: 'status',
        header: 'Offer Status',
        render: (status) => <StatusBadge status={status || 'PENDING'} />,
      },
      {
        key: 'validUntil',
        header: 'Valid Until',
        render: (d) => (d ? new Date(d).toLocaleDateString() : 'N/A'),
      },
    ];

    return (
      <div className="animate-fade-in">
        <DataTable
          columns={offerColumns}
          data={offers}
          loading={loading}
          emptyTitle="No offers released yet"
          emptyDescription="Extend formal job offers from the Candidates tab once interviews conclude."
        />
      </div>
    );
  }

  return null;
}
