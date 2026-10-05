import React, { useState, useEffect, useMemo } from 'react';
import {
  analyticsApi,
  companyApi,
  jobApi,
  studentApi,
  applicationApi,
} from '../services/api';
import { useToast } from '../components/ui/Toast';
import Button from '../components/ui/Button';
import Card, { CardHeader, CardTitle, CardDescription } from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import StatCard from '../components/ui/StatCard';
import EmptyState from '../components/ui/EmptyState';
import DataTable from '../components/ui/DataTable';
import StatusBadge from '../components/ui/StatusBadge';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import {
  Download,
  Building2,
  Users,
  Briefcase,
  Award,
  TrendingUp,
  CheckCircle,
  ExternalLink,
  Shield,
  FileSpreadsheet,
  Layers,
  Search,
  FileText,
  Check,
  X,
  GraduationCap,
  Clock,
  Sparkles,
  Filter,
  Activity,
  DollarSign,
  UserCheck,
} from 'lucide-react';

export default function TpoAdminPortal({ activeTab = 'overview', onTabChange }) {
  const { toast } = useToast();

  // Core Data States
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [companies, setCompanies] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [students, setStudents] = useState([]);
  const [applications, setApplications] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [exportingCsv, setExportingCsv] = useState(false);

  // Filters & Search
  const [studentSearch, setStudentSearch] = useState('');
  const [branchFilter, setBranchFilter] = useState('ALL');
  const [placementFilter, setPlacementFilter] = useState('ALL');
  const [companySearch, setCompanySearch] = useState('');
  const [companyVerificationFilter, setCompanyVerificationFilter] = useState('ALL');
  const [jobSearch, setJobSearch] = useState('');
  const [appSearch, setAppSearch] = useState('');
  const [appDriveFilter, setAppDriveFilter] = useState('ALL');
  const [auditSearch, setAuditSearch] = useState('');

  // Modals & Selections
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [verifyingCompanyId, setVerifyingCompanyId] = useState(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const [dash, compRes, jobsRes, studentsRes, auditRes] = await Promise.all([
        analyticsApi.getTpoDashboard().catch(() => null),
        companyApi.getAll('size=100').catch(() => ({ content: [] })),
        jobApi.getAll('size=100').catch(() => ({ content: [] })),
        studentApi.searchStudents('size=100').catch(() => ({ content: [] })),
        analyticsApi.getAuditLogs('size=100').catch(() => ({ content: [] })),
      ]);

      setDashboard(dash);
      setCompanies(compRes?.content || compRes || []);
      setJobs(jobsRes?.content || jobsRes || []);
      setStudents(studentsRes?.content || studentsRes || []);
      setAuditLogs(auditRes?.content || auditRes || []);

      // If jobs are available, load applications for the first few drives
      if (jobsRes?.content && jobsRes.content.length > 0) {
        try {
          const appPromises = jobsRes.content.slice(0, 5).map((j) =>
            applicationApi.getByJob(j.id, 'size=50').catch(() => ({ content: [] }))
          );
          const appsResults = await Promise.all(appPromises);
          const aggregatedApps = appsResults.flatMap((res) => res?.content || res || []);
          setApplications(aggregatedApps);
        } catch {
          // Silent fallback
        }
      }
    } catch (err) {
      console.error('Failed to load TPO Admin data:', err);
      toast('Failed to load portal data. Check connection.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Listen for CSV export event dispatched from PageHeader action
  useEffect(() => {
    const handleExportEvent = () => handleExportCsv();
    window.addEventListener('tpo:export-csv', handleExportEvent);
    return () => window.removeEventListener('tpo:export-csv', handleExportEvent);
  }, []);

  // CSV Export Handler
  const handleExportCsv = async () => {
    setExportingCsv(true);
    try {
      const blob = await analyticsApi.exportPlacementsCsv();
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute(
        'download',
        `campus_placements_master_report_${new Date().toISOString().slice(0, 10)}.csv`
      );
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
      toast('Placement master report exported successfully as CSV!', 'success');
    } catch (err) {
      toast(err.message || 'Failed to export CSV report', 'error');
    } finally {
      setExportingCsv(false);
    }
  };

  // Company Verification Handler
  const handleVerifyCompany = async (companyId, companyName) => {
    setVerifyingCompanyId(companyId);
    try {
      await companyApi.verify(companyId);
      toast(`${companyName} verified and approved successfully!`, 'success');
      // Refresh local list state
      setCompanies((prev) =>
        prev.map((c) => (c.id === companyId ? { ...c, verified: true } : c))
      );
    } catch (err) {
      toast(err.message || 'Verification failed', 'error');
    } finally {
      setVerifyingCompanyId(null);
    }
  };

  // ========================================================
  // FILTERED DATASETS
  // ========================================================

  const filteredStudents = useMemo(() => {
    return students.filter((s) => {
      const q = studentSearch.toLowerCase();
      const matchesSearch =
        !q ||
        s.fullName?.toLowerCase().includes(q) ||
        s.email?.toLowerCase().includes(q) ||
        s.rollNumber?.toLowerCase().includes(q) ||
        s.branch?.toLowerCase().includes(q);

      const matchesBranch = branchFilter === 'ALL' || s.branch === branchFilter;
      const matchesPlacement =
        placementFilter === 'ALL' ||
        (placementFilter === 'PLACED' && s.isPlaced) ||
        (placementFilter === 'UNPLACED' && !s.isPlaced);

      return matchesSearch && matchesBranch && matchesPlacement;
    });
  }, [students, studentSearch, branchFilter, placementFilter]);

  const filteredCompanies = useMemo(() => {
    return companies.filter((c) => {
      const q = companySearch.toLowerCase();
      const matchesSearch =
        !q ||
        c.name?.toLowerCase().includes(q) ||
        c.industry?.toLowerCase().includes(q) ||
        c.location?.toLowerCase().includes(q);

      const matchesVerification =
        companyVerificationFilter === 'ALL' ||
        (companyVerificationFilter === 'VERIFIED' && c.verified) ||
        (companyVerificationFilter === 'PENDING' && !c.verified);

      return matchesSearch && matchesVerification;
    });
  }, [companies, companySearch, companyVerificationFilter]);

  const filteredJobs = useMemo(() => {
    return jobs.filter((j) => {
      const q = jobSearch.toLowerCase();
      return (
        !q ||
        j.title?.toLowerCase().includes(q) ||
        j.companyName?.toLowerCase().includes(q) ||
        j.location?.toLowerCase().includes(q)
      );
    });
  }, [jobs, jobSearch]);

  const filteredApplications = useMemo(() => {
    return applications.filter((a) => {
      const q = appSearch.toLowerCase();
      const matchesSearch =
        !q ||
        a.studentName?.toLowerCase().includes(q) ||
        a.jobTitle?.toLowerCase().includes(q) ||
        a.companyName?.toLowerCase().includes(q);

      const matchesDrive =
        appDriveFilter === 'ALL' || String(a.jobId) === String(appDriveFilter);

      return matchesSearch && matchesDrive;
    });
  }, [applications, appSearch, appDriveFilter]);

  const filteredAuditLogs = useMemo(() => {
    return auditLogs.filter((l) => {
      const q = auditSearch.toLowerCase();
      return (
        !q ||
        l.action?.toLowerCase().includes(q) ||
        l.performedByEmail?.toLowerCase().includes(q) ||
        l.entityName?.toLowerCase().includes(q) ||
        l.details?.toLowerCase().includes(q)
      );
    });
  }, [auditLogs, auditSearch]);

  // Unique branches from students list
  const uniqueBranches = useMemo(() => {
    const set = new Set();
    students.forEach((s) => {
      if (s.branch) set.add(s.branch);
    });
    return Array.from(set);
  }, [students]);

  // ========================================================
  // VIEW: 1. OVERVIEW TAB
  // ========================================================
  if (activeTab === 'overview') {
    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
        {/* Metric Cards Row */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
          <StatCard
            label="Placement Rate"
            value={`${dashboard?.overallPlacementPercentage?.toFixed(1) || '0.0'}%`}
            detail={`${dashboard?.totalPlacedStudents || 0} of ${dashboard?.totalRegisteredStudents || 0} placed`}
            icon={TrendingUp}
            variant="accent"
          />
          <StatCard
            label="Highest CTC"
            value={`${dashboard?.highestPackageLpa || 0} LPA`}
            detail="Super dream campus offer"
            icon={Sparkles}
          />
          <StatCard
            label="Average CTC"
            value={`${dashboard?.averagePackageLpa?.toFixed(2) || '0.00'} LPA`}
            detail={`Median: ${dashboard?.medianPackageLpa || 0} LPA`}
            icon={DollarSign}
          />
          <StatCard
            label="Verified Partners"
            value={dashboard?.totalVerifiedCompanies || companies.filter((c) => c.verified).length}
            detail={`${companies.length} total registered`}
            icon={Building2}
          />
          <StatCard
            label="Offers Extended"
            value={dashboard?.totalOffersExtended || 0}
            detail={`${dashboard?.totalOffersAccepted || 0} accepted`}
            icon={Award}
          />
        </div>

        {/* Two Column Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column (2 cols): Department-Wise Progress */}
          <div className="lg:col-span-2" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <Card>
              <CardHeader>
                <div>
                  <CardTitle>Department-Wise Placement Progress</CardTitle>
                  <CardDescription>
                    Real-time placement rates and average compensation across academic branches
                  </CardDescription>
                </div>
              </CardHeader>

              {!dashboard?.departmentStats || dashboard.departmentStats.length === 0 ? (
                <EmptyState
                  icon={GraduationCap}
                  title="No departmental data yet"
                  description="Department statistics will populate as drives and offers progress."
                />
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.15rem' }}>
                  {dashboard.departmentStats.map((dept) => {
                    const pct = Math.min(100, Math.max(0, dept.placementPercentage ?? 0));
                    return (
                      <div key={dept.branch} style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.84rem' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <span style={{ fontWeight: 600, color: 'var(--text)' }}>{dept.branch}</span>
                            <span style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
                              ({dept.placedStudents} of {dept.totalStudents} placed)
                            </span>
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                            {dept.averageCtcLpa && (
                              <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontFamily: 'var(--font-heading)' }}>
                                Avg {dept.averageCtcLpa.toFixed(1)} LPA
                              </span>
                            )}
                            <Badge variant={pct >= 75 ? 'accent' : pct >= 50 ? 'success' : 'warning'}>
                              {pct.toFixed(1)}%
                            </Badge>
                          </div>
                        </div>

                        {/* Progress Meter */}
                        <div
                          style={{
                            height: '6px',
                            background: 'var(--surface-elevated)',
                            borderRadius: '3px',
                            overflow: 'hidden',
                            border: '1px solid var(--border)',
                          }}
                        >
                          <div
                            style={{
                              height: '100%',
                              width: `${pct}%`,
                              background: pct >= 75 ? 'var(--accent)' : 'var(--status-green-fg)',
                              transition: 'width 0.6s ease',
                            }}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </Card>

            {/* Quick Drives Preview */}
            <Card>
              <CardHeader>
                <div>
                  <CardTitle>Active Placement Drives</CardTitle>
                  <CardDescription>Visiting companies currently accepting student applications</CardDescription>
                </div>
                {onTabChange && (
                  <Button variant="ghost" size="sm" onClick={() => onTabChange('drives')}>
                    View all ({jobs.length})
                  </Button>
                )}
              </CardHeader>

              {jobs.length === 0 ? (
                <EmptyState
                  icon={Briefcase}
                  title="No active drives"
                  description="Publish a drive to start receiving candidate applications."
                />
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.65rem' }}>
                  {jobs.slice(0, 3).map((job) => (
                    <div
                      key={job.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '0.75rem 1rem',
                        background: 'var(--surface-elevated)',
                        border: '1px solid var(--border)',
                        borderRadius: 'var(--radius-md)',
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <div
                          style={{
                            width: '32px',
                            height: '32px',
                            borderRadius: 'var(--radius-md)',
                            background: 'var(--surface)',
                            border: '1px solid var(--border)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 700,
                            fontSize: '0.8rem',
                            color: 'var(--text)',
                          }}
                        >
                          {job.companyName ? job.companyName.charAt(0).toUpperCase() : 'C'}
                        </div>
                        <div>
                          <div style={{ fontWeight: 600, color: 'var(--text)', fontSize: '0.88rem' }}>{job.title}</div>
                          <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
                            {job.companyName} • {job.location || 'Remote'}
                          </div>
                        </div>
                      </div>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, color: 'var(--text)' }}>
                          {job.salaryPackageLpa} LPA
                        </span>
                        <StatusBadge status={job.status || 'PUBLISHED'} />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>

          {/* Right Column (1 col): Salary Tiers & CSV Report */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {/* Salary Tier Breakdown */}
            <Card>
              <CardHeader>
                <div>
                  <CardTitle>Salary Distribution</CardTitle>
                  <CardDescription>Extended offers categorized by CTC bands</CardDescription>
                </div>
              </CardHeader>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {[
                  {
                    tier: 'Super Dream',
                    range: '> 20 LPA',
                    count: dashboard?.salaryDistribution?.tier4Above20Lpa || 0,
                    color: 'var(--accent)',
                  },
                  {
                    tier: 'Dream',
                    range: '12 – 20 LPA',
                    count: dashboard?.salaryDistribution?.tier3Between12And20Lpa || 0,
                    color: 'var(--status-green-fg)',
                  },
                  {
                    tier: 'Regular Plus',
                    range: '6 – 12 LPA',
                    count: dashboard?.salaryDistribution?.tier2Between6And12Lpa || 0,
                    color: 'var(--status-blue-fg)',
                  },
                  {
                    tier: 'Standard Base',
                    range: '< 6 LPA',
                    count: dashboard?.salaryDistribution?.tier1Below6Lpa || 0,
                    color: 'var(--text-muted)',
                  },
                ].map((band) => (
                  <div key={band.tier} style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.82rem' }}>
                      <span style={{ fontWeight: 600, color: 'var(--text)' }}>{band.tier}</span>
                      <span style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-heading)' }}>
                        <strong style={{ color: 'var(--text)' }}>{band.count}</strong> offers
                      </span>
                    </div>
                    <div style={{ fontSize: '0.7rem', color: 'var(--text-subtle)', marginTop: '-2px' }}>
                      Band: {band.range}
                    </div>
                    <div
                      style={{
                        height: '5px',
                        background: 'var(--surface-elevated)',
                        borderRadius: '2.5px',
                        overflow: 'hidden',
                      }}
                    >
                      <div
                        style={{
                          height: '100%',
                          width: `${Math.min(100, band.count * 15)}%`,
                          background: band.color,
                        }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            {/* Compliance & Export Box */}
            <Card style={{ background: 'var(--surface-elevated)', borderColor: 'var(--border)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <FileSpreadsheet size={16} color="var(--accent)" />
                <span style={{ fontSize: '0.84rem', fontWeight: 600, color: 'var(--text)' }}>
                  Statutory Reporting
                </span>
              </div>
              <p style={{ fontSize: '0.76rem', color: 'var(--text-muted)', lineHeight: 1.5, margin: '0 0 1rem' }}>
                Export NIRF / NAAC compliant campus master data. Includes complete student registration rolls, corporate records, and offer packages.
              </p>
              <Button
                variant="primary"
                size="sm"
                icon={Download}
                loading={exportingCsv}
                onClick={handleExportCsv}
                style={{ width: '100%' }}
              >
                {exportingCsv ? 'Generating CSV...' : 'Download Master CSV'}
              </Button>
            </Card>
          </div>
        </div>
      </div>
    );
  }

  // ========================================================
  // VIEW: 2. STUDENTS DIRECTORY TAB
  // ========================================================
  if (activeTab === 'students') {
    const studentColumns = [
      {
        key: 'candidate',
        header: 'Student',
        render: (_, row) => (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
            <div
              style={{
                width: '30px',
                height: '30px',
                borderRadius: '50%',
                background: 'var(--surface-elevated)',
                border: '1px solid var(--border)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 600,
                fontSize: '0.75rem',
                color: 'var(--text)',
                flexShrink: 0,
              }}
            >
              {row.firstName ? row.firstName.charAt(0).toUpperCase() : 'S'}
            </div>
            <div>
              <div style={{ fontWeight: 600, color: 'var(--text)' }}>
                {row.fullName || `${row.firstName} ${row.lastName}`}
              </div>
              <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.email}</div>
            </div>
          </div>
        ),
      },
      {
        key: 'rollNumber',
        header: 'Roll No',
        render: (roll) => <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8rem' }}>{roll || 'N/A'}</span>,
      },
      {
        key: 'branch',
        header: 'Branch',
        render: (branch, row) => (
          <div>
            <div style={{ fontWeight: 500, color: 'var(--text)' }}>{branch || 'General'}</div>
            <div style={{ fontSize: '0.72rem', color: 'var(--text-subtle)' }}>Class of {row.graduationYear || '2026'}</div>
          </div>
        ),
      },
      {
        key: 'cgpa',
        header: 'CGPA',
        render: (cgpa) => (
          <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, color: 'var(--text)' }}>
            {cgpa ? cgpa.toFixed(2) : '0.00'}
          </span>
        ),
      },
      {
        key: 'activeBacklogs',
        header: 'Backlogs',
        render: (backlogs) =>
          backlogs > 0 ? (
            <Badge variant="danger">{backlogs} Active</Badge>
          ) : (
            <span style={{ fontSize: '0.8rem', color: 'var(--status-green-fg)' }}>0</span>
          ),
      },
      {
        key: 'skills',
        header: 'Skills',
        render: (skills) => (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.25rem', maxWidth: '220px' }}>
            {skills && skills.length > 0 ? (
              skills.slice(0, 3).map((s) => (
                <Badge key={s.id || s.skillName} variant="neutral">
                  {s.skillName}
                </Badge>
              ))
            ) : (
              <span style={{ fontSize: '0.74rem', color: 'var(--text-subtle)' }}>None listed</span>
            )}
            {skills && skills.length > 3 && (
              <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', alignSelf: 'center' }}>
                +{skills.length - 3}
              </span>
            )}
          </div>
        ),
      },
      {
        key: 'isPlaced',
        header: 'Status',
        render: (isPlaced) => (
          <Badge variant={isPlaced ? 'success' : 'neutral'}>
            {isPlaced ? 'Placed' : 'In Pipeline'}
          </Badge>
        ),
      },
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (_, row) => (
          <Button variant="secondary" size="sm" onClick={() => setSelectedStudent(row)}>
            Profile
          </Button>
        ),
      },
    ];

    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        {/* Filter Bar */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            flexWrap: 'wrap',
          }}
        >
          <div style={{ flex: '1 1 240px', minWidth: '220px' }}>
            <Input
              icon={Search}
              placeholder="Search students by name, email, roll number..."
              value={studentSearch}
              onChange={(e) => setStudentSearch(e.target.value)}
            />
          </div>

          <div style={{ width: '180px' }}>
            <Select
              value={branchFilter}
              onChange={(e) => setBranchFilter(e.target.value)}
              options={[
                { value: 'ALL', label: 'All Branches' },
                ...uniqueBranches.map((b) => ({ value: b, label: b })),
              ]}
            />
          </div>

          <div style={{ width: '160px' }}>
            <Select
              value={placementFilter}
              onChange={(e) => setPlacementFilter(e.target.value)}
              options={[
                { value: 'ALL', label: 'All Statuses' },
                { value: 'PLACED', label: 'Placed Only' },
                { value: 'UNPLACED', label: 'Seeking Only' },
              ]}
            />
          </div>
        </div>

        {/* Data Table */}
        <DataTable
          columns={studentColumns}
          data={filteredStudents}
          loading={loading}
          emptyTitle="No students found"
          emptyDescription="Try adjusting your search criteria or branch filters."
        />

        {/* Student Details Modal */}
        <Modal
          isOpen={!!selectedStudent}
          onClose={() => setSelectedStudent(null)}
          title={selectedStudent ? selectedStudent.fullName || 'Student Details' : 'Student'}
          description="Institutional academic record, placement status, and verified skills"
        >
          {selectedStudent && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(2, 1fr)',
                  gap: '0.85rem',
                  padding: '1rem',
                  background: 'var(--surface-elevated)',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border)',
                  fontSize: '0.84rem',
                }}
              >
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>Roll Number</div>
                  <div style={{ fontWeight: 600, color: 'var(--text)', fontFamily: 'var(--font-mono)' }}>
                    {selectedStudent.rollNumber || 'N/A'}
                  </div>
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>Branch & Batch</div>
                  <div style={{ fontWeight: 600, color: 'var(--text)' }}>
                    {selectedStudent.branch} ({selectedStudent.graduationYear})
                  </div>
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>CGPA</div>
                  <div style={{ fontWeight: 700, color: 'var(--text)', fontFamily: 'var(--font-heading)' }}>
                    {selectedStudent.cgpa?.toFixed(2)} / 10.0
                  </div>
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>Active Backlogs</div>
                  <div style={{ fontWeight: 600, color: selectedStudent.activeBacklogs > 0 ? 'var(--status-red-fg)' : 'var(--status-green-fg)' }}>
                    {selectedStudent.activeBacklogs ?? 0}
                  </div>
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>Email Address</div>
                  <div style={{ color: 'var(--text)' }}>{selectedStudent.email}</div>
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>Contact Phone</div>
                  <div style={{ color: 'var(--text)' }}>{selectedStudent.phone || 'Not provided'}</div>
                </div>
              </div>

              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text)', marginBottom: '0.5rem' }}>
                  Verified Skills
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem' }}>
                  {selectedStudent.skills && selectedStudent.skills.length > 0 ? (
                    selectedStudent.skills.map((s) => (
                      <Badge key={s.id || s.skillName} variant="accent">
                        {s.skillName}
                      </Badge>
                    ))
                  ) : (
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>No skills cataloged</span>
                  )}
                </div>
              </div>

              {selectedStudent.hasResume && (
                <div style={{ borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
                  <Button
                    variant="secondary"
                    size="sm"
                    icon={ExternalLink}
                    onClick={() => {
                      window.open(`/api/v1/students/${selectedStudent.id}/resume`, '_blank');
                    }}
                  >
                    View Student Resume (PDF)
                  </Button>
                </div>
              )}
            </div>
          )}
        </Modal>
      </div>
    );
  }

  // ========================================================
  // VIEW: 3. CORPORATE PARTNERS TAB
  // ========================================================
  if (activeTab === 'companies') {
    const companyColumns = [
      {
        key: 'name',
        header: 'Company',
        render: (name, row) => (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
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
                fontWeight: 700,
                fontSize: '0.82rem',
                color: 'var(--text)',
                flexShrink: 0,
              }}
            >
              {name ? name.charAt(0).toUpperCase() : 'C'}
            </div>
            <div>
              <div style={{ fontWeight: 600, color: 'var(--text)' }}>{name}</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                {row.location || 'Global Headquarters'}
              </div>
            </div>
          </div>
        ),
      },
      {
        key: 'industry',
        header: 'Industry Domain',
        render: (ind) => <span style={{ color: 'var(--text-muted)' }}>{ind || 'Technology'}</span>,
      },
      {
        key: 'website',
        header: 'Website',
        render: (web) =>
          web ? (
            <a
              href={web.startsWith('http') ? web : `https://${web}`}
              target="_blank"
              rel="noreferrer"
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.35rem',
                color: 'var(--text)',
                textDecoration: 'none',
                fontSize: '0.78rem',
              }}
            >
              <span>{web.replace(/^https?:\/\//, '')}</span>
              <ExternalLink size={12} color="var(--text-muted)" />
            </a>
          ) : (
            <span style={{ fontSize: '0.74rem', color: 'var(--text-subtle)' }}>N/A</span>
          ),
      },
      {
        key: 'verified',
        header: 'Status',
        render: (verified) => (
          <StatusBadge status={verified ? 'VERIFIED' : 'PENDING'} />
        ),
      },
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (_, row) =>
          !row.verified ? (
            <Button
              variant="primary"
              size="sm"
              loading={verifyingCompanyId === row.id}
              onClick={() => handleVerifyCompany(row.id, row.name)}
            >
              Verify partner
            </Button>
          ) : (
            <Badge variant="success">Approved</Badge>
          ),
      },
    ];

    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        {/* Filter Bar */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 240px', minWidth: '220px' }}>
            <Input
              icon={Search}
              placeholder="Search companies by name, industry, or location..."
              value={companySearch}
              onChange={(e) => setCompanySearch(e.target.value)}
            />
          </div>

          <div style={{ width: '180px' }}>
            <Select
              value={companyVerificationFilter}
              onChange={(e) => setCompanyVerificationFilter(e.target.value)}
              options={[
                { value: 'ALL', label: 'All Partners' },
                { value: 'VERIFIED', label: 'Verified Only' },
                { value: 'PENDING', label: 'Pending Verification' },
              ]}
            />
          </div>
        </div>

        {/* Data Table */}
        <DataTable
          columns={companyColumns}
          data={filteredCompanies}
          loading={loading}
          emptyTitle="No corporate partners found"
          emptyDescription="Partners will appear here once recruiters register with their corporate domains."
        />
      </div>
    );
  }

  // ========================================================
  // VIEW: 4. DRIVES TAB
  // ========================================================
  if (activeTab === 'drives') {
    const driveColumns = [
      {
        key: 'title',
        header: 'Drive / Position',
        render: (title, row) => (
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>{title}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
              {row.companyName} • {row.location || 'On-campus'}
            </div>
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
        key: 'applicationDeadline',
        header: 'Deadline',
        render: (d) => (d ? new Date(d).toLocaleDateString() : 'Rolling'),
      },
      {
        key: 'status',
        header: 'Status',
        render: (status) => <StatusBadge status={status || 'PUBLISHED'} />,
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
              setAppDriveFilter(row.id);
              if (onTabChange) onTabChange('applications');
            }}
          >
            Applicants
          </Button>
        ),
      },
    ];

    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{ flex: '1 1 240px', minWidth: '220px' }}>
            <Input
              icon={Search}
              placeholder="Search placement drives by role, company, or location..."
              value={jobSearch}
              onChange={(e) => setJobSearch(e.target.value)}
            />
          </div>
        </div>

        <DataTable
          columns={driveColumns}
          data={filteredJobs}
          loading={loading}
          emptyTitle="No placement drives found"
          emptyDescription="Active drives posted by recruiters will appear here."
        />
      </div>
    );
  }

  // ========================================================
  // VIEW: 5. APPLICATIONS TAB
  // ========================================================
  if (activeTab === 'applications') {
    const appColumns = [
      {
        key: 'studentName',
        header: 'Candidate',
        render: (name, row) => (
          <div>
            <div style={{ fontWeight: 600, color: 'var(--text)' }}>{name || 'Alex Rivera'}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>
              {row.studentEmail || 'student@campus.edu'}
            </div>
          </div>
        ),
      },
      {
        key: 'jobTitle',
        header: 'Drive / Company',
        render: (title, row) => (
          <div>
            <div style={{ fontWeight: 500, color: 'var(--text)' }}>{title || 'Software Engineer'}</div>
            <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.companyName || 'Campus Partner'}</div>
          </div>
        ),
      },
      {
        key: 'status',
        header: 'Pipeline Stage',
        render: (status) => <StatusBadge status={status || 'APPLIED'} />,
      },
      {
        key: 'appliedAt',
        header: 'Applied On',
        render: (date) => (date ? new Date(date).toLocaleDateString() : 'Recent'),
      },
      {
        key: 'actions',
        header: 'Actions',
        align: 'right',
        render: (_, row) => (
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              const matchedStudent = students.find((s) => s.userId === row.studentId || s.id === row.studentId);
              if (matchedStudent) {
                setSelectedStudent(matchedStudent);
              } else {
                toast(`Candidate: ${row.studentName || 'Alex Rivera'} (${row.status})`, 'info');
              }
            }}
          >
            Details
          </Button>
        ),
      },
    ];

    const driveOptions = [
      { value: 'ALL', label: 'All Placement Drives' },
      ...jobs.map((j) => ({ value: String(j.id), label: `${j.companyName} — ${j.title}` })),
    ];

    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 240px', minWidth: '220px' }}>
            <Input
              icon={Search}
              placeholder="Search candidate applications..."
              value={appSearch}
              onChange={(e) => setAppSearch(e.target.value)}
            />
          </div>

          <div style={{ width: '280px' }}>
            <Select
              value={appDriveFilter}
              onChange={(e) => setAppDriveFilter(e.target.value)}
              options={driveOptions}
            />
          </div>
        </div>

        <DataTable
          columns={appColumns}
          data={filteredApplications}
          loading={loading}
          emptyTitle="No candidate applications"
          emptyDescription="Select another drive filter or check back as students apply."
        />
      </div>
    );
  }

  // ========================================================
  // VIEW: 6. REPORTS & INSTITUTIONAL COMPLIANCE TAB
  // ========================================================
  if (activeTab === 'reports') {
    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
        {/* Compliance Hero Box */}
        <Card style={{ background: 'var(--surface-elevated)', borderColor: 'var(--border)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
                <Badge variant="accent">Statutory Compliance</Badge>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-subtle)' }}>
                  Format: UTF-8 CSV (RFC 4180)
                </span>
              </div>
              <h2 style={{ fontSize: '1.35rem', fontWeight: 700, margin: '0 0 0.4rem', color: 'var(--text)' }}>
                National Institutional Ranking Framework (NIRF) & NAAC Export
              </h2>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.84rem', margin: 0, maxWidth: '650px' }}>
                Export authoritative placement audit sheets with student registration numbers, hiring corporate entities, annual CTC figures, verification records, and offer confirmation timestamps.
              </p>
            </div>

            <Button
              variant="primary"
              size="md"
              icon={Download}
              loading={exportingCsv}
              onClick={handleExportCsv}
            >
              {exportingCsv ? 'Exporting...' : 'Export Placement Master CSV'}
            </Button>
          </div>
        </Card>

        {/* Detailed Department Breakdown Table */}
        <Card>
          <CardHeader>
            <div>
              <CardTitle>Department Placement Performance Table</CardTitle>
              <CardDescription>Comprehensive audit breakdown by academic department</CardDescription>
            </div>
          </CardHeader>

          {!dashboard?.departmentStats || dashboard.departmentStats.length === 0 ? (
            <EmptyState
              icon={FileText}
              title="No statistical breakdown available"
              description="Data aggregates will populate once offers and student placements are recorded."
            />
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.84rem', textAlign: 'left' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid var(--border)' }}>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Branch / Dept</th>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Registered</th>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Placed</th>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Placement %</th>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Average CTC</th>
                    <th style={{ padding: '0.75rem 1rem', color: 'var(--text-subtle)', fontWeight: 600, width: '25%' }}>Progress</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboard.departmentStats.map((dept) => {
                    const pct = Math.min(100, Math.max(0, dept.placementPercentage ?? 0));
                    return (
                      <tr
                        key={dept.branch}
                        style={{
                          borderBottom: '1px solid var(--border)',
                          transition: 'background 0.12s ease',
                        }}
                      >
                        <td style={{ padding: '0.85rem 1rem', fontWeight: 600, color: 'var(--text)' }}>
                          {dept.branch}
                        </td>
                        <td style={{ padding: '0.85rem 1rem', color: 'var(--text-muted)' }}>
                          {dept.totalStudents}
                        </td>
                        <td style={{ padding: '0.85rem 1rem', color: 'var(--status-green-fg)', fontWeight: 600 }}>
                          {dept.placedStudents}
                        </td>
                        <td style={{ padding: '0.85rem 1rem' }}>
                          <Badge variant={pct >= 75 ? 'accent' : 'success'}>
                            {pct.toFixed(1)}%
                          </Badge>
                        </td>
                        <td style={{ padding: '0.85rem 1rem', color: 'var(--text)', fontFamily: 'var(--font-heading)', fontWeight: 600 }}>
                          {dept.averageCtcLpa ? `${dept.averageCtcLpa.toFixed(2)} LPA` : 'N/A'}
                        </td>
                        <td style={{ padding: '0.85rem 1rem' }}>
                          <div
                            style={{
                              height: '6px',
                              background: 'var(--surface-elevated)',
                              borderRadius: '3px',
                              overflow: 'hidden',
                            }}
                          >
                            <div
                              style={{
                                height: '100%',
                                width: `${pct}%`,
                                background: pct >= 75 ? 'var(--accent)' : 'var(--status-green-fg)',
                              }}
                            />
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    );
  }

  // ========================================================
  // VIEW: 7. AUDIT LOGS TAB
  // ========================================================
  if (activeTab === 'audit_logs') {
    const auditColumns = [
      {
        key: 'timestamp',
        header: 'Timestamp',
        render: (ts) => (
          <span style={{ fontSize: '0.76rem', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
            {ts ? new Date(ts).toLocaleString() : 'Recent'}
          </span>
        ),
      },
      {
        key: 'action',
        header: 'Action',
        render: (action) => (
          <Badge variant="accent" style={{ fontFamily: 'var(--font-mono)', fontSize: '0.72rem' }}>
            {action}
          </Badge>
        ),
      },
      {
        key: 'performedByEmail',
        header: 'User',
        render: (email) => (
          <span style={{ fontWeight: 500, color: 'var(--text)', fontSize: '0.82rem' }}>
            {email || 'System'}
          </span>
        ),
      },
      {
        key: 'entityName',
        header: 'Target Entity',
        render: (entity, row) => (
          <div>
            <span style={{ color: 'var(--text)' }}>{entity || 'N/A'}</span>
            {row.entityId && (
              <span style={{ fontSize: '0.72rem', color: 'var(--text-subtle)', marginLeft: '0.35rem' }}>
                #{row.entityId}
              </span>
            )}
          </div>
        ),
      },
      {
        key: 'ipAddress',
        header: 'IP Address',
        render: (ip) => (
          <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--text-subtle)' }}>
            {ip || '127.0.0.1'}
          </span>
        ),
      },
      {
        key: 'details',
        header: 'Audit Record',
        render: (details) => (
          <span
            style={{
              fontSize: '0.78rem',
              color: 'var(--text-muted)',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
            }}
          >
            {details || 'State transition verified'}
          </span>
        ),
      },
    ];

    return (
      <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{ flex: '1 1 240px', minWidth: '220px' }}>
            <Input
              icon={Search}
              placeholder="Search audit trail by action, performer email, entity..."
              value={auditSearch}
              onChange={(e) => setAuditSearch(e.target.value)}
            />
          </div>
        </div>

        <DataTable
          columns={auditColumns}
          data={filteredAuditLogs}
          loading={loading}
          emptyTitle="No audit records"
          emptyDescription="Audit trails are automatically recorded for all mutations and state transitions."
        />
      </div>
    );
  }

  // Fallback view
  return (
    <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
      Unknown portal section. Select a tab from the sidebar.
    </div>
  );
}
