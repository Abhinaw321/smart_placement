import React from 'react';

const DEFAULT_STEPS = ['Applied', 'Shortlisted', 'Test', 'Interview', 'Offer'];

export default function Stepper({
  steps = DEFAULT_STEPS,
  currentStep = 'Applied',
  status = 'PENDING',
  className = '',
  style = {},
}) {
  // Normalize current step index
  const normalizedCurrent = (currentStep || '').toLowerCase();
  let activeIndex = steps.findIndex(
    (s) => s.toLowerCase() === normalizedCurrent || normalizedCurrent.includes(s.toLowerCase())
  );
  if (activeIndex === -1) {
    if (normalizedCurrent.includes('offer') || normalizedCurrent.includes('accept') || status === 'OFFER_MADE') {
      activeIndex = 4;
    } else if (
      normalizedCurrent.includes('interview') ||
      normalizedCurrent.includes('technical') ||
      normalizedCurrent.includes('round') ||
      status === 'TECHNICAL_INTERVIEW' ||
      status === 'INTERVIEW_SCHEDULED'
    ) {
      activeIndex = 3;
    } else if (normalizedCurrent.includes('test') || normalizedCurrent.includes('assessment')) {
      activeIndex = 2;
    } else if (normalizedCurrent.includes('shortlist') || status === 'SHORTLISTED') {
      activeIndex = 1;
    } else {
      activeIndex = 0;
    }
  }

  const isRejected = status === 'REJECTED';

  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '0.4rem',
        ...style,
      }}
      className={className}
    >
      {steps.map((step, idx) => {
        const isCompleted = idx < activeIndex;
        const isCurrent = idx === activeIndex;

        let dotColor = 'var(--border)';
        let textColor = 'var(--text-subtle)';

        if (isCurrent) {
          dotColor = isRejected ? 'var(--status-red-fg)' : 'var(--accent)';
          textColor = isRejected ? 'var(--status-red-fg)' : 'var(--text)';
        } else if (isCompleted) {
          dotColor = 'var(--text-muted)';
          textColor = 'var(--text-muted)';
        }

        return (
          <React.Fragment key={step}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
              <div
                style={{
                  width: '6px',
                  height: '6px',
                  borderRadius: '50%',
                  background: dotColor,
                  transition: 'background 0.15s ease',
                }}
              />
              <span
                style={{
                  fontSize: '0.72rem',
                  fontWeight: isCurrent ? 600 : 400,
                  color: textColor,
                }}
              >
                {step}
              </span>
            </div>

            {idx < steps.length - 1 && (
              <div
                style={{
                  width: '14px',
                  height: '1px',
                  background: isCompleted ? 'var(--text-muted)' : 'var(--border)',
                  margin: '0 0.1rem',
                }}
              />
            )}
          </React.Fragment>
        );
      })}
    </div>
  );
}
