import React from 'react';
import Card from './Card';

export default function StatCard({
  label,
  value,
  subtitle,
  indicator,
  indicatorType = 'neutral', // 'positive' | 'warning' | 'negative' | 'neutral'
  icon: Icon,
  style = {},
  ...props
}) {
  const indicatorColor = {
    positive: 'var(--status-green-fg)',
    warning: 'var(--status-amber-fg)',
    negative: 'var(--status-red-fg)',
    neutral: 'var(--text-muted)',
    accent: 'var(--accent)',
  }[indicatorType] || 'var(--text-muted)';

  return (
    <Card style={{ padding: '1.15rem 1.25rem', ...style }} {...props}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.65rem' }}>
        <span
          style={{
            fontSize: '0.72rem',
            fontWeight: 600,
            textTransform: 'uppercase',
            letterSpacing: '0.06em',
            color: 'var(--text-muted)',
          }}
        >
          {label}
        </span>
        {Icon && (
          <div style={{ color: 'var(--text-muted)', opacity: 0.8 }}>
            <Icon size={16} strokeWidth={2} />
          </div>
        )}
      </div>

      <div
        style={{
          fontFamily: 'var(--font-heading)',
          fontSize: '1.85rem',
          fontWeight: 700,
          color: 'var(--text)',
          letterSpacing: '-0.03em',
          lineHeight: 1.1,
          marginBottom: '0.35rem',
        }}
      >
        {value}
      </div>

      {(subtitle || indicator) && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.78rem' }}>
          {indicator && (
            <span style={{ color: indicatorColor, fontWeight: 600, letterSpacing: '-0.01em' }}>
              {indicator}
            </span>
          )}
          {subtitle && (
            <span style={{ color: 'var(--text-muted)', letterSpacing: '-0.01em' }}>
              {subtitle}
            </span>
          )}
        </div>
      )}
    </Card>
  );
}
