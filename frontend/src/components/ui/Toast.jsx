import React, { createContext, useContext, useState } from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const addToast = (message, type = 'info', duration = 3500) => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id);
      }, duration);
    }
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  return (
    <ToastContext.Provider value={{ toast: addToast }}>
      {children}
      {/* Toast container */}
      <div
        style={{
          position: 'fixed',
          bottom: '1.5rem',
          right: '1.5rem',
          display: 'flex',
          flexDirection: 'column',
          gap: '0.5rem',
          zIndex: 9999,
          pointerEvents: 'none',
        }}
      >
        {toasts.map((t) => {
          let Icon = Info;
          let color = 'var(--text)';
          let borderColor = 'var(--border)';

          if (t.type === 'success') {
            Icon = CheckCircle2;
            color = 'var(--status-green-fg)';
            borderColor = 'rgba(74, 222, 128, 0.3)';
          } else if (t.type === 'error') {
            Icon = AlertCircle;
            color = 'var(--status-red-fg)';
            borderColor = 'rgba(248, 113, 113, 0.3)';
          }

          return (
            <div
              key={t.id}
              className="animate-fade-in"
              style={{
                pointerEvents: 'auto',
                display: 'flex',
                alignItems: 'center',
                gap: '0.65rem',
                backgroundColor: 'var(--surface-elevated)',
                border: `1px solid ${borderColor}`,
                borderRadius: 'var(--radius-md)',
                padding: '0.65rem 0.95rem',
                color: 'var(--text)',
                fontSize: '0.84rem',
                fontFamily: 'var(--font-body)',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.5)',
                minWidth: '240px',
                maxWidth: '400px',
              }}
            >
              <Icon size={16} style={{ color, flexShrink: 0 }} />
              <span style={{ flex: 1 }}>{t.message}</span>
              <button
                onClick={() => removeToast(t.id)}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--text-muted)',
                  cursor: 'pointer',
                  padding: '2px',
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <X size={14} />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    return {
      toast: (msg) => console.log(msg),
    };
  }
  return context;
}
