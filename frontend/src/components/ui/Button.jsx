import React from 'react';

export default function Button({
  children,
  variant = 'primary', // 'primary' | 'secondary' | 'ghost' | 'danger'
  size = 'md',         // 'sm' | 'md'
  disabled = false,
  loading = false,
  icon: Icon,
  className = '',
  style = {},
  type = 'button',
  ...props
}) {
  const getStyles = () => {
    const base = {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '0.45rem',
      fontWeight: 500,
      fontFamily: 'var(--font-body)',
      borderRadius: 'var(--radius-md)',
      cursor: disabled || loading ? 'not-allowed' : 'pointer',
      opacity: disabled || loading ? 0.5 : 1,
      transition: 'all 0.12s ease',
      outline: 'none',
      borderWidth: '1px',
      borderStyle: 'solid',
      borderColor: 'transparent',
      textDecoration: 'none',
      userSelect: 'none',
      whiteSpace: 'nowrap',
      lineHeight: 1,
    };

    const sizes = {
      sm: { height: '32px', padding: '0 0.75rem', fontSize: '0.78rem' },
      md: { height: '40px', padding: '0 1rem', fontSize: '0.86rem' },
    };

    let variantStyles = {};
    if (variant === 'primary') {
      variantStyles = {
        background: 'var(--accent)',
        color: '#0A0A0B',
        fontWeight: 600,
        borderColor: 'var(--accent)',
      };
    } else if (variant === 'secondary') {
      variantStyles = {
        background: 'var(--surface)',
        borderColor: 'var(--border)',
        color: 'var(--text)',
      };
    } else if (variant === 'ghost') {
      variantStyles = {
        background: 'transparent',
        borderColor: 'transparent',
        color: 'var(--text-muted)',
      };
    } else if (variant === 'danger') {
      variantStyles = {
        background: 'var(--status-red-bg)',
        borderColor: 'transparent',
        color: 'var(--status-red-fg)',
      };
    }

    return { ...base, ...sizes[size], ...variantStyles, ...style };
  };

  return (
    <button
      type={type}
      disabled={disabled || loading}
      style={getStyles()}
      className={`btn-ui ${className}`}
      {...props}
    >
      {loading ? (
        <span
          style={{
            width: '14px',
            height: '14px',
            border: '2px solid currentColor',
            borderRightColor: 'transparent',
            borderRadius: '50%',
            display: 'inline-block',
            animation: 'spin 0.6s linear infinite',
          }}
        />
      ) : (
        Icon && <Icon size={size === 'sm' ? 14 : 16} strokeWidth={2} />
      )}
      {children}
    </button>
  );
}
