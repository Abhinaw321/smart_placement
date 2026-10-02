import React from 'react';
import EmptyState from './EmptyState';
import { Database } from 'lucide-react';

export default function DataTable({
  columns = [],
  data = [],
  loading = false,
  emptyTitle = 'No records found',
  emptyDescription = 'There are no items to display in this table.',
  emptyIcon = Database,
  selectable = false,
  selectedIds = [],
  onSelectRow,
  onSelectAll,
  keyField = 'id',
  className = '',
  style = {},
}) {
  const allSelected = data.length > 0 && data.every((row) => selectedIds.includes(row[keyField]));
  const isIndeterminate = selectedIds.length > 0 && !allSelected;

  return (
    <div
      style={{
        width: '100%',
        background: 'var(--surface)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-xl)',
        overflow: 'hidden',
        ...style,
      }}
      className={className}
    >
      <div style={{ overflowX: 'auto', width: '100%', maxHeight: '640px' }}>
        <table
          style={{
            width: '100%',
            borderCollapse: 'collapse',
            textAlign: 'left',
            fontFamily: 'var(--font-body)',
          }}
        >
          {/* Sticky Header */}
          <thead>
            <tr
              style={{
                position: 'sticky',
                top: 0,
                background: 'var(--surface)',
                borderBottom: '1px solid var(--border)',
                zIndex: 10,
              }}
            >
              {selectable && (
                <th
                  style={{
                    width: '44px',
                    padding: '0.75rem 1rem',
                    verticalAlign: 'middle',
                  }}
                >
                  <input
                    type="checkbox"
                    checked={allSelected}
                    ref={(el) => {
                      if (el) el.indeterminate = isIndeterminate;
                    }}
                    onChange={() => onSelectAll && onSelectAll(!allSelected)}
                    style={{
                      cursor: 'pointer',
                      accentColor: 'var(--accent)',
                      width: '15px',
                      height: '15px',
                    }}
                  />
                </th>
              )}

              {columns.map((col) => {
                const isRight = col.align === 'right' || col.key === 'actions';
                return (
                  <th
                    key={col.key || col.header}
                    style={{
                      width: col.width || 'auto',
                      padding: '0.75rem 1rem',
                      fontSize: '0.72rem',
                      fontWeight: 600,
                      color: 'var(--text-muted)',
                      textTransform: 'uppercase',
                      letterSpacing: '0.05em',
                      textAlign: isRight ? 'right' : 'left',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    {col.header}
                  </th>
                );
              })}
            </tr>
          </thead>

          {/* Table Body */}
          <tbody>
            {loading ? (
              // Loading Skeleton
              Array.from({ length: 4 }).map((_, rIdx) => (
                <tr key={rIdx} style={{ borderBottom: '1px solid var(--border)' }}>
                  {selectable && (
                    <td style={{ padding: '0.85rem 1rem' }}>
                      <div
                        style={{
                          width: '16px',
                          height: '16px',
                          borderRadius: '4px',
                          background: 'var(--surface-elevated)',
                        }}
                      />
                    </td>
                  )}
                  {columns.map((col, cIdx) => (
                    <td key={cIdx} style={{ padding: '0.85rem 1rem' }}>
                      <div
                        style={{
                          width: col.width ? '70%' : '80px',
                          height: '14px',
                          borderRadius: '4px',
                          background: 'var(--surface-elevated)',
                          animation: 'pulse 1.5s ease-in-out infinite',
                        }}
                      />
                    </td>
                  ))}
                </tr>
              ))
            ) : data.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length + (selectable ? 1 : 0)}
                  style={{ padding: '3rem 1.5rem', textAlign: 'center' }}
                >
                  <EmptyState
                    icon={emptyIcon}
                    title={emptyTitle}
                    description={emptyDescription}
                    style={{ background: 'transparent', border: 'none', padding: 0 }}
                  />
                </td>
              </tr>
            ) : (
              data.map((row) => {
                const rowId = row[keyField];
                const isSelected = selectedIds.includes(rowId);

                return (
                  <tr
                    key={rowId}
                    style={{
                      borderBottom: '1px solid var(--border)',
                      background: isSelected ? 'rgba(198, 255, 61, 0.04)' : 'transparent',
                      transition: 'background-color 0.12s ease',
                      cursor: 'default',
                    }}
                    onMouseEnter={(e) => {
                      if (!isSelected) e.currentTarget.style.backgroundColor = 'var(--surface-hover)';
                    }}
                    onMouseLeave={(e) => {
                      if (!isSelected) e.currentTarget.style.backgroundColor = 'transparent';
                    }}
                  >
                    {selectable && (
                      <td style={{ padding: '0.75rem 1rem', verticalAlign: 'middle' }}>
                        <input
                          type="checkbox"
                          checked={isSelected}
                          onChange={() => onSelectRow && onSelectRow(rowId)}
                          style={{
                            cursor: 'pointer',
                            accentColor: 'var(--accent)',
                            width: '15px',
                            height: '15px',
                          }}
                        />
                      </td>
                    )}

                    {columns.map((col) => {
                      const isRight = col.align === 'right' || col.key === 'actions';
                      return (
                        <td
                          key={col.key}
                          style={{
                            padding: '0.75rem 1rem',
                            fontSize: '0.84rem',
                            color: 'var(--text)',
                            textAlign: isRight ? 'right' : 'left',
                            verticalAlign: 'middle',
                            whiteSpace: col.wrap ? 'normal' : 'nowrap',
                          }}
                        >
                          {col.render ? col.render(row[col.key], row) : row[col.key]}
                        </td>
                      );
                    })}
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
