import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../services/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Input from '../components/ui/Input';
import Badge from '../components/ui/Badge';
import {
  Compass,
  ArrowRight,
  AlertCircle,
  CheckCircle2,
  Mail,
  Lock,
  User,
  Building2,
  Phone,
  GraduationCap,
  Sparkles,
} from 'lucide-react';

export default function AuthPage() {
  const { login } = useAuth();
  const [mode, setMode] = useState('login'); // 'login' | 'student_reg' | 'recruiter_reg'

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
    designation: 'Technical Talent Lead',
    phone: '',
  });

  const handleQuickFill = (role) => {
    setError('');
    setSuccessMsg('');
    setMode('login');
    if (role === 'admin') {
      setEmail('admin@smartplacement.com');
      setPassword('Admin@123');
    } else if (role === 'student') {
      setEmail('student@smartplacement.com');
      setPassword('Student@123');
    } else if (role === 'recruiter') {
      setEmail('recruiter@google.com');
      setPassword('Recruiter@123');
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
      setError(err.message || 'Authentication failed. Check credentials and try again.');
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
        padding: '2rem 1rem',
        background: 'var(--bg)',
      }}
    >
      <div style={{ width: '100%', maxWidth: '440px' }} className="animate-fade-in">
        {/* Brand Header */}
        <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--accent)',
              marginBottom: '1rem',
            }}
          >
            <Compass size={22} strokeWidth={2.2} />
          </div>

          <h1
            style={{
              fontSize: '1.65rem',
              fontWeight: 700,
              color: 'var(--text)',
              letterSpacing: '-0.03em',
              marginBottom: '0.35rem',
            }}
          >
            {mode === 'login'
              ? 'Sign in to PlacementOS'
              : mode === 'student_reg'
              ? 'Join as Candidate'
              : 'Join as Corporate Recruiter'}
          </h1>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            {mode === 'login'
              ? 'Campus hiring, minus the spreadsheet trauma.'
              : mode === 'student_reg'
              ? 'Build your profile, auto-verify eligibility, and land offers.'
              : 'Publish placement drives and interview top campus talent.'}
          </p>
        </div>

        {/* Auth Card */}
        <Card style={{ padding: '1.75rem' }}>
          {/* Segmented Mode Selector */}
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(3, 1fr)',
              background: 'var(--bg)',
              padding: '3px',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border)',
              marginBottom: '1.25rem',
              gap: '2px',
            }}
          >
            {[
              { id: 'login', label: 'Sign In' },
              { id: 'student_reg', label: 'Student' },
              { id: 'recruiter_reg', label: 'Recruiter' },
            ].map((tab) => {
              const active = mode === tab.id;
              return (
                <button
                  key={tab.id}
                  type="button"
                  onClick={() => {
                    setMode(tab.id);
                    setError('');
                  }}
                  style={{
                    padding: '0.45rem 0.5rem',
                    fontSize: '0.78rem',
                    fontWeight: active ? 600 : 500,
                    borderRadius: 'var(--radius-sm)',
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'all 0.12s ease',
                    background: active ? 'var(--surface-elevated)' : 'transparent',
                    color: active ? 'var(--text)' : 'var(--text-muted)',
                    boxShadow: active ? '0 1px 2px rgba(0,0,0,0.4)' : 'none',
                  }}
                >
                  {tab.label}
                </button>
              );
            })}
          </div>

          {/* Quick Demo Fill (Linear style) */}
          {mode === 'login' && (
            <div
              style={{
                background: 'rgba(198, 255, 61, 0.04)',
                border: '1px solid var(--accent-border)',
                borderRadius: 'var(--radius-md)',
                padding: '0.75rem 0.85rem',
                marginBottom: '1.25rem',
              }}
            >
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  marginBottom: '0.45rem',
                }}
              >
                <span
                  style={{
                    fontSize: '0.7rem',
                    fontWeight: 600,
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em',
                    color: 'var(--accent)',
                  }}
                >
                  1-Click Demo Accounts
                </span>
                <Badge variant="accent" style={{ fontSize: '0.65rem', padding: '0.1rem 0.4rem' }}>
                  Live MySQL
                </Badge>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.4rem' }}>
                <Button
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={() => handleQuickFill('student')}
                  style={{ fontSize: '0.72rem', height: '28px' }}
                >
                  Student
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={() => handleQuickFill('recruiter')}
                  style={{ fontSize: '0.72rem', height: '28px' }}
                >
                  Recruiter
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={() => handleQuickFill('admin')}
                  style={{ fontSize: '0.72rem', height: '28px' }}
                >
                  TPO Admin
                </Button>
              </div>
            </div>
          )}

          {/* Error Banner */}
          {error && (
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                background: 'var(--status-red-bg)',
                border: '1px solid var(--status-red-border)',
                borderRadius: 'var(--radius-md)',
                padding: '0.65rem 0.85rem',
                color: 'var(--status-red-fg)',
                fontSize: '0.8rem',
                fontWeight: 500,
                marginBottom: '1.25rem',
              }}
            >
              <AlertCircle size={15} style={{ flexShrink: 0 }} />
              <span>{error}</span>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '0.9rem' }}>
            <Input
              label="Email Address"
              type="email"
              required
              placeholder="you@institution.edu"
              icon={Mail}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />

            <Input
              label="Password"
              type="password"
              required
              placeholder="••••••••••••"
              icon={Lock}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            {/* Student Registration Fields */}
            {mode === 'student_reg' && (
              <>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                  <Input
                    label="First Name"
                    required
                    placeholder="Alex"
                    value={studentForm.firstName}
                    onChange={(e) => setStudentForm({ ...studentForm, firstName: e.target.value })}
                  />
                  <Input
                    label="Last Name"
                    required
                    placeholder="Rivera"
                    value={studentForm.lastName}
                    onChange={(e) => setStudentForm({ ...studentForm, lastName: e.target.value })}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '0.75rem' }}>
                  <Input
                    label="Roll Number"
                    required
                    placeholder="2023CS0101"
                    value={studentForm.rollNumber}
                    onChange={(e) => setStudentForm({ ...studentForm, rollNumber: e.target.value })}
                  />
                  <Input
                    label="Phone"
                    required
                    placeholder="+91..."
                    value={studentForm.phone}
                    onChange={(e) => setStudentForm({ ...studentForm, phone: e.target.value })}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '0.75rem' }}>
                  <div>
                    <label className="input-label">Branch</label>
                    <select
                      className="input-base"
                      value={studentForm.branch}
                      onChange={(e) => setStudentForm({ ...studentForm, branch: e.target.value })}
                    >
                      <option value="Computer Science and Engineering">Computer Science (CSE)</option>
                      <option value="Information Technology">Information Tech (IT)</option>
                      <option value="Electronics and Communication">Electronics (ECE)</option>
                      <option value="Electrical Engineering">Electrical (EEE)</option>
                      <option value="Mechanical Engineering">Mechanical</option>
                      <option value="Civil Engineering">Civil</option>
                    </select>
                  </div>

                  <Input
                    label="CGPA (0-10)"
                    type="number"
                    step="0.01"
                    min="0"
                    max="10"
                    required
                    value={studentForm.cgpa}
                    onChange={(e) => setStudentForm({ ...studentForm, cgpa: e.target.value })}
                  />
                </div>
              </>
            )}

            {/* Recruiter Registration Fields */}
            {mode === 'recruiter_reg' && (
              <>
                <Input
                  label="Company Name"
                  required
                  placeholder="e.g. Stripe, Google, Linear"
                  icon={Building2}
                  value={recruiterForm.companyName}
                  onChange={(e) => setRecruiterForm({ ...recruiterForm, companyName: e.target.value })}
                />

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                  <Input
                    label="Designation"
                    required
                    placeholder="Lead Recruiter"
                    value={recruiterForm.designation}
                    onChange={(e) => setRecruiterForm({ ...recruiterForm, designation: e.target.value })}
                  />
                  <Input
                    label="Contact Phone"
                    required
                    placeholder="+91..."
                    icon={Phone}
                    value={recruiterForm.phone}
                    onChange={(e) => setRecruiterForm({ ...recruiterForm, phone: e.target.value })}
                  />
                </div>
              </>
            )}

            <Button
              type="submit"
              variant="primary"
              size="lg"
              loading={loading}
              style={{ marginTop: '0.5rem', width: '100%' }}
            >
              {mode === 'login'
                ? 'Sign In to PlacementOS'
                : mode === 'student_reg'
                ? 'Complete Candidate Registration'
                : 'Register Corporate Account'}
            </Button>
          </form>
        </Card>

        {/* Subtle Footer Note */}
        <div style={{ textAlign: 'center', marginTop: '1.25rem', fontSize: '0.75rem', color: 'var(--text-subtle)' }}>
          PlacementOS • Institutional Placement Operating System • v1.0
        </div>
      </div>
    </div>
  );
}
