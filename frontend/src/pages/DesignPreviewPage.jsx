import React, { useState } from 'react';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import Textarea from '../components/ui/Textarea';
import DatePicker from '../components/ui/DatePicker';
import FormField, { FormSection } from '../components/ui/FormField';
import StatusBadge from '../components/ui/StatusBadge';
import DataTable from '../components/ui/DataTable';
import Card, { CardHeader, CardTitle } from '../components/ui/Card';
import StatCard from '../components/ui/StatCard';
import EmptyState from '../components/ui/EmptyState';
import Stepper from '../components/ui/Stepper';
import Modal from '../components/ui/Modal';
import Tabs from '../components/ui/Tabs';
import { useToast } from '../components/ui/Toast';
import {
  Mail,
  Send,
  Calendar,
  Award,
  Users,
  Building2,
  CheckCircle2,
  Plus,
  ExternalLink,
} from 'lucide-react';

export default function DesignPreviewPage() {
  const { toast } = useToast();
  const [modalOpen, setModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState('tab1');
  const [selectedIds, setSelectedIds] = useState([1]);

  const sampleTableData = [
    {
      id: 1,
      candidate: 'Alex Rivera',
      email: 'alex.rivera@campus.edu',
      branch: 'CSE',
      cgpa: '8.75',
      role: 'Cloud Systems Engineer',
      stage: 'TECHNICAL_INTERVIEW',
      appliedOn: 'Oct 01, 2026',
    },
    {
      id: 2,
      candidate: 'Priya Sharma',
      email: 'priya.sharma@campus.edu',
      branch: 'IT',
      cgpa: '9.20',
      role: 'Full Stack Web Engineer',
      stage: 'OFFER_MADE',
      appliedOn: 'Sep 28, 2026',
    },
    {
      id: 3,
      candidate: 'Marcus Vance',
      email: 'marcus.v@campus.edu',
      branch: 'ECE',
      cgpa: '6.90',
      role: 'Backend Systems Developer',
      stage: 'REJECTED',
      appliedOn: 'Sep 25, 2026',
    },
  ];

  const tableColumns = [
    {
      key: 'candidate',
      header: 'Candidate',
      render: (_, row) => (
        <div>
          <div style={{ fontWeight: 600, color: 'var(--text)' }}>{row.candidate}</div>
          <div style={{ fontSize: '0.74rem', color: 'var(--text-muted)' }}>{row.email}</div>
        </div>
      ),
    },
    { key: 'branch', header: 'Branch' },
    { key: 'cgpa', header: 'CGPA' },
    { key: 'role', header: 'Applied For' },
    {
      key: 'stage',
      header: 'Stage',
      render: (stage) => <StatusBadge status={stage} />,
    },
    { key: 'appliedOn', header: 'Applied On' },
    {
      key: 'actions',
      header: 'Actions',
      align: 'right',
      render: () => (
        <Button variant="secondary" size="sm" onClick={() => setModalOpen(true)}>
          Move stage
        </Button>
      ),
    },
  ];

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', color: 'var(--text)', padding: '2.5rem 1.5rem' }}>
      <div className="max-w-7xl mx-auto" style={{ display: 'flex', flexDirection: 'column', gap: '2.5rem' }}>
        {/* Header */}
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 700, letterSpacing: '-0.03em' }}>
            PlacementOS Design System Preview
          </h1>
          <p style={{ color: 'var(--text-muted)', marginTop: '0.25rem' }}>
            Linear/Raycast strict token specification verification
          </p>
        </div>

        {/* 1. Buttons */}
        <Card>
          <CardHeader>
            <CardTitle>Buttons</CardTitle>
          </CardHeader>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', alignItems: 'center' }}>
            <Button variant="primary" size="md">Primary Button</Button>
            <Button variant="primary" size="sm">Primary Small</Button>
            <Button variant="primary" size="md" icon={Plus}>With Icon</Button>
            <Button variant="primary" size="md" loading>Loading State</Button>
            <Button variant="secondary" size="md">Secondary Button</Button>
            <Button variant="secondary" size="sm">Secondary Small</Button>
            <Button variant="ghost" size="md">Ghost Button</Button>
            <Button variant="danger" size="md">Danger Button</Button>
            <Button variant="primary" size="md" disabled>Disabled State</Button>
          </div>
        </Card>

        {/* 2. Status Badges */}
        <Card>
          <CardHeader>
            <CardTitle>StatusBadges (Enum Mapping)</CardTitle>
          </CardHeader>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
            <StatusBadge status="TECHNICAL_INTERVIEW" />
            <StatusBadge status="HR_INTERVIEW" />
            <StatusBadge status="ONLINE_TEST" />
            <StatusBadge status="SHORTLISTED" />
            <StatusBadge status="SELECTED" />
            <StatusBadge status="OFFER_MADE" />
            <StatusBadge status="REJECTED" />
            <StatusBadge status="APPLIED" />
            <StatusBadge status="ELIGIBLE" />
            <StatusBadge status="NOT_ELIGIBLE" />
          </div>
        </Card>

        {/* 3. Form Controls & FormFields */}
        <Card>
          <CardHeader>
            <CardTitle>Form Controls & FormField</CardTitle>
          </CardHeader>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField label="Full Name" required helperText="Enter your official student name">
              <Input placeholder="Alex Rivera" />
            </FormField>

            <FormField label="Email Address" required>
              <Input icon={Mail} placeholder="alex@campus.edu" />
            </FormField>

            <FormField label="Department / Branch" required>
              <Select>
                <option value="CSE">Computer Science and Engineering</option>
                <option value="IT">Information Technology</option>
                <option value="ECE">Electronics and Communication</option>
              </Select>
            </FormField>

            <FormField label="Drive Date" required>
              <DatePicker />
            </FormField>

            <div style={{ gridColumn: 'span 2' }}>
              <FormField label="Role Description" helperText="Maximum 500 characters">
                <Textarea placeholder="Describe the candidate expectations, stack, and interview process..." />
              </FormField>
            </div>
          </div>
        </Card>

        {/* 4. StatCards */}
        <div>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '0.85rem' }}>StatCards</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Applied" value="24" icon={Send} subtext="Submitted candidates" />
            <StatCard label="Shortlisted" value="12" icon={CheckCircle2} subtext="Advanced to test" />
            <StatCard label="Interviews" value="6" icon={Calendar} subtext="Scheduled this week" />
            <StatCard label="Offers" value="3" icon={Award} subtext="Letters accepted" />
          </div>
        </div>

        {/* 5. Horizontal Stepper */}
        <Card>
          <CardHeader>
            <CardTitle>Horizontal Stepper</CardTitle>
          </CardHeader>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.4rem' }}>Stage: Interview</div>
              <Stepper currentStep="Interview" />
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.4rem' }}>Stage: Shortlisted</div>
              <Stepper currentStep="Shortlisted" />
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.4rem' }}>Stage: Offer Made</div>
              <Stepper currentStep="Offer" />
            </div>
          </div>
        </Card>

        {/* 6. DataTable */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.85rem' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>DataTable</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{selectedIds.length} candidate(s) selected</span>
          </div>
          <DataTable
            columns={tableColumns}
            data={sampleTableData}
            selectable
            selectedIds={selectedIds}
            onSelectRow={(id) => {
              setSelectedIds((prev) =>
                prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
              );
            }}
            onSelectAll={(all) => {
              setSelectedIds(all ? sampleTableData.map((d) => d.id) : []);
            }}
          />
        </div>

        {/* 7. Tabs & Modal Trigger */}
        <Card>
          <CardHeader>
            <CardTitle>Tabs & Modal Dialog</CardTitle>
          </CardHeader>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
            <Tabs
              tabs={[
                { id: 'tab1', label: 'All Candidates', count: 48 },
                { id: 'tab2', label: 'Shortlisted', count: 12 },
                { id: 'tab3', label: 'Interviewed', count: 4 },
              ]}
              activeTab={activeTab}
              onChange={setActiveTab}
            />

            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <Button
                variant="secondary"
                size="md"
                onClick={() => toast('Stage updated successfully!', 'success')}
              >
                Trigger Success Toast
              </Button>
              <Button
                variant="primary"
                size="md"
                onClick={() => setModalOpen(true)}
              >
                Open Demo Modal
              </Button>
            </div>
          </div>
        </Card>

        {/* Modal instance */}
        <Modal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          title="Move candidate stage"
          description="Update Alex Rivera's recruitment pipeline milestone."
          primaryAction={{
            label: 'Save stage',
            onClick: () => {
              setModalOpen(false);
              toast('Candidate moved to Interview stage.', 'success');
            },
          }}
          secondaryAction={{
            label: 'Cancel',
            onClick: () => setModalOpen(false),
          }}
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <FormField label="Target recruitment stage" required>
              <Select defaultValue="TECHNICAL_INTERVIEW">
                <option value="SHORTLISTED">Shortlisted</option>
                <option value="ONLINE_TEST">Online test</option>
                <option value="TECHNICAL_INTERVIEW">Technical interview</option>
                <option value="HR_INTERVIEW">HR interview</option>
                <option value="SELECTED">Selected / Offer</option>
                <option value="REJECTED">Rejected</option>
              </Select>
            </FormField>

            <FormField label="Internal feedback note" helperText="Visible only to recruitment team">
              <Textarea placeholder="Candidate showed deep understanding of SQL transactions and concurrency..." rows={3} />
            </FormField>
          </div>
        </Modal>
      </div>
    </div>
  );
}
