import React from 'react';
import Card from './Card';
import Button from './Button';

export default function EmptyState({
  icon: Icon,
  title,
  description,
  actionLabel,
  onAction,
  className = '',
  style = {},
}) {
  return (
    <Card
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '3rem 1.5rem',
        ...style,
      }}
      className={className}
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
          <Icon size={20} strokeWidth={1.8} />
        </div>
      )}

      {title && (
        <h4
          style={{
            fontSize: '0.95rem',
            fontWeight: 600,
            color: 'var(--text)',
            marginBottom: '0.35rem',
          }}
        >
          {title}
        </h4>
      )}

      {description && (
        <p
          style={{
            fontSize: '0.82rem',
            color: 'var(--text-muted)',
            maxWidth: '320px',
            lineHeight: 1.45,
            marginBottom: actionLabel ? '1.25rem' : 0,
          }}
        >
          {description}
        </p>
      )}

      {actionLabel && (
        <Button variant="secondary" size="sm" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </Card>
  );
}
