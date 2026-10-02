import React from 'react';

export default function Card({ children, className = '', style = {}, onClick, ...props }) {
  return (
    <div
      onClick={onClick}
      style={{
        background: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-xl)',
        padding: '1.25rem',
        ...style,
      }}
      className={className}
      {...props}
    >
      {children}
    </div>
  );
}

export function CardHeader({ children, style = {}, className = '' }) {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '1rem',
        ...style,
      }}
      className={className}
    >
      {children}
    </div>
  );
}

export function CardTitle({ children, style = {}, className = '' }) {
  return (
    <h3
      style={{
        fontSize: '1rem',
        fontWeight: 600,
        color: 'var(--text)',
        letterSpacing: '-0.02em',
        ...style,
      }}
      className={className}
    >
      {children}
    </h3>
  );
}

export function CardDescription({ children, style = {}, className = '' }) {
  return (
    <p
      style={{
        fontSize: '0.8rem',
        color: 'var(--text-muted)',
        marginTop: '0.2rem',
        ...style,
      }}
      className={className}
    >
      {children}
    </p>
  );
}
