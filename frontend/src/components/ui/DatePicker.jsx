import React from 'react';
import { Calendar } from 'lucide-react';

export default function DatePicker({
  className = '',
  style = {},
  disabled = false,
  error = false,
  ...props
}) {
  return (
    <div style={{ position: 'relative', width: '100%', display: 'flex', alignItems: 'center' }}>
      <input
        type="date"
        disabled={disabled}
        className={`ui-control ${className}`}
        style={{
          paddingLeft: '2.4rem',
          ...(error ? { borderColor: 'var(--status-red-fg)' } : {}),
          ...style,
        }}
        {...props}
      />
      <div
        style={{
          position: 'absolute',
          left: '0.75rem',
          color: 'var(--text-muted)',
          display: 'flex',
          alignItems: 'center',
          pointerEvents: 'none',
        }}
      >
        <Calendar size={16} strokeWidth={2} />
      </div>
    </div>
  );
}
