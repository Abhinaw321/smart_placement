import React from 'react';
import Card from './Card';

export default function StatCard({ label, value, icon: Icon, subtext, className = '', style = {} }) {
  return (
    <Card
      style={{
        height: '115px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '1rem 1.15rem',
        ...style,
      }}
      className={className}
    >
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span
          style={{
            fontSize: '0.82rem',
            color: 'var(--text-muted)',
            fontWeight: 500,
          }}
        >
          {label}
        </span>
        {Icon && (
          <Icon
            size={16}
            style={{
              color: 'var(--text-muted)',
              opacity: 0.7,
            }}
          />
        )}
      </div>

      <div>
        <div
          className="stat-value"
          style={{
            fontSize: '1.85rem',
            fontWeight: 700,
            color: '#FFFFFF',
            lineHeight: 1.1,
          }}
        >
          {value}
        </div>
        {subtext && (
          <div
            style={{
              fontSize: '0.72rem',
              color: 'var(--text-muted)',
              marginTop: '0.2rem',
            }}
          >
            {subtext}
          </div>
        )}
      </div>
    </Card>
  );
}
