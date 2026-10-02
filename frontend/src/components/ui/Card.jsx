import React from 'react';

export default function Card({
  children,
  className = '',
  style = {},
  onClick,
  hoverable = false,
  ...props
}) {
  const [hovered, setHovered] = React.useState(false);

  return (
    <div
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-lg)',
        padding: '1.25rem',
        transition: 'border-color 0.15s ease, background 0.15s ease',
        cursor: onClick ? 'pointer' : 'default',
        ...(hoverable && hovered
          ? {
              borderColor: 'var(--border-focus)',
              background: 'var(--surface-elevated)',
            }
          : {}),
        ...style,
      }}
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      {...props}
    >
      {children}
    </div>
  );
}

export function CardHeader({ children, style = {}, ...props }) {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '0.25rem',
        marginBottom: '1rem',
        ...style,
      }}
      {...props}
    >
      {children}
    </div>
  );
}

export function CardTitle({ children, style = {}, ...props }) {
  return (
    <h3
      style={{
        fontSize: '1.05rem',
        fontWeight: 600,
        color: 'var(--text)',
        letterSpacing: '-0.02em',
        margin: 0,
        ...style,
      }}
      {...props}
    >
      {children}
    </h3>
  );
}

export function CardDescription({ children, style = {}, ...props }) {
  return (
    <p
      style={{
        fontSize: '0.82rem',
        color: 'var(--text-muted)',
        margin: 0,
        lineHeight: 1.4,
        ...style,
      }}
      {...props}
    >
      {children}
    </p>
  );
}
