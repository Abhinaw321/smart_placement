import React, { useState, useEffect } from 'react';
import { Bell, User } from 'lucide-react';
import NotificationDropdown from '../NotificationDropdown';
import { notificationApi } from '../../services/api';

export default function PageHeader({
  title,
  subtitle,
  user,
  onLogout,
  className = '',
  style = {},
}) {
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);

  useEffect(() => {
    if (!user) return;
    const fetchUnread = async () => {
      try {
        const count = await notificationApi.getUnreadCount();
        setUnreadCount(count);
      } catch (err) {
        // Silent fail
      }
    };

    fetchUnread();
    const interval = setInterval(fetchUnread, 15000);
    return () => clearInterval(interval);
  }, [user]);

  return (
    <header
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingBottom: '1.5rem',
        marginBottom: '1.5rem',
        borderBottom: '1px solid var(--border)',
        ...style,
      }}
      className={className}
    >
      {/* Left: Page Title & optional Subtitle */}
      <div>
        <h1
          style={{
            fontSize: '1.5rem',
            fontWeight: 700,
            color: 'var(--text)',
            letterSpacing: '-0.03em',
            lineHeight: 1.2,
          }}
        >
          {title}
        </h1>
        {subtitle && (
          <p
            style={{
              fontSize: '0.84rem',
              color: 'var(--text-muted)',
              marginTop: '0.2rem',
            }}
          >
            {subtitle}
          </p>
        )}
      </div>

      {/* Right: Notification Bell & Avatar */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', position: 'relative' }}>
        {/* Bell Button */}
        <div style={{ position: 'relative' }}>
          <button
            id="notification-bell-btn"
            onClick={() => setShowNotifications(!showNotifications)}
            style={{
              width: '36px',
              height: '36px',
              borderRadius: 'var(--radius-md)',
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: unreadCount > 0 ? 'var(--text)' : 'var(--text-muted)',
              cursor: 'pointer',
              position: 'relative',
              transition: 'border-color 0.12s ease',
            }}
          >
            <Bell size={16} />
            {unreadCount > 0 && (
              <span
                style={{
                  position: 'absolute',
                  top: '6px',
                  right: '6px',
                  width: '6px',
                  height: '6px',
                  borderRadius: '50%',
                  background: 'var(--accent)',
                }}
              />
            )}
          </button>

          {showNotifications && (
            <NotificationDropdown onClose={() => setShowNotifications(false)} />
          )}
        </div>

        {/* User Pill / Avatar */}
        {user && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.3rem 0.65rem 0.3rem 0.4rem',
              borderRadius: 'var(--radius-md)',
              background: 'var(--surface)',
              border: '1px solid var(--border)',
            }}
          >
            <div
              style={{
                width: '26px',
                height: '26px',
                borderRadius: '50%',
                background: 'var(--surface-elevated)',
                border: '1px solid var(--border)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '0.72rem',
                fontWeight: 600,
                color: 'var(--text)',
              }}
            >
              {user.name ? user.name.charAt(0).toUpperCase() : (user.email ? user.email.charAt(0).toUpperCase() : 'U')}
            </div>
            <span
              style={{
                fontSize: '0.78rem',
                fontWeight: 500,
                color: 'var(--text)',
                maxWidth: '120px',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {user.name || user.email?.split('@')[0]}
            </span>
          </div>
        )}
      </div>
    </header>
  );
}
