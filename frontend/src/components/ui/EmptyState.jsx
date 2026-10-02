import React from 'react';
import Button from './Button';

export default function EmptyState({
  icon: Icon,
  headline,
  description,
  actionLabel,
  onAction,
  style = {},
  ...props
}) {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '3rem 1.5rem',
        borderRadius: 'var(--radius-lg)',
        border: '1px dashed var(--border)',
        background: 'rgba(20, 20, 22, 0.4)',
        ...style,
      }}
      {...props}
    >
      {Icon && (
        <div
          style={{
            width: '42px',
            height: '42px',
            borderRadius: '10px',
            background: 'var(--surface-elevated)',
            border: '1px solid var(--border)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'var(--text-muted)',
            marginBottom: '1rem',
          }}
        >
          <Icon size={20} strokeWidth={2} />
        </div>
      )}

      {headline && (
        <h4
          style={{
            fontSize: '1rem',
            fontWeight: 600,
            color: 'var(--text)',
            marginBottom: '0.35rem',
            letterSpacing: '-0.02em',
          }}
        >
          {headline}
        </h4>
      )}

      {description && (
        <p
          style={{
            fontSize: '0.85rem',
            color: 'var(--text-muted)',
            maxWidth: '360px',
            margin: '0 auto 1.25rem',
            lineHeight: 1.45,
          }}
        >
          {description}
        </p>
      )}

      {actionLabel && onAction && (
        <Button variant="secondary" size="sm" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
}
