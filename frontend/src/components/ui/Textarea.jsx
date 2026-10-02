import React from 'react';

export default function Textarea({
  className = '',
  style = {},
  disabled = false,
  error = false,
  rows = 4,
  ...props
}) {
  return (
    <textarea
      rows={rows}
      disabled={disabled}
      className={`ui-control ${className}`}
      style={{
        ...(error ? { borderColor: 'var(--status-red-fg)' } : {}),
        ...style,
      }}
      {...props}
    />
  );
}
