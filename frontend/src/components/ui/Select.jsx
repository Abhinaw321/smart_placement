import React from 'react';
import { ChevronDown } from 'lucide-react';

export default function Select({
  children,
  className = '',
  style = {},
  disabled = false,
  error = false,
  ...props
}) {
  return (
    <div style={{ position: 'relative', width: '100%', display: 'flex', alignItems: 'center' }}>
      <select
        disabled={disabled}
        className={`ui-control ${className}`}
        style={{
          appearance: 'none',
          paddingRight: '2.25rem',
          cursor: disabled ? 'not-allowed' : 'pointer',
          ...(error ? { borderColor: 'var(--status-red-fg)' } : {}),
          ...style,
        }}
        {...props}
      >
        {children}
      </select>
      <div
        style={{
          position: 'absolute',
          right: '0.75rem',
          color: 'var(--text-muted)',
          display: 'flex',
          alignItems: 'center',
          pointerEvents: 'none',
        }}
      >
        <ChevronDown size={16} strokeWidth={2} />
      </div>
    </div>
  );
}
