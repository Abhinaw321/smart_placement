import React from 'react';
import { Check } from 'lucide-react';

export default function Stepper({
  steps = [],
  currentStepIndex = 0,
  style = {},
  ...props
}) {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        width: '100%',
        gap: '0.5rem',
        ...style,
      }}
      {...props}
    >
      {steps.map((step, idx) => {
        const isCompleted = idx < currentStepIndex;
        const isCurrent = idx === currentStepIndex;
        const isUpcoming = idx > currentStepIndex;

        return (
          <React.Fragment key={step.id || step.label || idx}>
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.45rem',
                flexShrink: 0,
              }}
            >
              <div
                style={{
                  width: '20px',
                  height: '20px',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.65rem',
                  fontWeight: 700,
                  transition: 'all 0.15s ease',
                  background: isCompleted
                    ? 'var(--accent)'
                    : isCurrent
                    ? 'var(--surface-elevated)'
                    : 'var(--surface)',
                  color: isCompleted
                    ? 'var(--accent-fg)'
                    : isCurrent
                    ? 'var(--accent)'
                    : 'var(--text-subtle)',
                  border: isCurrent
                    ? '1.5px solid var(--accent)'
                    : isCompleted
                    ? 'none'
                    : '1px solid var(--border)',
                }}
              >
                {isCompleted ? <Check size={11} strokeWidth={3} /> : idx + 1}
              </div>

              <span
                style={{
                  fontSize: '0.78rem',
                  fontWeight: isCurrent ? 600 : 500,
                  color: isCurrent
                    ? 'var(--text)'
                    : isCompleted
                    ? 'var(--text-muted)'
                    : 'var(--text-subtle)',
                  letterSpacing: '-0.01em',
                }}
              >
                {step.label}
              </span>
            </div>

            {idx < steps.length - 1 && (
              <div
                style={{
                  flex: 1,
                  height: '1px',
                  background: isCompleted ? 'var(--accent-border)' : 'var(--border)',
                  minWidth: '16px',
                }}
              />
            )}
          </React.Fragment>
        );
      })}
    </div>
  );
}
