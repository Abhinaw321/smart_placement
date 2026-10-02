import React from 'react';
import { CheckCircle2, XCircle, AlertTriangle, ShieldCheck, X } from 'lucide-react';

export default function EligibilityModal({ job, report, isOpen, onClose, onApply, applying }) {
  if (!isOpen || !job || !report) return null;

  const isEligible = report.eligible;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '640px' }}>
        {/* Modal Header */}
        <div className="modal-header">
          <div>
            <div style={{ fontSize: '0.8rem', color: '#818cf8', fontWeight: 600, textTransform: 'uppercase' }}>
              Automated Eligibility Engine
            </div>
            <h3 style={{ margin: 0, color: '#fff' }}>{job.title}</h3>
            <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
              {job.company?.name || 'Recruitment Drive'}
            </div>
          </div>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: '#94a3b8',
              cursor: 'pointer',
              padding: '0.25rem',
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div className="modal-body">
          {/* Status Banner */}
          <div
            style={{
              padding: '1rem',
              borderRadius: '10px',
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              marginBottom: '1.25rem',
              background: isEligible ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
              border: `1px solid ${isEligible ? 'rgba(16, 185, 129, 0.4)' : 'rgba(239, 68, 68, 0.4)'}`,
            }}
          >
            {isEligible ? (
              <CheckCircle2 size={28} color="#10b981" />
            ) : (
              <XCircle size={28} color="#ef4444" />
            )}
            <div>
              <div style={{ fontWeight: 700, fontSize: '1rem', color: isEligible ? '#34d399' : '#f87171' }}>
                {isEligible ? 'You Meet All Eligibility Criteria' : 'You Are Not Eligible For This Position'}
              </div>
              <div style={{ fontSize: '0.8rem', color: isEligible ? '#a7f3d0' : '#fca5a5' }}>
                {isEligible
                  ? 'Your profile satisfies all academic, backlog, branch, and skill benchmarks set by the recruiter.'
                  : 'Automated evaluation detected one or more criteria violations.'}
              </div>
            </div>
          </div>

          {/* Granular Breakdown */}
          <div style={{ marginBottom: '1.25rem' }}>
            <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#cbd5e1', marginBottom: '0.75rem' }}>
              Detailed Evaluation Criteria Breakdown:
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {report.criteriaEvaluations && report.criteriaEvaluations.length > 0 ? (
                report.criteriaEvaluations.map((c, idx) => (
                  <div
                    key={idx}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '0.65rem 0.85rem',
                      borderRadius: '8px',
                      background: 'rgba(255, 255, 255, 0.03)',
                      border: '1px solid rgba(255, 255, 255, 0.05)',
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                      {c.passed ? (
                        <CheckCircle2 size={16} color="#10b981" />
                      ) : (
                        <XCircle size={16} color="#ef4444" />
                      )}
                      <div>
                        <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f8fafc' }}>
                          {c.criterionName}
                        </div>
                        <div style={{ fontSize: '0.75rem', color: c.passed ? '#94a3b8' : '#fca5a5' }}>
                          {c.reason}
                        </div>
                      </div>
                    </div>

                    <span className={`badge ${c.passed ? 'badge-success' : 'badge-danger'}`} style={{ fontSize: '0.65rem' }}>
                      {c.passed ? 'PASS' : 'FAIL'}
                    </span>
                  </div>
                ))
              ) : (
                <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
                  No specific criteria evaluations recorded.
                </div>
              )}
            </div>
          </div>

          {/* Rejection Reasons Summary (if any) */}
          {!isEligible && report.rejectionReasons && report.rejectionReasons.length > 0 && (
            <div
              style={{
                padding: '0.85rem',
                borderRadius: '8px',
                background: 'rgba(239, 68, 68, 0.08)',
                border: '1px solid rgba(239, 68, 68, 0.2)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#f87171', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.25rem' }}>
                <AlertTriangle size={14} /> Criteria Blocking Application:
              </div>
              <ul style={{ margin: 0, paddingLeft: '1.25rem', color: '#fca5a5', fontSize: '0.8rem' }}>
                {report.rejectionReasons.map((r, i) => (
                  <li key={i}>{r}</li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div className="modal-footer">
          <button onClick={onClose} className="btn btn-secondary">
            Close
          </button>
          {isEligible && (
            <button
              id="confirm-apply-btn"
              onClick={() => onApply(job.id)}
              disabled={applying}
              className="btn btn-primary"
            >
              <ShieldCheck size={16} />
              {applying ? 'Submitting Application...' : 'Confirm & Submit Application'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
