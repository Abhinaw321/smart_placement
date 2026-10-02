import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('spms_token');
    const savedUser = localStorage.getItem('spms_user');

    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('spms_token');
        localStorage.removeItem('spms_user');
      }
    }
    setLoading(false);

    const handleUnauthorized = () => {
      logout();
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  const login = async (email, password) => {
    const res = await authApi.login({ email, password });
    const authToken = res.accessToken;
    const authUser = {
      id: res.userId || res.user?.id,
      email: res.email || res.user?.email,
      role: res.role || res.user?.role,
      name: (res.email || res.user?.email || 'User').split('@')[0],
    };

    localStorage.setItem('spms_token', authToken);
    localStorage.setItem('spms_user', JSON.stringify(authUser));

    setToken(authToken);
    setUser(authUser);
    return authUser;
  };

  const logout = () => {
    localStorage.removeItem('spms_token');
    localStorage.removeItem('spms_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        loading,
        login,
        logout,
        isStudent: user?.role === 'ROLE_STUDENT',
        isRecruiter: user?.role === 'ROLE_RECRUITER',
        isAdmin: user?.role === 'ROLE_TPO_ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
