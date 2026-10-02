import React from 'react';
import {
  LayoutDashboard,
  Briefcase,
  FileText,
  Calendar,
  Award,
  User,
  Users,
  Building2,
  GraduationCap,
  BarChart3,
  Shield,
  PlusCircle,
  LogOut,
  Compass,
} from 'lucide-react';

const ROLE_NAV_ITEMS = {
  ROLE_STUDENT: [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'drives', label: 'Drives', icon: Briefcase },
    { id: 'applications', label: 'Applications', icon: FileText },
    { id: 'interviews', label: 'Interviews', icon: Calendar },
    { id: 'offers', label: 'Offers', icon: Award },
    { id: 'profile', label: 'Profile', icon: User },
  ],
  ROLE_RECRUITER: [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'post_drive', label: 'Post a drive', icon: PlusCircle },
    { id: 'my_drives', label: 'My drives', icon: Briefcase },
    { id: 'candidates', label: 'Candidates', icon: Users },
    { id: 'interviews', label: 'Interviews', icon: Calendar },
    { id: 'offers', label: 'Offers', icon: Award },
  ],
  ROLE_TPO_ADMIN: [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'students', label: 'Students', icon: GraduationCap },
    { id: 'companies', label: 'Companies', icon: Building2 },
    { id: 'drives', label: 'Drives', icon: Briefcase },
    { id: 'applications', label: 'Applications', icon: FileText },
    { id: 'reports', label: 'Reports', icon: BarChart3 },
    { id: 'audit_logs', label: 'Audit logs', icon: Shield },
  ],
};

export default function Sidebar({
  activeTab = 'overview',
  onTabChange,
  user,
  onLogout,
  className = '',
  style = {},
}) {
  const role = user?.role || 'ROLE_STUDENT';
  const navItems = ROLE_NAV_ITEMS[role] || ROLE_NAV_ITEMS.ROLE_STUDENT;

  const getRoleLabel = () => {
    if (role === 'ROLE_TPO_ADMIN') return 'TPO Administration';
    if (role === 'ROLE_RECRUITER') return 'Corporate Recruiter';
    return 'Candidate Portal';
  };

  return (
    <aside
      style={{
        width: '240px',
        minWidth: '240px',
        height: '100vh',
        position: 'fixed',
        top: 0,
        left: 0,
        background: 'var(--bg)',
        borderRight: '1px solid var(--border)',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '1.25rem 0.75rem',
        zIndex: 40,
        ...style,
      }}
      className={className}
    >
      {/* Top: Brand Header */}
      <div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.65rem',
            padding: '0.5rem 0.65rem 1.5rem',
          }}
        >
          <div
            style={{
              width: '28px',
              height: '28px',
              borderRadius: '6px',
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--accent)',
            }}
          >
            <Compass size={16} strokeWidth={2.2} />
          </div>
          <div>
            <div
              style={{
                fontFamily: 'var(--font-heading)',
                fontSize: '0.95rem',
                fontWeight: 700,
                color: 'var(--text)',
                letterSpacing: '-0.02em',
                lineHeight: 1.1,
              }}
            >
              PlacementOS
            </div>
            <div style={{ fontSize: '0.65rem', color: 'var(--text-subtle)' }}>
              {getRoleLabel()}
            </div>
          </div>
        </div>

        {/* Navigation List */}
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.2rem' }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;

            return (
              <button
                key={item.id}
                type="button"
                onClick={() => onTabChange && onTabChange(item.id)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.75rem',
                  width: '100%',
                  padding: '0.55rem 0.75rem',
                  fontSize: '0.84rem',
                  fontWeight: isActive ? 600 : 500,
                  fontFamily: 'var(--font-body)',
                  color: isActive ? 'var(--text)' : 'var(--text-muted)',
                  background: isActive ? 'var(--surface)' : 'transparent',
                  border: 'none',
                  borderRadius: 'var(--radius-md)',
                  position: 'relative',
                  cursor: 'pointer',
                  transition: 'all 0.12s ease',
                  textAlign: 'left',
                }}
              >
                {/* Accent indicator on the left for active item */}
                {isActive && (
                  <div
                    style={{
                      position: 'absolute',
                      left: 0,
                      top: '20%',
                      bottom: '20%',
                      width: '3px',
                      borderRadius: '0 2px 2px 0',
                      background: 'var(--accent)',
                    }}
                  />
                )}
                <Icon
                  size={16}
                  strokeWidth={isActive ? 2.2 : 1.8}
                  style={{
                    color: isActive ? 'var(--accent)' : 'var(--text-muted)',
                    flexShrink: 0,
                  }}
                />
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* Bottom: User Profile & Logout */}
      {user && (
        <div
          style={{
            borderTop: '1px solid var(--border)',
            paddingTop: '0.85rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            paddingLeft: '0.35rem',
            paddingRight: '0.35rem',
          }}
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.65rem',
              overflow: 'hidden',
            }}
          >
            <div
              style={{
                width: '30px',
                height: '30px',
                borderRadius: '50%',
                background: 'var(--surface)',
                border: '1px solid var(--border)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '0.75rem',
                fontWeight: 600,
                color: 'var(--text)',
                flexShrink: 0,
              }}
            >
              {user.name ? user.name.charAt(0).toUpperCase() : (user.email ? user.email.charAt(0).toUpperCase() : 'U')}
            </div>
            <div style={{ overflow: 'hidden', lineHeight: 1.2 }}>
              <div
                style={{
                  fontSize: '0.8rem',
                  fontWeight: 600,
                  color: 'var(--text)',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                }}
              >
                {user.name || user.email?.split('@')[0]}
              </div>
              <div
                style={{
                  fontSize: '0.68rem',
                  color: 'var(--text-subtle)',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                }}
              >
                {user.email}
              </div>
            </div>
          </div>

          <button
            type="button"
            onClick={onLogout}
            title="Sign out"
            style={{
              background: 'transparent',
              border: 'none',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              padding: '0.4rem',
              borderRadius: 'var(--radius-sm)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              transition: 'color 0.12s ease',
            }}
          >
            <LogOut size={15} />
          </button>
        </div>
      )}
    </aside>
  );
}
