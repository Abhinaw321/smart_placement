import React from 'react';

export default function Tabs({
  tabs = [],
  activeTab,
  onChange,
  className = '',
  style = {},
}) {
  return (
    <div
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: '3px',
        backgroundColor: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-md)',
        gap: '2px',
        ...style,
      }}
      className={className}
    >
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;
        const Icon = tab.icon;

        return (
          <button
            key={tab.id}
            type="button"
            onClick={() => onChange && onChange(tab.id)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              padding: '0.35rem 0.75rem',
              fontSize: '0.8rem',
              fontWeight: isActive ? 600 : 500,
              fontFamily: 'var(--font-body)',
              color: isActive ? 'var(--text)' : 'var(--text-muted)',
              backgroundColor: isActive ? 'var(--surface-elevated)' : 'transparent',
              border: 'none',
              borderRadius: 'var(--radius-sm)',
              cursor: 'pointer',
              transition: 'all 0.12s ease',
              whiteSpace: 'nowrap',
            }}
          >
            {Icon && <Icon size={14} style={{ color: isActive ? 'var(--accent)' : 'inherit' }} />}
            <span>{tab.label}</span>
            {tab.count !== undefined && (
              <span
                style={{
                  fontSize: '0.68rem',
                  padding: '0.1rem 0.35rem',
                  borderRadius: 'var(--radius-full)',
                  backgroundColor: isActive ? 'var(--accent-subtle)' : 'var(--border)',
                  color: isActive ? 'var(--accent)' : 'var(--text-muted)',
                }}
              >
                {tab.count}
              </span>
            )}
          </button>
        );
      })}
    </div>
  );
}
