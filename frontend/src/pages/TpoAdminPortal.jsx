import React, { useState, useEffect } from 'react';
import {
  analyticsApi,
  companyApi,
  jobApi,
  studentApi,
} from '../services/api';
import StatCard from '../components/StatCard';
import {
  Shield,
  Download,
  Building2,
  Users,
  Briefcase,
  Award,
  TrendingUp,
  CheckCircle,
  XCircle,
  AlertCircle,
  Search,
  Filter,
  DollarSign,
  FileSpreadsheet,
  Activity,
  Layers,
  Sparkles,
  Calendar,
} from 'lucide-react';

export default function TpoAdminPortal() {
  const [tab, setTab] = useState('dashboard'); // dashboard, companies, jobs, auditLogs
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [companies, setCompanies] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [exportingCsv, setExportingCsv] = useState(false);

  // Filters & Search
  const [companySearch, setCompanySearch] = useState('');
  const [jobSearch, setJobSearch] = useState('');
  const [auditSearch, setAuditSearch] = useState('');

  // Notification Toast
  const [toast, setToast] = useState({ text: '', type: '' });

  const showToast = (text, type = 'success') => {
    setToast({ text, type });
    setTimeout(() => setToast({ text: '', type: '' }), 5000);
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const [dash, compRes, jobsRes, auditRes] = await Promise.all([
        analyticsApi.getTpoDashboard().catch(() => null),
        companyApi.getAll('size=100').catch(() => ({ content: [] })),
        jobApi.getAll('size=100').catch(() => ({ content: [] })),
        analyticsApi.getAuditLogs('size=100').catch(() => ({ content: [] })),
      ]);

      setDashboard(dash);
      setCompanies(compRes.content || []);
      setJobs(jobsRes.content || []);
      setAuditLogs(auditRes.content || []);
    } catch (err) {
      console.error('Failed to load TPO data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Handle Export CSV
  const handleExportCsv = async () => {
    setExportingCsv(true);
    try {
      const blob = await analyticsApi.exportPlacementsCsv();
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `campus_placements_master_report_${new Date().toISOString().slice(0, 10)}.csv`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
      showToast('Placement master data exported successfully as CSV!');
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setExportingCsv(false);
    }
  };

  // Handle Verify Company
  const handleVerifyCompany = async (companyId, companyName) => {
    try {
      await companyApi.verify(companyId);
      showToast(`${companyName} verified successfully!`);
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // Filtered lists
  const filteredCompanies = companies.filter(
    (c) =>
      c.name?.toLowerCase().includes(companySearch.toLowerCase()) ||
      c.industry?.toLowerCase().includes(companySearch.toLowerCase())
  );

  const filteredJobs = jobs.filter(
    (j) =>
      j.title?.toLowerCase().includes(jobSearch.toLowerCase()) ||
      j.companyName?.toLowerCase().includes(jobSearch.toLowerCase())
  );

  const filteredAuditLogs = auditLogs.filter(
    (l) =>
      l.action?.toLowerCase().includes(auditSearch.toLowerCase()) ||
      l.performedByEmail?.toLowerCase().includes(auditSearch.toLowerCase()) ||
      l.entityName?.toLowerCase().includes(auditSearch.toLowerCase())
  );

  return (
    <div className="app-container" style={{ padding: '2rem 1.5rem 4rem' }}>
      {/* Toast Notification */}
      {toast.text && (
        <div
          style={{
            position: 'fixed',
            bottom: '2rem',
            right: '2rem',
            zIndex: 100,
            background: toast.type === 'error' ? 'rgba(239, 68, 68, 0.95)' : 'rgba(16, 185, 129, 0.95)',
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
          {toast.type === 'error' ? <AlertCircle size={20} /> : <CheckCircle size={20} />}
          {toast.text}
        </div>
      )}

      {/* Hero Executive Header */}
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
          background: 'linear-gradient(135deg, rgba(245, 158, 11, 0.12) 0%, rgba(99, 102, 241, 0.1) 100%)',
          border: '1px solid rgba(245, 158, 11, 0.3)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <span className="badge badge-warning">
              <Shield size={12} /> University Placement Cell (TPO)
            </span>
            <span className="badge badge-primary">
              Executive Institutional View
            </span>
          </div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, margin: '0 0 0.5rem', color: '#fff' }}>
            Placement Officer Command Center
          </h1>
          <p style={{ color: '#94a3b8', margin: 0, fontSize: '0.95rem' }}>
            Real-time campus recruitment intelligence, corporate verification, branch-wise placement tracking, and statutory audit logging.
          </p>
        </div>

        <button
          id="export-placements-csv-btn"
          className="btn btn-primary"
          disabled={exportingCsv}
          onClick={handleExportCsv}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            padding: '0.75rem 1.5rem',
            background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
          }}
        >
          <Download size={18} />
          {exportingCsv ? 'Exporting CSV...' : 'Download Placement Master Report (CSV)'}
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
          { id: 'dashboard', label: 'Institutional Analytics', icon: TrendingUp },
          { id: 'companies', label: `Corporate Partners (${companies.length})`, icon: Building2 },
          { id: 'jobs', label: `All Job Drives (${jobs.length})`, icon: Briefcase },
          { id: 'auditLogs', label: `Security & Audit Trail (${auditLogs.length})`, icon: Activity },
        ].map((t) => {
          const Icon = t.icon;
          const isActive = tab === t.id;
          return (
            <button
              key={t.id}
              id={`tpo-tab-${t.id}`}
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
                background: isActive ? 'rgba(245, 158, 11, 0.2)' : 'transparent',
                color: isActive ? '#fbbf24' : '#94a3b8',
                borderBottom: isActive ? '2px solid #fbbf24' : '2px solid transparent',
              }}
            >
              <Icon size={16} />
              {t.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: EXECUTIVE ANALYTICS */}
      {tab === 'dashboard' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          {/* Top Row KPIs */}
          <div className="grid-responsive" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))' }}>
            <StatCard
              title="Placement Rate"
              value={`${dashboard?.overallPlacementPercentage?.toFixed(1) || '0.0'}%`}
              subtitle={`${dashboard?.totalPlacedStudents || 0} of ${dashboard?.totalRegisteredStudents || 0} students placed`}
              icon={TrendingUp}
              color="emerald"
            />
            <StatCard
              title="Highest CTC Package"
              value={`${dashboard?.highestPackageLpa || 0} LPA`}
              subtitle="Super dream campus placement"
              icon={Sparkles}
              color="amber"
            />
            <StatCard
              title="Average CTC Package"
              value={`${dashboard?.averagePackageLpa?.toFixed(2) || '0.00'} LPA`}
              subtitle={`Median CTC: ${dashboard?.medianPackageLpa || 0} LPA`}
              icon={DollarSign}
              color="indigo"
            />
            <StatCard
              title="Verified Recruiters"
              value={`${dashboard?.totalVerifiedCompanies || 0}`}
              subtitle={`${dashboard?.totalRegisteredCompanies || 0} total partners registered`}
              icon={Building2}
              color="cyan"
            />
            <StatCard
              title="Offers Extended"
              value={`${dashboard?.totalOffersExtended || 0}`}
              subtitle={`${dashboard?.totalOffersAccepted || 0} offers accepted`}
              icon={Award}
              color="indigo"
            />
          </div>

          {/* Middle Row: Salary Distribution & Quick Actions */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
            {/* Salary Tier Breakdown */}
            <div className="glass-card" style={{ padding: '1.75rem' }}>
              <h3 style={{ margin: '0 0 1rem', fontSize: '1.15rem', color: '#fff', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Layers size={18} color="#818cf8" /> Salary Tier Distribution
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span style={{ color: '#f59e0b', fontWeight: 600 }}>Super Dream (&gt; 20 LPA)</span>
                    <strong style={{ color: '#fff' }}>{dashboard?.salaryDistribution?.tier4Above20Lpa || 0} offers</strong>
                  </div>
                  <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.05)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${Math.min(100, (dashboard?.salaryDistribution?.tier4Above20Lpa || 0) * 10)}%`,
                        background: 'linear-gradient(90deg, #f59e0b, #fbbf24)',
                      }}
                    />
                  </div>
                </div>

                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span style={{ color: '#10b981', fontWeight: 600 }}>Dream (12 - 20 LPA)</span>
                    <strong style={{ color: '#fff' }}>{dashboard?.salaryDistribution?.tier3Between12And20Lpa || 0} offers</strong>
                  </div>
                  <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.05)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${Math.min(100, (dashboard?.salaryDistribution?.tier3Between12And20Lpa || 0) * 10)}%`,
                        background: 'linear-gradient(90deg, #10b981, #34d399)',
                      }}
                    />
                  </div>
                </div>

                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span style={{ color: '#38bdf8', fontWeight: 600 }}>Regular Plus (6 - 12 LPA)</span>
                    <strong style={{ color: '#fff' }}>{dashboard?.salaryDistribution?.tier2Between6And12Lpa || 0} offers</strong>
                  </div>
                  <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.05)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${Math.min(100, (dashboard?.salaryDistribution?.tier2Between6And12Lpa || 0) * 10)}%`,
                        background: 'linear-gradient(90deg, #38bdf8, #818cf8)',
                      }}
                    />
                  </div>
                </div>

                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span style={{ color: '#94a3b8', fontWeight: 600 }}>Standard Base (&lt; 6 LPA)</span>
                    <strong style={{ color: '#fff' }}>{dashboard?.salaryDistribution?.tier1Below6Lpa || 0} offers</strong>
                  </div>
                  <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.05)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${Math.min(100, (dashboard?.salaryDistribution?.tier1Below6Lpa || 0) * 10)}%`,
                        background: '#64748b',
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Export & Compliance Note */}
            <div
              className="glass-card"
              style={{
                padding: '1.75rem',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
              }}
            >
              <div>
                <h3 style={{ margin: '0 0 0.5rem', fontSize: '1.15rem', color: '#fff', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <FileSpreadsheet size={18} color="#10b981" /> Institutional Compliance Reporting
                </h3>
                <p style={{ color: '#94a3b8', fontSize: '0.85rem', lineHeight: 1.5, margin: '0 0 1rem' }}>
                  Generate NAAC / NIRF compliant audit exports. The master CSV includes student registration rolls, company recruitment details, offer packages, verification timestamps, and placement records.
                </p>
                <div
                  style={{
                    background: 'rgba(255, 255, 255, 0.03)',
                    padding: '0.75rem 1rem',
                    borderRadius: '8px',
                    fontSize: '0.8rem',
                    color: '#cbd5e1',
                    marginBottom: '1rem',
                  }}
                >
                  Statutory Format: <code>UTF-8 CSV (RFC 4180)</code> | Real-time SQL Aggregate
                </div>
              </div>

              <button
                className="btn btn-primary"
                disabled={exportingCsv}
                onClick={handleExportCsv}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '0.5rem',
                  width: '100%',
                  background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                }}
              >
                <Download size={16} /> Export Placements Master CSV
              </button>
            </div>
          </div>

          {/* Department Placement Statistics Table */}
          <div className="glass-card" style={{ padding: '1.75rem' }}>
            <h3 style={{ margin: '0 0 1.25rem', fontSize: '1.15rem', color: '#fff' }}>
              Department-Wise Placement Progress
            </h3>

            {!dashboard?.departmentStats || dashboard.departmentStats.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                No departmental statistics available yet.
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Branch / Department</th>
                      <th>Registered Students</th>
                      <th>Placed Students</th>
                      <th>Placement Rate</th>
                      <th>Average Package</th>
                      <th style={{ width: '25%' }}>Progress</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dashboard.departmentStats.map((dept) => {
                      const pct = dept.placementPercentage ?? 0;
                      return (
                        <tr key={dept.branch}>
                          <td>
                            <strong style={{ color: '#fff' }}>{dept.branch}</strong>
                          </td>
                          <td>{dept.totalStudents}</td>
                          <td style={{ color: '#10b981', fontWeight: 600 }}>{dept.placedStudents}</td>
                          <td>
                            <span className="badge badge-success">{pct.toFixed(1)}%</span>
                          </td>
                          <td style={{ color: '#818cf8', fontWeight: 600 }}>
                            {dept.averageCtcLpa ? `${dept.averageCtcLpa.toFixed(2)} LPA` : 'N/A'}
                          </td>
                          <td>
                            <div style={{ height: '8px', background: 'rgba(255, 255, 255, 0.08)', borderRadius: '4px', overflow: 'hidden' }}>
                              <div
                                style={{
                                  height: '100%',
                                  width: `${pct}%`,
                                  background: pct > 80 ? '#10b981' : pct > 50 ? '#38bdf8' : '#f59e0b',
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
          </div>
        </div>
      )}

      {/* TAB 2: CORPORATE PARTNERS */}
      {tab === 'companies' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <h2 style={{ margin: 0, fontSize: '1.4rem', color: '#fff' }}>
              Corporate Partners & Verification
            </h2>

            <div style={{ position: 'relative', minWidth: '250px' }}>
              <input
                type="text"
                placeholder="Search company or industry..."
                className="input-field"
                value={companySearch}
                onChange={(e) => setCompanySearch(e.target.value)}
              />
            </div>
          </div>

          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {filteredCompanies.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Building2 size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No corporate partners found matching search criteria.</p>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Company Name</th>
                      <th>Industry Domain</th>
                      <th>HQ Location</th>
                      <th>Website</th>
                      <th>Status</th>
                      <th style={{ textAlign: 'right' }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredCompanies.map((c) => (
                      <tr key={c.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>{c.name}</div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>ID: #{c.id}</div>
                        </td>
                        <td>{c.industry || 'Technology'}</td>
                        <td>{c.location || 'Global'}</td>
                        <td>
                          {c.website ? (
                            <a
                              href={c.website.startsWith('http') ? c.website : `https://${c.website}`}
                              target="_blank"
                              rel="noreferrer"
                              style={{ color: '#38bdf8', textDecoration: 'none', fontSize: '0.85rem' }}
                            >
                              {c.website}
                            </a>
                          ) : (
                            'N/A'
                          )}
                        </td>
                        <td>
                          {c.verified ? (
                            <span className="badge badge-success">
                              <CheckCircle size={12} /> VERIFIED
                            </span>
                          ) : (
                            <span className="badge badge-warning">
                              <AlertCircle size={12} /> PENDING
                            </span>
                          )}
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          {!c.verified && (
                            <button
                              className="btn btn-primary btn-sm"
                              onClick={() => handleVerifyCompany(c.id, c.name)}
                              style={{ background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' }}
                            >
                              Verify Company
                            </button>
                          )}
                          {c.verified && (
                            <span style={{ fontSize: '0.8rem', color: '#10b981' }}>Approved</span>
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

      {/* TAB 3: ALL JOBS */}
      {tab === 'jobs' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <h2 style={{ margin: 0, fontSize: '1.4rem', color: '#fff' }}>
              Campus Placement Drives Across All Companies
            </h2>

            <div style={{ position: 'relative', minWidth: '250px' }}>
              <input
                type="text"
                placeholder="Search job title or company..."
                className="input-field"
                value={jobSearch}
                onChange={(e) => setJobSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {filteredJobs.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Briefcase size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No job drives found matching search criteria.</p>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Drive Role</th>
                      <th>Company</th>
                      <th>Type</th>
                      <th>Package (CTC)</th>
                      <th>Deadline</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredJobs.map((j) => (
                      <tr key={j.id}>
                        <td>
                          <div style={{ fontWeight: 600, color: '#f8fafc' }}>{j.title}</div>
                          <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                            Location: {j.location || 'Remote'}
                          </div>
                        </td>
                        <td>
                          <strong style={{ color: '#818cf8' }}>{j.companyName || 'Corporate Partner'}</strong>
                        </td>
                        <td>{j.jobType?.replace('_', ' ')}</td>
                        <td>
                          <strong style={{ color: '#10b981' }}>{j.salaryPackageLpa} LPA</strong>
                        </td>
                        <td style={{ fontSize: '0.85rem', color: '#f59e0b' }}>
                          {j.applicationDeadline ? new Date(j.applicationDeadline).toLocaleDateString() : 'N/A'}
                        </td>
                        <td>
                          <span className="badge badge-success">{j.status || 'PUBLISHED'}</span>
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

      {/* TAB 4: AUDIT LOGS */}
      {tab === 'auditLogs' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h2 style={{ margin: '0 0 0.25rem', fontSize: '1.4rem', color: '#fff' }}>
                Security & Regulatory Audit Trail
              </h2>
              <p style={{ margin: 0, fontSize: '0.85rem', color: '#94a3b8' }}>
                Immutable, timestamped record of every administrative action, stage transition, and credential update
              </p>
            </div>

            <div style={{ position: 'relative', minWidth: '250px' }}>
              <input
                type="text"
                placeholder="Search action or email..."
                className="input-field"
                value={auditSearch}
                onChange={(e) => setAuditSearch(e.target.value)}
              />
            </div>
          </div>

          <div className="glass-card" style={{ padding: '1.5rem' }}>
            {filteredAuditLogs.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
                <Activity size={40} style={{ margin: '0 auto 1rem', opacity: 0.4 }} />
                <p>No audit trail events recorded yet.</p>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Timestamp</th>
                      <th>Action</th>
                      <th>Performed By</th>
                      <th>Entity / Target</th>
                      <th>IP Address</th>
                      <th>Details</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredAuditLogs.map((log) => (
                      <tr key={log.id}>
                        <td style={{ fontSize: '0.8rem', color: '#94a3b8', whiteSpace: 'nowrap' }}>
                          {log.timestamp ? new Date(log.timestamp).toLocaleString() : 'Recent'}
                        </td>
                        <td>
                          <span className="badge badge-primary" style={{ fontSize: '0.7rem' }}>
                            {log.action}
                          </span>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f8fafc' }}>
                            {log.performedByEmail || 'System'}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.85rem' }}>{log.entityName}</div>
                          {log.entityId && (
                            <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>ID: #{log.entityId}</div>
                          )}
                        </td>
                        <td style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                          <code>{log.ipAddress || '127.0.0.1'}</code>
                        </td>
                        <td style={{ fontSize: '0.8rem', color: '#cbd5e1', maxWidth: '300px', wordBreak: 'break-word' }}>
                          {log.details || 'N/A'}
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
    </div>
  );
}
