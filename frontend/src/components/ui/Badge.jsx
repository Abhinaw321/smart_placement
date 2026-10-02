import React from 'react';

export default function Badge({
  children,
  variant = 'neutral', // 'accent' | 'success' | 'warning' | 'danger' | 'neutral'
  dot = false,
  icon: Icon,
  style = {},
  ...props
}) {
  const variantStyles = {
    accent: {
      background: 'var(--accent-muted)',
      color: 'var(--accent)',
      borderColor: 'var(--accent-border)',
      dotColor: 'var(--accent)',
    },
    success: {
      background: 'var(--status-green-bg)',
      color: 'var(--status-green-fg)',
      borderColor: 'var(--status-green-border)',
      dotColor: 'var(--status-green-fg)',
    },
    warning: {
      background: 'var(--status-amber-bg)',
      color: 'var(--status-amber-fg)',
      borderColor: 'var(--status-amber-border)',
      dotColor: 'var(--status-amber-fg)',
    },
    danger: {
      background: 'var(--status-red-bg)',
      color: 'var(--status-red-fg)',
      borderColor: 'var(--status-red-border)',
      dotColor: 'var(--status-red-fg)',
    },
    neutral: {
      background: 'var(--surface)',
      color: 'var(--text-muted)',
      borderColor: 'var(--border)',
      dotColor: 'var(--text-subtle)',
    },
  };

  const current = variantStyles[variant] || variantStyles.neutral;

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '0.35rem',
        padding: '0.2rem 0.55rem',
        borderRadius: 'var(--radius-sm)',
        fontSize: '0.72rem',
        fontWeight: 600,
        letterSpacing: '-0.01em',
        background: current.background,
        color: current.color,
        border: `1px solid ${current.borderColor}`,
        whiteSpace: 'nowrap',
        lineHeight: 1.3,
        ...style,
      }}
      {...props}
    >
      {dot && (
        <span
          style={{
            width: '5px',
            height: '5px',
            borderRadius: '50%',
            background: current.dotColor,
            flexShrink: 0,
          }}
        />
      )}
      {Icon && <Icon size={12} strokeWidth={2.2} />}
      {children}
    </span>
  );
}
