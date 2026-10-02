import React, { useEffect, useState } from 'react';
import { notificationApi } from '../services/api';
import { CheckCheck } from 'lucide-react';

export default function NotificationDropdown({ onClose }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadNotifications = async () => {
    try {
      const res = await notificationApi.getAll('page=0&size=6');
      setNotifications(res.content || []);
    } catch (err) {
      // Silent fail
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id, e) => {
    e.stopPropagation();
    try {
      await notificationApi.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div
      style={{
        position: 'absolute',
        top: '115%',
        right: 0,
        width: '340px',
        background: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-xl)',
        zIndex: 100,
        overflow: 'hidden',
      }}
    >
      <div
        style={{
          padding: '0.85rem 1rem',
          borderBottom: '1px solid var(--border)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          background: 'var(--surface-elevated)',
        }}
      >
        <span style={{ fontWeight: 600, fontSize: '0.85rem', color: 'var(--text)' }}>
          Notifications
        </span>
        <button
          onClick={handleMarkAllRead}
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--text-muted)',
            fontSize: '0.74rem',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem',
            transition: 'color 0.12s',
          }}
        >
          <CheckCheck size={13} /> Mark all read
        </button>
      </div>

      <div style={{ maxHeight: '320px', overflowY: 'auto' }}>
        {loading ? (
          <div style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
            Loading updates...
          </div>
        ) : notifications.length === 0 ? (
          <div style={{ padding: '2rem 1rem', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.82rem' }}>
            All caught up. No notifications.
          </div>
        ) : (
          notifications.map(n => (
            <div
              key={n.id}
              onClick={(e) => handleMarkAsRead(n.id, e)}
              style={{
                padding: '0.75rem 1rem',
                borderBottom: '1px solid var(--border)',
                background: n.isRead ? 'transparent' : 'rgba(198, 255, 61, 0.04)',
                cursor: 'pointer',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.2rem' }}>
                <span style={{ fontWeight: 600, fontSize: '0.82rem', color: n.isRead ? 'var(--text-muted)' : 'var(--text)' }}>
                  {n.title}
                </span>
                {!n.isRead && (
                  <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: 'var(--accent)', display: 'inline-block', flexShrink: 0, marginTop: '4px' }} />
                )}
              </div>
              <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', margin: 0, lineHeight: 1.4 }}>
                {n.message}
              </p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
