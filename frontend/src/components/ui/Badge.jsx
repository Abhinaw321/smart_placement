import React from 'react';

export default function Badge({
  children,
  variant = 'grey', // 'green' | 'amber' | 'red' | 'grey' | 'accent'
  className = '',
  style = {},
  ...props
}) {
  const getVariantStyles = () => {
    switch (variant) {
      case 'green':
      case 'selected':
      case 'offer':
      case 'ACCEPTED':
      case 'ELIGIBLE':
        return {
          background: 'var(--status-green-bg)',
          color: 'var(--status-green-fg)',
        };
      case 'amber':
      case 'pending':
      case 'scheduled':
      case 'PENDING':
      case 'INTERVIEW_SCHEDULED':
      case 'SHORTLISTED':
        return {
          background: 'var(--status-amber-bg)',
          color: 'var(--status-amber-fg)',
        };
      case 'red':
      case 'rejected':
      case 'REJECTED':
      case 'NOT_ELIGIBLE':
        return {
          background: 'var(--status-red-bg)',
          color: 'var(--status-red-fg)',
        };
      case 'accent':
        return {
          background: 'var(--accent-subtle)',
          color: 'var(--accent)',
        };
      case 'grey':
      case 'applied':
      case 'APPLIED':
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
        ...getVariantStyles(),
        ...style,
      }}
      className={className}
      {...props}
    >
      {children}
    </span>
  );
}
