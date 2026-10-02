import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../services/api';
import { Sparkles, Shield, User, Briefcase, Lock, Mail, ArrowRight, CheckCircle2 } from 'lucide-react';

export default function AuthPage() {
  const { login } = useAuth();
  const [mode, setMode] = useState('login'); // 'login', 'student_reg', 'recruiter_reg'

  // Form states
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Student specific registration fields
  const [studentForm, setStudentForm] = useState({
    rollNumber: '',
    firstName: '',
    lastName: '',
    phone: '',
    branch: 'Computer Science and Engineering',
    graduationYear: 2026,
    cgpa: 8.5,
  });

  // Recruiter specific registration fields
  const [recruiterForm, setRecruiterForm] = useState({
    companyName: '',
    designation: '',
    phone: '',
  });

  const handleQuickFill = (demoRole) => {
    setError('');
    setSuccessMsg('');
    setMode('login');
    if (demoRole === 'admin') {
      setEmail('admin@smartplacement.com');
      setPassword('Admin@123');
    } else if (demoRole === 'student') {
      setEmail('arjun.varma@univ.edu');
      setPassword('Secret@123');
    } else if (demoRole === 'recruiter') {
      setEmail('recruiter@google.com');
      setPassword('Secret@123');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');
    setLoading(true);

    try {
      if (mode === 'login') {
        await login(email, password);
      } else if (mode === 'student_reg') {
        await authApi.registerStudent({
          email,
          password,
          ...studentForm,
          graduationYear: Number(studentForm.graduationYear),
          cgpa: Number(studentForm.cgpa),
        });
        await login(email, password);
      } else if (mode === 'recruiter_reg') {
        await authApi.registerRecruiter({
          email,
          password,
          ...recruiterForm,
        });
        await login(email, password);
      }
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem 1.5rem',
      }}
    >
      <div style={{ width: '100%', maxWidth: '480px' }}>
        {/* Brand Banner */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div
            style={{
              width: '56px',
              height: '56px',
              borderRadius: '16px',
              background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
              boxShadow: '0 0 25px rgba(99, 102, 241, 0.6)',
              marginBottom: '1rem',
            }}
          >
            <Sparkles size={30} />
          </div>
          <h1 style={{ fontSize: '1.85rem', marginBottom: '0.25rem' }}>
            Smart Placement Portal
          </h1>
          <p style={{ fontSize: '0.9rem', color: '#94a3b8' }}>
            Next-Generation Campus Recruitment & Automated Eligibility Management
          </p>
        </div>

        {/* Demo Account Quick-Fill Pill Bar */}
        <div
          className="glass-card"
          style={{
            padding: '0.75rem',
            marginBottom: '1.5rem',
            background: 'rgba(30, 41, 59, 0.6)',
          }}
        >
          <div style={{ fontSize: '0.75rem', fontWeight: 600, color: '#94a3b8', textAlign: 'center', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Quick Demo Logins (1-Click Fill):
          </div>
          <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
            <button
              type="button"
              onClick={() => handleQuickFill('admin')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', borderColor: 'rgba(245, 158, 11, 0.3)' }}
            >
              <Shield size={12} color="#fbbf24" /> TPO Admin
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('student')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', borderColor: 'rgba(16, 185, 129, 0.3)' }}
            >
              <User size={12} color="#34d399" /> Student
            </button>
            <button
              type="button"
              onClick={() => handleQuickFill('recruiter')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '0.75rem', borderColor: 'rgba(99, 102, 241, 0.3)' }}
            >
              <Briefcase size={12} color="#818cf8" /> Recruiter
            </button>
          </div>
        </div>

        {/* Card Form */}
        <div className="glass-card" style={{ padding: '2rem' }}>
          {/* Mode Switcher Tabs */}
          <div
            style={{
              display: 'flex',
              background: 'rgba(0, 0, 0, 0.3)',
              padding: '0.3rem',
              borderRadius: '10px',
              marginBottom: '1.5rem',
            }}
          >
            <button
              type="button"
              onClick={() => setMode('login')}
              style={{
                flex: 1,
                padding: '0.5rem',
                border: 'none',
                borderRadius: '8px',
                background: mode === 'login' ? 'rgba(99, 102, 241, 0.3)' : 'transparent',
                color: mode === 'login' ? '#fff' : '#94a3b8',
                fontWeight: 600,
                fontSize: '0.85rem',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              Sign In
            </button>
            <button
              type="button"
              onClick={() => setMode('student_reg')}
              style={{
                flex: 1,
                padding: '0.5rem',
                border: 'none',
                borderRadius: '8px',
                background: mode === 'student_reg' ? 'rgba(16, 185, 129, 0.3)' : 'transparent',
                color: mode === 'student_reg' ? '#fff' : '#94a3b8',
                fontWeight: 600,
                fontSize: '0.85rem',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              Student Register
            </button>
            <button
              type="button"
              onClick={() => setMode('recruiter_reg')}
              style={{
                flex: 1,
                padding: '0.5rem',
                border: 'none',
                borderRadius: '8px',
                background: mode === 'recruiter_reg' ? 'rgba(99, 102, 241, 0.3)' : 'transparent',
                color: mode === 'recruiter_reg' ? '#fff' : '#94a3b8',
                fontWeight: 600,
                fontSize: '0.85rem',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              Recruiter
            </button>
          </div>

          {error && (
            <div
              style={{
                padding: '0.75rem 1rem',
                borderRadius: '8px',
                background: 'rgba(239, 68, 68, 0.15)',
                border: '1px solid rgba(239, 68, 68, 0.4)',
                color: '#f87171',
                fontSize: '0.85rem',
                marginBottom: '1.25rem',
              }}
            >
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Common Credentials */}
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <div style={{ position: 'relative' }}>
                <input
                  id="auth-email-input"
                  type="email"
                  required
                  placeholder="name@university.edu"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="form-input"
                  style={{ paddingLeft: '2.5rem' }}
                />
                <Mail size={16} color="#64748b" style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)' }} />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <input
                  id="auth-password-input"
                  type="password"
                  required
                  placeholder="••••••••"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="form-input"
                  style={{ paddingLeft: '2.5rem' }}
                />
                <Lock size={16} color="#64748b" style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)' }} />
              </div>
            </div>

            {/* Student Registration Fields */}
            {mode === 'student_reg' && (
              <>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label className="form-label">First Name</label>
                    <input
                      type="text"
                      required
                      placeholder="Arjun"
                      value={studentForm.firstName}
                      onChange={e => setStudentForm({ ...studentForm, firstName: e.target.value })}
                      className="form-input"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Last Name</label>
                    <input
                      type="text"
                      required
                      placeholder="Varma"
                      value={studentForm.lastName}
                      onChange={e => setStudentForm({ ...studentForm, lastName: e.target.value })}
                      className="form-input"
                    />
                  </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label className="form-label">Roll Number</label>
                    <input
                      type="text"
                      required
                      placeholder="2024CS101"
                      value={studentForm.rollNumber}
                      onChange={e => setStudentForm({ ...studentForm, rollNumber: e.target.value })}
                      className="form-input"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">CGPA (0 - 10)</label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      max="10"
                      required
                      value={studentForm.cgpa}
                      onChange={e => setStudentForm({ ...studentForm, cgpa: e.target.value })}
                      className="form-input"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Branch / Department</label>
                  <select
                    value={studentForm.branch}
                    onChange={e => setStudentForm({ ...studentForm, branch: e.target.value })}
                    className="form-select"
                  >
                    <option value="Computer Science and Engineering">Computer Science and Engineering</option>
                    <option value="Information Technology">Information Technology</option>
                    <option value="Electronics and Communication Engineering">Electronics and Communication Engineering</option>
                    <option value="Mechanical Engineering">Mechanical Engineering</option>
                  </select>
                </div>
              </>
            )}

            {/* Recruiter Registration Fields */}
            {mode === 'recruiter_reg' && (
              <>
                <div className="form-group">
                  <label className="form-label">Company Name</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Google, Atlassian"
                    value={recruiterForm.companyName}
                    onChange={e => setRecruiterForm({ ...recruiterForm, companyName: e.target.value })}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Job Designation</label>
                  <input
                    type="text"
                    required
                    placeholder="Technical University Recruiter"
                    value={recruiterForm.designation}
                    onChange={e => setRecruiterForm({ ...recruiterForm, designation: e.target.value })}
                    className="form-input"
                  />
                </div>
              </>
            )}

            <button
              id="auth-submit-btn"
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '0.75rem', padding: '0.85rem' }}
            >
              {loading ? (
                'Processing...'
              ) : mode === 'login' ? (
                <>Sign In to Portal <ArrowRight size={18} /></>
              ) : (
                <>Complete Registration <CheckCircle2 size={18} /></>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
