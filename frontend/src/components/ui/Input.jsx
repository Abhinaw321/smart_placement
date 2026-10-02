import React from 'react';

export default function Input({
  label,
  error,
  icon: Icon,
  type = 'text',
  className = '',
  style = {},
  id,
  ...props
}) {
  const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', width: '100%', ...style }}>
      {label && (
        <label
          htmlFor={inputId}
          style={{
            fontSize: '0.8rem',
            fontWeight: 500,
            color: 'var(--text-muted)',
            letterSpacing: '-0.01em',
          }}
        >
          {label}
        </label>
      )}

      <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
        {Icon && (
          <div
            style={{
              position: 'absolute',
              left: '0.85rem',
              display: 'flex',
              alignItems: 'center',
              pointerEvents: 'none',
              color: 'var(--text-muted)',
            }}
          >
            <Icon size={16} strokeWidth={2} />
          </div>
        )}

        <input
          id={inputId}
          type={type}
          className="input-base"
          style={{
            ...(Icon ? { paddingLeft: '2.5rem' } : {}),
            ...(error ? { borderColor: 'var(--status-red-border)' } : {}),
          }}
          {...props}
        />
      </div>

      {error && (
        <span style={{ fontSize: '0.75rem', color: 'var(--status-red-fg)', marginTop: '0.15rem' }}>
          {error}
        </span>
      )}
    </div>
  );
}
