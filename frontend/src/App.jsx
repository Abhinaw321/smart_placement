import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import AuthPage from './pages/AuthPage';
import StudentPortal from './pages/StudentPortal';
import RecruiterPortal from './pages/RecruiterPortal';
import TpoAdminPortal from './pages/TpoAdminPortal';
import Sidebar from './components/ui/Sidebar';
import PageHeader from './components/ui/PageHeader';

function AppContent() {
  const { user, isAuthenticated, loading, logout, isStudent, isRecruiter, isAdmin } = useAuth();
  const [studentTab, setStudentTab] = useState('overview');

  if (loading) {
    return (
      <div
        style={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: 'var(--bg)',
          color: 'var(--text-muted)',
          fontFamily: 'var(--font-body)',
        }}
      >
        <div style={{ textAlign: 'center' }}>
          <div
            style={{
              width: '32px',
              height: '32px',
              border: '2px solid var(--border)',
              borderTopColor: 'var(--accent)',
              borderRadius: '50%',
              animation: 'spin 0.6s linear infinite',
              margin: '0 auto 1rem',
            }}
          />
          <div style={{ fontSize: '0.85rem', fontWeight: 500 }}>Loading PlacementOS...</div>
        </div>
      </div>
    );
  }

  if (!isAuthenticated || !user) {
    return <AuthPage />;
  }

  if (isStudent) {
    const titles = {
      overview: 'Overview',
      drives: 'Placement Drives',
      applications: 'Applications',
      interviews: 'Interviews',
      offers: 'Job Offers',
      profile: 'Profile & Resume',
    };

    return (
      <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex' }}>
        <Sidebar
          activeTab={studentTab}
          onTabChange={setStudentTab}
          user={user}
          onLogout={logout}
        />
        <main
          style={{
            marginLeft: '240px',
            flex: 1,
            minHeight: '100vh',
            background: 'var(--bg)',
            overflowY: 'auto',
          }}
        >
          <div className="max-w-7xl mx-auto px-6 py-8">
            <PageHeader
              title={titles[studentTab] || 'Overview'}
              user={user}
              onLogout={logout}
            />
            <StudentPortal activeTab={studentTab} onTabChange={setStudentTab} />
          </div>
        </main>
      </div>
    );
  }

  // Recruiter / Admin fallback (will be redesigned after user approval)
  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)' }}>
      {isRecruiter && <RecruiterPortal />}
      {isAdmin && <TpoAdminPortal />}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
