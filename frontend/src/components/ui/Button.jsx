import React from 'react';

export default function Button({
  children,
  variant = 'primary', // 'primary' | 'secondary' | 'ghost' | 'danger'
  size = 'md', // 'sm' | 'md' | 'lg'
  icon: Icon,
  loading = false,
  disabled = false,
  className = '',
  style = {},
  ...props
}) {
  const baseStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '0.45rem',
    borderRadius: '8px',
    fontWeight: 600,
    cursor: disabled || loading ? 'not-allowed' : 'pointer',
    opacity: disabled || loading ? 0.6 : 1,
    transition: 'all 0.15s cubic-bezier(0.16, 1, 0.3, 1)',
    border: 'none',
    outline: 'none',
    whiteSpace: 'nowrap',
    userSelect: 'none',
    ...style,
  };

  const sizeStyles = {
    sm: { padding: '0.35rem 0.65rem', fontSize: '0.8rem', height: '30px' },
    md: { padding: '0.55rem 0.95rem', fontSize: '0.85rem', height: '38px' },
    lg: { padding: '0.75rem 1.25rem', fontSize: '0.95rem', height: '44px' },
  };

  const variantStyles = {
    primary: {
      background: 'var(--accent)',
      color: 'var(--accent-fg)',
      boxShadow: '0 1px 2px rgba(0, 0, 0, 0.3)',
    },
    secondary: {
      background: 'var(--surface)',
      border: '1px solid var(--border)',
      color: 'var(--text)',
    },
    ghost: {
      background: 'transparent',
      color: 'var(--text-muted)',
    },
    danger: {
      background: 'var(--status-red-bg)',
      color: 'var(--status-red-fg)',
      border: '1px solid var(--status-red-border)',
    },
  };

  const [hovered, setHovered] = React.useState(false);
  const [pressed, setPressed] = React.useState(false);

  let dynamicStyle = { ...baseStyle, ...sizeStyles[size], ...variantStyles[variant] };

  if (hovered && !disabled && !loading) {
    if (variant === 'primary') dynamicStyle.background = 'var(--accent-hover)';
    if (variant === 'secondary') {
      dynamicStyle.background = 'var(--surface-hover)';
      dynamicStyle.borderColor = 'var(--border-focus)';
    }
    if (variant === 'ghost') {
      dynamicStyle.background = 'var(--surface-hover)';
      dynamicStyle.color = 'var(--text)';
    }
  }

  if (pressed && !disabled && !loading) {
    dynamicStyle.transform = 'scale(0.98)';
  }

  return (
    <button
      style={dynamicStyle}
      disabled={disabled || loading}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => { setHovered(false); setPressed(false); }}
      onMouseDown={() => setPressed(true)}
      onMouseUp={() => setPressed(false)}
      {...props}
    >
      {loading ? (
        <span
          style={{
            width: '14px',
            height: '14px',
            border: '2px solid currentColor',
            borderTopColor: 'transparent',
            borderRadius: '50%',
            animation: 'spin 0.6s linear infinite',
          }}
        />
      ) : (
        Icon && <Icon size={size === 'sm' ? 14 : 16} strokeWidth={2.2} />
      )}
      {children}
    </button>
  );
}
