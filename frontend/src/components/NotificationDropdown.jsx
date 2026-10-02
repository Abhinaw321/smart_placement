import React, { useEffect, useState } from 'react';
import { notificationApi } from '../services/api';
import { Bell, CheckCheck, Clock } from 'lucide-react';

export default function NotificationDropdown({ onClose }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadNotifications = async () => {
    try {
      const res = await notificationApi.getAll('page=0&size=6');
      setNotifications(res.content || []);
    } catch (err) {
      console.error('Failed to load notifications', err);
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
        top: '110%',
        right: 0,
        width: '360px',
        background: '#0f172a',
        border: '1px solid rgba(255, 255, 255, 0.15)',
        borderRadius: '12px',
        boxShadow: '0 20px 40px rgba(0, 0, 0, 0.6)',
        zIndex: 100,
        overflow: 'hidden',
      }}
    >
      <div
        style={{
          padding: '0.85rem 1rem',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          background: 'rgba(0, 0, 0, 0.2)',
        }}
      >
        <span style={{ fontWeight: 600, fontSize: '0.9rem', color: '#fff' }}>
          Notifications
        </span>
        <button
          onClick={handleMarkAllRead}
          style={{
            background: 'none',
            border: 'none',
            color: '#818cf8',
            fontSize: '0.75rem',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem',
          }}
        >
          <CheckCheck size={14} /> Mark all read
        </button>
      </div>

      <div style={{ maxHeight: '340px', overflowY: 'auto' }}>
        {loading ? (
          <div style={{ padding: '1.5rem', textAlign: 'center', color: '#94a3b8' }}>
            Loading alerts...
          </div>
        ) : notifications.length === 0 ? (
          <div style={{ padding: '2rem 1rem', textAlign: 'center', color: '#64748b', fontSize: '0.85rem' }}>
            No notifications yet
          </div>
        ) : (
          notifications.map(n => (
            <div
              key={n.id}
              onClick={(e) => handleMarkAsRead(n.id, e)}
              style={{
                padding: '0.75rem 1rem',
                borderBottom: '1px solid rgba(255, 255, 255, 0.04)',
                background: n.isRead ? 'transparent' : 'rgba(99, 102, 241, 0.08)',
                cursor: 'pointer',
                transition: 'background 0.2s',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.25rem' }}>
                <span style={{ fontWeight: 600, fontSize: '0.85rem', color: n.isRead ? '#cbd5e1' : '#fff' }}>
                  {n.title}
                </span>
                {!n.isRead && (
                  <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#6366f1', display: 'inline-block' }} />
                )}
              </div>
              <p style={{ fontSize: '0.8rem', color: '#94a3b8', margin: 0, lineHeight: 1.4 }}>
                {n.message}
              </p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
