import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { notificationApi } from '../services/api';
import NotificationDropdown from './NotificationDropdown';
import { Sparkles, Bell, LogOut, Shield, GraduationCap, Briefcase } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab }) {
  const { user, logout, isStudent, isRecruiter, isAdmin } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);

  useEffect(() => {
    if (!user) return;
    const fetchUnread = async () => {
      try {
        const count = await notificationApi.getUnreadCount();
        setUnreadCount(count);
      } catch (err) {
        // silent fail
      }
    };

    fetchUnread();
    const interval = setInterval(fetchUnread, 15000); // 15s poll
    return () => clearInterval(interval);
  }, [user]);

  const getRoleBadge = () => {
    if (isAdmin) {
      return (
        <span className="badge badge-warning" style={{ fontSize: '0.7rem' }}>
          <Shield size={12} /> TPO Admin
        </span>
      );
    }
    if (isRecruiter) {
      return (
        <span className="badge badge-primary" style={{ fontSize: '0.7rem' }}>
          <Briefcase size={12} /> Recruiter
        </span>
      );
    }
    return (
      <span className="badge badge-success" style={{ fontSize: '0.7rem' }}>
        <GraduationCap size={12} /> Student
      </span>
    );
  };

  return (
    <nav
      style={{
        background: 'rgba(9, 13, 22, 0.85)',
        backdropFilter: 'blur(16px)',
        borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
        position: 'sticky',
        top: 0,
        zIndex: 50,
      }}
    >
      <div
        className="app-container"
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          height: '70px',
        }}
      >
        {/* Brand */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
              boxShadow: '0 0 15px rgba(99, 102, 241, 0.5)',
            }}
          >
            <Sparkles size={22} />
          </div>
          <div>
            <div style={{ fontWeight: 800, fontSize: '1.25rem', fontFamily: 'var(--font-display)', color: '#fff', letterSpacing: '-0.02em' }}>
              SmartPlacement
            </div>
            <div style={{ fontSize: '0.7rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Campus Recruitment Portal
            </div>
          </div>
        </div>

        {/* Navigation / Actions */}
        {user && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
            {/* Notification Bell */}
            <div style={{ position: 'relative' }}>
              <button
                id="notification-bell-btn"
                onClick={() => setShowNotifications(!showNotifications)}
                style={{
                  background: 'rgba(255, 255, 255, 0.05)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  borderRadius: '10px',
                  width: '40px',
                  height: '40px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: unreadCount > 0 ? '#818cf8' : '#94a3b8',
                  cursor: 'pointer',
                  position: 'relative',
                  transition: 'all 0.2s',
                }}
              >
                <Bell size={18} />
                {unreadCount > 0 && (
                  <span
                    style={{
                      position: 'absolute',
                      top: '-4px',
                      right: '-4px',
                      background: '#ef4444',
                      color: '#fff',
                      borderRadius: '50%',
                      width: '18px',
                      height: '18px',
                      fontSize: '0.65rem',
                      fontWeight: 700,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 0 8px rgba(239, 68, 68, 0.6)',
                    }}
                  >
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </button>

              {showNotifications && (
                <NotificationDropdown onClose={() => setShowNotifications(false)} />
              )}
            </div>

            {/* User Pill */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.75rem',
                background: 'rgba(255, 255, 255, 0.04)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                padding: '0.35rem 0.75rem 0.35rem 0.5rem',
                borderRadius: '50px',
              }}
            >
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.85rem',
                  fontWeight: 700,
                  color: '#fff',
                }}
              >
                {user.name ? user.name.charAt(0).toUpperCase() : user.email.charAt(0).toUpperCase()}
              </div>
              <div style={{ textAlign: 'left', lineHeight: 1.2 }}>
                <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f8fafc' }}>
                  {user.name || user.email}
                </div>
                <div>{getRoleBadge()}</div>
              </div>
            </div>

            {/* Logout */}
            <button
              id="logout-btn"
              onClick={logout}
              className="btn btn-secondary btn-sm"
              title="Sign Out"
              style={{ padding: '0.5rem', borderRadius: '10px' }}
            >
              <LogOut size={16} />
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}
