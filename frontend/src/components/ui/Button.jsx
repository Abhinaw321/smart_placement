import React from 'react';

export default function Button({
  children,
  variant = 'primary', // 'primary' | 'secondary' | 'ghost' | 'danger'
  size = 'md',         // 'sm' | 'md' | 'lg'
  disabled = false,
  loading = false,
  icon: Icon,
  className = '',
  style = {},
  ...props
}) {
  const getStyles = () => {
    // Base styles
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
    };

    // Sizes
    const sizes = {
      sm: { padding: '0.35rem 0.65rem', fontSize: '0.78rem', height: '30px' },
      md: { padding: '0.5rem 0.95rem', fontSize: '0.84rem', height: '36px' },
      lg: { padding: '0.65rem 1.25rem', fontSize: '0.9rem', height: '42px' },
    };

    // Variants
    let variantStyles = {};
    if (variant === 'primary') {
      variantStyles = {
        background: 'var(--accent)',
        color: 'var(--accent-fg)',
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
      disabled={disabled || loading}
      style={getStyles()}
      className={`btn-interactive ${className}`}
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
