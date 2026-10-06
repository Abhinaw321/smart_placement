// Base URL from environment variable, falling back to http://localhost:8080 in dev
const RAW_BASE_URL = import.meta.env.VITE_API_BASE_URL
  ? import.meta.env.VITE_API_BASE_URL.replace(/\/+$/, '')
  : (import.meta.env.DEV ? 'http://localhost:8080' : '');

const API_BASE = `${RAW_BASE_URL}/api/v1`;

async function request(endpoint, options = {}) {
  const token = localStorage.getItem('spms_token');
  const headers = {
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    localStorage.removeItem('spms_token');
    localStorage.removeItem('spms_user');
    window.dispatchEvent(new Event('auth:unauthorized'));
  }

  // Handle binary/CSV downloads
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('text/csv')) {
    if (!response.ok) throw new Error('Failed to download CSV');
    return await response.blob();
  }

  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.message || 'An unexpected error occurred');
  }

  return data.data;
}

export const authApi = {
  login: (credentials) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    }),
  registerStudent: (payload) =>
    request('/auth/student/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  registerRecruiter: (payload) =>
    request('/auth/recruiter/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  getCurrentUser: () => request('/auth/me'),
};

export const studentApi = {
  getProfile: () => request('/students/me'),
  updatePersonal: (payload) =>
    request('/students/me', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  updateAcademic: (payload) =>
    request('/students/me/academic', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  uploadResume: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return request('/students/me/resume', {
      method: 'POST',
      body: formData,
    });
  },
  downloadResume: () => `${API_BASE}/students/me/resume`,
  addSkill: (payload) =>
    request('/students/me/skills', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  deleteSkill: (id) =>
    request(`/students/me/skills/${id}`, {
      method: 'DELETE',
    }),
  searchStudents: (params = '') => request(`/students?${params}`),
  getStudentById: (id) => request(`/students/${id}`),
};

export const companyApi = {
  getAll: (params = '') => request(`/companies?${params}`),
  getById: (id) => request(`/companies/${id}`),
  create: (payload) =>
    request('/companies', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  verify: (id) =>
    request(`/companies/${id}/verify`, {
      method: 'PUT',
    }),
  getMyProfile: () => request('/recruiters/me'),
};

export const jobApi = {
  getAll: (params = '') => request(`/jobs?${params}`),
  getById: (id) => request(`/jobs/${id}`),
  create: (payload) =>
    request('/jobs', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  checkMyEligibility: (jobId) => request(`/jobs/${jobId}/my-eligibility`),
  getEligibleCandidates: (jobId) => request(`/jobs/${jobId}/eligible-candidates`),
};

export const applicationApi = {
  apply: (jobId) =>
    request(`/applications/jobs/${jobId}/apply`, {
      method: 'POST',
    }),
  getMyApplications: (params = '') => request(`/applications/my?${params}`),
  getByJob: (jobId, params = '') => request(`/applications/jobs/${jobId}?${params}`),
  getById: (id) => request(`/applications/${id}`),
  updateStatus: (id, payload) =>
    request(`/applications/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  batchUpdateStatus: (payload) =>
    request('/applications/batch-status', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  withdraw: (id) =>
    request(`/applications/${id}/withdraw`, {
      method: 'DELETE',
    }),
};

export const interviewApi = {
  schedule: (payload) =>
    request('/interviews/schedule', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  submitResult: (id, payload) =>
    request(`/interviews/${id}/result`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  getMyInterviews: (params = '') => request(`/interviews/my?${params}`),
  getByApplication: (appId) => request(`/interviews/application/${appId}`),
  getByJob: (jobId, params = '') => request(`/interviews/job/${jobId}?${params}`),
  cancel: (id, reason) =>
    request(`/interviews/${id}/cancel?reason=${encodeURIComponent(reason || '')}`, {
      method: 'PUT',
    }),
};

export const offerApi = {
  issue: (payload) =>
    request('/offers/issue', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  respond: (id, payload) =>
    request(`/offers/${id}/respond`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  revoke: (id, reason) =>
    request(`/offers/${id}/revoke?reason=${encodeURIComponent(reason || '')}`, {
      method: 'PUT',
    }),
  getMyOffers: (params = '') => request(`/offers/my?${params}`),
  getByJob: (jobId, params = '') => request(`/offers/job/${jobId}?${params}`),
  getById: (id) => request(`/offers/${id}`),
};

export const notificationApi = {
  getAll: (params = '') => request(`/notifications?${params}`),
  getUnreadCount: () => request('/notifications/unread-count'),
  markAsRead: (id) =>
    request(`/notifications/${id}/read`, {
      method: 'PUT',
    }),
  markAllAsRead: () =>
    request('/notifications/read-all', {
      method: 'PUT',
    }),
};

export const analyticsApi = {
  getStudentDashboard: () => request('/analytics/student-dashboard'),
  getRecruiterDashboard: () => request('/analytics/recruiter-dashboard'),
  getTpoDashboard: () => request('/analytics/tpo-dashboard'),
  exportPlacementsCsv: () => request('/analytics/export/placements'),
  getAuditLogs: (params = '') => request(`/audit-logs?${params}`),
};
