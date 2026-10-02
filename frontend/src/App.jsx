import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './components/ui/Toast';
import AuthPage from './pages/AuthPage';
import StudentPortal from './pages/StudentPortal';
import RecruiterPortal from './pages/RecruiterPortal';
import TpoAdminPortal from './pages/TpoAdminPortal';
import DesignPreviewPage from './pages/DesignPreviewPage';
import Sidebar from './components/ui/Sidebar';
import PageHeader from './components/ui/PageHeader';

function AppContent() {
  const { user, isAuthenticated, loading, logout, isStudent, isRecruiter, isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');

  // Check for design preview route
  if (
    typeof window !== 'undefined' &&
    (window.location.pathname === '/design-preview' || window.location.search.includes('preview'))
  ) {
    return <DesignPreviewPage />;
  }

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

  // Common titles per role
  const studentTitles = {
    overview: 'Overview',
    drives: 'Placement Drives',
    applications: 'Applications',
    interviews: 'Interviews',
    offers: 'Job Offers',
    profile: 'Profile & Resume',
  };

  const recruiterTitles = {
    overview: 'Overview',
    post_drive: 'Post a drive',
    my_drives: 'My drives',
    candidates: 'Candidates',
    interviews: 'Interviews',
    offers: 'Offers',
  };

  const adminTitles = {
    overview: 'Overview',
    students: 'Students',
    companies: 'Companies',
    drives: 'Drives',
    applications: 'Applications',
    reports: 'Reports',
    audit_logs: 'Audit logs',
  };

  let pageTitle = 'Overview';
  let pageSubtitle = 'Recruitment operations dashboard';
  let pageAction = null;

  if (isStudent) {
    pageTitle = studentTitles[activeTab] || 'Overview';
    pageSubtitle = 'Active drives, interview rounds, and offers';
  } else if (isRecruiter) {
    pageTitle = recruiterTitles[activeTab] || 'Overview';
    pageSubtitle = 'Manage campus recruitment pipelines and evaluate candidates';
    if (activeTab !== 'post_drive') {
      pageAction = {
        label: 'Post a drive',
        onClick: () => setActiveTab('post_drive'),
      };
    }
  } else if (isAdmin) {
    pageTitle = adminTitles[activeTab] || 'Overview';
    pageSubtitle = 'Institutional placement metrics, student records, and company audits';
  }

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex' }}>
      <Sidebar
        activeTab={activeTab}
        onTabChange={setActiveTab}
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
            title={pageTitle}
            subtitle={pageSubtitle}
            action={pageAction}
            user={user}
            onLogout={logout}
          />

          {isStudent && <StudentPortal activeTab={activeTab} onTabChange={setActiveTab} />}
          {isRecruiter && <RecruiterPortal activeTab={activeTab} onTabChange={setActiveTab} />}
          {isAdmin && <TpoAdminPortal activeTab={activeTab} onTabChange={setActiveTab} />}
        </div>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <AppContent />
      </ToastProvider>
    </AuthProvider>
  );
}
