import React from 'react';

const STATUS_MAP = {
  // Application statuses
  APPLIED: { label: 'Applied', variant: 'grey' },
  SHORTLISTED: { label: 'Shortlisted', variant: 'amber' },
  ONLINE_TEST: { label: 'Online test', variant: 'amber' },
  ASSESSMENT: { label: 'Online test', variant: 'amber' },
  TECHNICAL_INTERVIEW: { label: 'Technical interview', variant: 'amber' },
  HR_INTERVIEW: { label: 'HR interview', variant: 'amber' },
  INTERVIEW_SCHEDULED: { label: 'Interview scheduled', variant: 'amber' },
  SELECTED: { label: 'Selected', variant: 'green' },
  OFFER_MADE: { label: 'Offer sent', variant: 'green' },
  OFFERED: { label: 'Offer sent', variant: 'green' },
  REJECTED: { label: 'Rejected', variant: 'red' },

  // Offer statuses
  PENDING: { label: 'Pending response', variant: 'amber' },
  ACCEPTED: { label: 'Offer accepted', variant: 'green' },
  DECLINED: { label: 'Offer declined', variant: 'red' },

  // Job / Drive statuses
  PUBLISHED: { label: 'Active drive', variant: 'green' },
  ACTIVE: { label: 'Active', variant: 'green' },
  DRAFT: { label: 'Draft', variant: 'grey' },
  CLOSED: { label: 'Closed', variant: 'grey' },

  // Eligibility
  ELIGIBLE: { label: 'Eligible', variant: 'green' },
  NOT_ELIGIBLE: { label: 'Not eligible', variant: 'red' },
};

export default function StatusBadge({ status, className = '', style = {} }) {
  if (!status) return null;

  const key = String(status).toUpperCase();
  const config = STATUS_MAP[key] || {
    label: key
      .toLowerCase()
      .split('_')
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' '),
    variant: 'grey',
  };

  const getVariantStyles = (variant) => {
    switch (variant) {
      case 'green':
        return {
          background: 'var(--status-green-bg)',
          color: 'var(--status-green-fg)',
        };
      case 'amber':
        return {
          background: 'var(--status-amber-bg)',
          color: 'var(--status-amber-fg)',
        };
      case 'red':
        return {
          background: 'var(--status-red-bg)',
          color: 'var(--status-red-fg)',
        };
      case 'grey':
      default:
        return {
          background: 'var(--status-grey-bg)',
          color: 'var(--status-grey-fg)',
        };
    }
  };

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: '0.2rem 0.65rem',
        borderRadius: 'var(--radius-full)',
        fontSize: '0.72rem',
        fontWeight: 500,
        lineHeight: 1.2,
        letterSpacing: '-0.01em',
        border: 'none',
        whiteSpace: 'nowrap',
        ...getVariantStyles(config.variant),
        ...style,
      }}
      className={className}
    >
      {config.label}
    </span>
  );
}
