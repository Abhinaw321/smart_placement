import React from 'react';

export default function FormField({
  label,
  helperText,
  error,
  required = false,
  className = '',
  style = {},
  children,
}) {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '0.35rem',
        width: '100%',
        ...style,
      }}
      className={className}
    >
      {label && (
        <label
          style={{
            fontSize: '0.84rem',
            fontWeight: 500,
            color: 'var(--text-muted)',
            fontFamily: 'var(--font-body)',
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem',
          }}
        >
          {label}
          {required && <span style={{ color: 'var(--accent)' }}>*</span>}
        </label>
      )}

      {children}

      {error ? (
        <span style={{ fontSize: '0.74rem', color: 'var(--status-red-fg)', marginTop: '0.1rem' }}>
          {error}
        </span>
      ) : helperText ? (
        <span style={{ fontSize: '0.74rem', color: 'var(--text-subtle)', marginTop: '0.1rem' }}>
          {helperText}
        </span>
      ) : null}
    </div>
  );
}

export function FormSection({ title, description, children, style = {} }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', ...style }}>
      <div>
        <h3
          style={{
            fontSize: '1.05rem',
            fontWeight: 600,
            color: 'var(--text)',
            letterSpacing: '-0.02em',
          }}
        >
          {title}
        </h3>
        {description && (
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
            {description}
          </p>
        )}
      </div>
      <div style={{ height: '1px', background: 'var(--border)', width: '100%' }} />
      <div>{children}</div>
    </div>
  );
}
