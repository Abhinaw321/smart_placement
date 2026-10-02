import React from 'react';

export default function StatCard({ title, value, subtitle, icon: Icon, color = 'indigo' }) {
  const colorMap = {
    indigo: {
      gradient: 'linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(99, 102, 241, 0.05) 100%)',
      border: 'rgba(99, 102, 241, 0.3)',
      text: '#818cf8',
    },
    emerald: {
      gradient: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2) 0%, rgba(16, 185, 129, 0.05) 100%)',
      border: 'rgba(16, 185, 129, 0.3)',
      text: '#34d399',
    },
    amber: {
      gradient: 'linear-gradient(135deg, rgba(245, 158, 11, 0.2) 0%, rgba(245, 158, 11, 0.05) 100%)',
      border: 'rgba(245, 158, 11, 0.3)',
      text: '#fbbf24',
    },
    rose: {
      gradient: 'linear-gradient(135deg, rgba(244, 63, 94, 0.2) 0%, rgba(244, 63, 94, 0.05) 100%)',
      border: 'rgba(244, 63, 94, 0.3)',
      text: '#fb7185',
    },
    cyan: {
      gradient: 'linear-gradient(135deg, rgba(6, 182, 212, 0.2) 0%, rgba(6, 182, 212, 0.05) 100%)',
      border: 'rgba(6, 182, 212, 0.3)',
      text: '#22d3ee',
    },
  };

  const scheme = colorMap[color] || colorMap.indigo;

  return (
    <div
      className="glass-card"
      style={{
        background: scheme.gradient,
        borderColor: scheme.border,
        padding: '1.25rem 1.5rem',
        display: 'flex',
        alignItems: 'center',
        gap: '1rem',
      }}
    >
      {Icon && (
        <div
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            background: 'rgba(0, 0, 0, 0.3)',
            border: `1px solid ${scheme.border}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: scheme.text,
            flexShrink: 0,
          }}
        >
          <Icon size={24} />
        </div>
      )}
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          {title}
        </div>
        <div style={{ fontSize: '1.75rem', fontWeight: 800, fontFamily: 'var(--font-display)', color: '#fff', lineHeight: 1.2, margin: '0.2rem 0' }}>
          {value}
        </div>
        {subtitle && (
          <div style={{ fontSize: '0.75rem', color: '#64748b' }}>
            {subtitle}
          </div>
        )}
      </div>
    </div>
  );
}
