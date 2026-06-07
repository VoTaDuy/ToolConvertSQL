import React, { useState, useEffect } from "react";
import axios from "axios";
import uthLogo from "./assets/uthlogo.png";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
const DEFAULT_METHOD = "aiSchema";

const getColumnLabel = (index) => {
  let label = "";
  let cursor = index + 1;

  while (cursor > 0) {
    const remainder = (cursor - 1) % 26;
    label = String.fromCharCode(65 + remainder) + label;
    cursor = Math.floor((cursor - 1) / 26);
  }

  return label;
};

const formatCellValue = (value) => {
  if (value === null || value === undefined) {
    return "";
  }

  if (typeof value === "object") {
    return JSON.stringify(value);
  }

  return String(value);
};

function ExcelResultTable({ columns, rows }) {
  return (
    <div className="excel-shell">
      <table className="excel-table">
        <thead>
          <tr className="excel-column-row">
            <th className="excel-corner" aria-label="row selector" />
            {columns.map((column, index) => (
              <th key={`${column}-${index}-letter`} className="excel-column-letter">
                {getColumnLabel(index)}
              </th>
            ))}
          </tr>
          <tr className="excel-field-row">
            <th className="excel-row-number">1</th>
            {columns.map((column, index) => (
              <th key={`${column}-${index}`} className="excel-field-cell" title={column}>
                {column}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, rowIndex) => (
            <tr key={`row-${rowIndex}`}>
              <th className="excel-row-number">{rowIndex + 2}</th>
              {columns.map((column, cellIndex) => {
                const rawValue = Array.isArray(row) ? row[cellIndex] : row?.[column];
                const cellValue = formatCellValue(rawValue);

                return (
                  <td
                    key={`${rowIndex}-${column}-${cellIndex}`}
                    className="excel-cell"
                    title={cellValue}
                  >
                    {cellValue}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function ResultPanel({ title, data, loading, execution, onRetry, onRate }) {
  const [rated, setRated] = useState(false);

  useEffect(() => {
    setRated(false);
  }, [data]);

  let rows = [];
  let columns = [];
  if (Array.isArray(data?.result) && data.result.length > 0) {
      columns = Object.keys(data.result[0]);
      rows = data.result.map(item => columns.map(col => item[col]));
  } else if (data?.result?.rows) {
      rows = data.result.rows;
      columns = data.result.columns || [];
  }

  const isRejected = typeof data?.sql === 'string' && /^Rejected:/i.test(data.sql);
  const rejectedText = isRejected ? data.sql.replace(/^Rejected:\s*/i, '') : '';
  const isExecutionError = typeof data?.sql === 'string' && /^Execution error:/i.test(data.sql);
  const executionErrorText = isExecutionError ? data.sql.replace(/^Execution error:\s*/i, '') : '';

  return (
    <article className="panel method-panel" style={{ display: 'flex', flexDirection: 'column', gap: '1rem', padding: '20px' }}>
      <div className="panel-head" style={{ marginBottom: '10px' }}>
        <h2 style={{ margin: 0, fontSize: '1.25rem', color: '#1e293b' }}>{title}</h2>
      </div>
      
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem 0' }}>
          <p>Đang xử lý...</p>
        </div>
      ) : data ? (
        <>
          {data.error ? (
            <div style={{ padding: '15px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px', border: '1px solid #fca5a5' }}>
              <strong>Lỗi:</strong> {data.error}
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', flexGrow: 1 }}>
              {/* KHUNG DỊCH SANG SQL */}
              <div className="sql-section" style={{ 
                  border: '1px solid #cbd5e1', 
                  borderRadius: '8px', 
                  overflow: 'hidden'
                }}>
                <div style={{ 
                  backgroundColor: '#f1f5f9', 
                  padding: '10px 15px', 
                  borderBottom: '1px solid #cbd5e1',
                  fontWeight: '600',
                  color: '#334155',
                  fontSize: '0.9rem',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  Khung Dịch Câu Lệnh Sang SQL
                </div>
                <div style={{ padding: '15px', backgroundColor: isRejected ? '#fef3c7' : '#f8fafc' }}>
                  {isRejected ? (
                    <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
                      <div style={{ fontSize: '1.35rem', lineHeight: 1.1 }}>⚠️</div>
                      <div>
                        <div style={{ fontWeight: '700', marginBottom: '8px', color: '#92400e' }}>Yêu cầu không thể dịch</div>
                        <div style={{ color: '#92400e', marginBottom: '10px', whiteSpace: 'pre-wrap' }}>{rejectedText}</div>
                        <div style={{ color: '#78350f', fontSize: '0.95rem' }}>
                          Vui lòng thử câu hỏi khác liên quan tới cơ sở dữ liệu phim.
                        </div>
                      </div>
                    </div>
                  ) : isExecutionError ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
                      <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
                        <div style={{ fontSize: '1.35rem', lineHeight: 1.1 }}>❌</div>
                        <div>
                          <div style={{ fontWeight: '700', marginBottom: '8px', color: '#7f1d1d', fontSize: '1.05rem' }}>Query Execution Failed</div>
                          <div style={{ color: '#1f2937', marginBottom: '8px' }}>
                            The generated SQL could not be executed due to invalid syntax or unsupported aggregation logic.
                          </div>
                          <div style={{ color: '#475569', whiteSpace: 'pre-wrap' }}>{executionErrorText}</div>
                        </div>
                      </div>

                      <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                        <button
                          onClick={onRetry}
                          style={{ padding: '10px 16px', borderRadius: '8px', border: 'none', backgroundColor: '#2563eb', color: '#ffffff', cursor: 'pointer' }}
                        >
                          Retry
                        </button>
                      </div>
                    </div>
                  ) : (
                    <pre style={{ 
                      whiteSpace: 'pre-wrap', 
                      wordWrap: 'break-word', 
                      margin: 0, 
                      maxHeight: '200px', 
                      overflowY: 'auto',
                      fontFamily: '"Fira Code", monospace',
                      fontSize: '0.85rem',
                      color: '#0f172a'
                    }}>
                      {data.sql || "-- Không có dữ liệu SQL"}
                    </pre>
                  )}
                </div>
              </div>
              
              {/* KHUNG KẾT QUẢ TỪ DATABASE */}
              <div className="result-section" style={{ 
                  border: '1px solid #e2e8f0', 
                  borderRadius: '8px', 
                  overflow: 'hidden',
                  display: 'flex',
                  flexDirection: 'column',
                  flexGrow: 1
                }}>
                <div style={{ 
                  display: 'flex', 
                  justifyContent: 'space-between', 
                  alignItems: 'center', 
                  backgroundColor: '#ffffff',
                  padding: '10px 15px', 
                  borderBottom: '1px solid #e2e8f0'
                }}>
                  <span style={{ 
                    fontWeight: '600',
                    color: '#0f172a',
                    fontSize: '0.9rem',
                    textTransform: 'uppercase',
                    letterSpacing: '0.5px'
                  }}>
                    Kết Quả Trả Ra Từ Database
                  </span>
                  <span className="badge neutral" style={{ backgroundColor: '#e2e8f0', color: '#475569', padding: '2px 8px', borderRadius: '12px', fontSize: '0.75rem', fontWeight: 'bold' }}>
                    {rows.length} dòng
                  </span>
                </div>
                
                <div style={{ backgroundColor: '#ffffff', padding: '0', flexGrow: 1 }}>
                  {Array.isArray(columns) && columns.length > 0 ? (
                    <ExcelResultTable columns={columns} rows={rows} />
                  ) : (
                    <div style={{ padding: '20px', color: '#64748b', fontSize: '0.9rem' }}>
                      {data.result ? <pre style={{margin: 0, fontSize: '0.85rem'}}>{JSON.stringify(data.result, null, 2)}</pre> : "Không có dữ liệu trả về."}
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </>
      ) : (
        <div className="empty-state" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem 0', color: '#94a3b8' }}>
          <p style={{ margin: 0 }}>Chưa có dữ liệu.</p>
        </div>
      )}
    </article>
  );
}

function EvaluationPanel({ evaluations, evaluationResult, evaluationLoading, evaluationError, runEvaluation }) {

  return (
    <article className="panel method-panel" style={{ padding: '20px' }}>
      <div className="panel-head" style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: 0, fontSize: '1.6rem', color: '#111827', fontWeight: 900, letterSpacing: '0.04em', backgroundColor: '#e2e8f0', padding: '12px 16px', borderRadius: '12px', display: 'inline-block', boxShadow: '0 8px 24px rgba(15, 23, 42, 0.08)' }}>Báo Cáo Đánh Giá Thực Tế</h2>
        <p style={{ margin: '10px 0 0 0', color: '#475569', fontSize: '1rem' }}>
        </p>
      </div>

      <div style={{ padding: '15px 20px', backgroundColor: '#f8fafc', borderRadius: '8px', marginBottom: '20px', border: '1px solid #e2e8f0' }}>
        <div style={{ marginTop: '10px', display: 'grid', gap: '10px' }}>
          <button onClick={() => runEvaluation('film_nl2sql_dataset.json')} style={{ padding: '10px 15px', borderRadius: '8px', border: '1px solid #2563eb', backgroundColor: '#ffffff', color: '#2563eb', cursor: 'pointer', textAlign: 'left' }}>
            Bộ dataset 1
          </button>
          <button onClick={() => runEvaluation('movie_eval_dataset.json')} style={{ padding: '10px 15px', borderRadius: '8px', border: '1px solid #2563eb', backgroundColor: '#ffffff', color: '#2563eb', cursor: 'pointer', textAlign: 'left' }}>
            Bộ dataset 2
          </button>
        </div>

        {evaluationLoading && (
          <div style={{ marginTop: '15px', color: '#0f172a' }}>Đang chạy đánh giá...</div>
        )}
        {evaluationError && (
          <div style={{ marginTop: '15px', padding: '12px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px' }}>
            {evaluationError}
          </div>
        )}
        {evaluationResult && (
          <div style={{ marginTop: '15px', display: 'grid', gap: '15px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: '12px' }}>
              <div style={{ padding: '15px', borderRadius: '10px', backgroundColor: '#ffffff', border: '1px solid #cbd5e1' }}>
                <div style={{ color: '#334155', fontWeight: 700, marginBottom: '4px' }}>VA</div>
                <div style={{ color: '#64748b', fontSize: '0.85rem', marginBottom: '10px' }}>Validation Accuracy</div>
                <div style={{ color: '#0f172a', fontSize: '1.1rem' }}>{evaluationResult.VA ?? evaluationResult.va ?? '-'}</div>
              </div>
              <div style={{ padding: '15px', borderRadius: '10px', backgroundColor: '#ffffff', border: '1px solid #cbd5e1' }}>
                <div style={{ color: '#334155', fontWeight: 700, marginBottom: '4px' }}>EA</div>
                <div style={{ color: '#64748b', fontSize: '0.85rem', marginBottom: '10px' }}>Execution Accuracy</div>
                <div style={{ color: '#0f172a', fontSize: '1.1rem' }}>{evaluationResult.executionAccuracy ?? evaluationResult.EA ?? '-'}</div>
              </div>
              <div style={{ padding: '15px', borderRadius: '10px', backgroundColor: '#ffffff', border: '1px solid #cbd5e1' }}>
                <div style={{ color: '#334155', fontWeight: 700, marginBottom: '4px' }}>EC</div>
                <div style={{ color: '#64748b', fontSize: '0.85rem', marginBottom: '10px' }}>Execution Correct</div>
                <div style={{ color: '#0f172a', fontSize: '1.1rem' }}>{evaluationResult.executionCorrect ?? evaluationResult.EC ?? '-'}</div>
              </div>
              <div style={{ padding: '15px', borderRadius: '10px', backgroundColor: '#ffffff', border: '1px solid #cbd5e1' }}>
                <div style={{ color: '#334155', fontWeight: 700, marginBottom: '4px' }}>FC</div>
                <div style={{ color: '#64748b', fontSize: '0.85rem', marginBottom: '10px' }}>Failed Cases</div>
                <div style={{ color: '#0f172a', fontSize: '1.1rem' }}>{Array.isArray(evaluationResult.failedCases) ? evaluationResult.failedCases.length : '-'}</div>
              </div>
            </div>

            {Array.isArray(evaluationResult.failedCases) && evaluationResult.failedCases.length > 0 && (
              <div style={{ padding: '15px', backgroundColor: '#ffffff', borderRadius: '10px', border: '1px solid #cbd5e1' }}>
                <div style={{ fontWeight: 700, marginBottom: '12px', color: '#334155' }}>Failed cases</div>
                <div style={{ display: 'grid', gap: '12px' }}>
                  {evaluationResult.failedCases.map((item, index) => (
                    <div key={item.id ?? index} style={{ padding: '12px', backgroundColor: '#f8fafc', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
                      <div style={{ fontWeight: 700, color: '#0f172a', marginBottom: '8px' }}>failCase {index + 1}</div>
                      <div style={{ marginBottom: '6px', color: '#475569' }}><strong>question:</strong> {item.question}</div>
                      <div style={{ marginBottom: '6px', color: '#475569' }}><strong>predictedSql:</strong> <code style={{ display: 'block', whiteSpace: 'pre-wrap', color: '#0f172a' }}>{item.predictedSql}</code></div>
                      <div style={{ marginBottom: '6px', color: '#475569' }}><strong>groundTruthSql:</strong> <code style={{ display: 'block', whiteSpace: 'pre-wrap', color: '#0f172a' }}>{item.groundTruthSql}</code></div>
                      <div style={{ color: '#7c3aed' }}><strong>error:</strong> {item.error}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {evaluations.length > 0 ? (
        <div>
          <h3 style={{ fontSize: '1.1rem', color: '#334155', marginBottom: '10px' }}>Lịch sử đánh giá chi tiết ({evaluations.length} lượt)</h3>
          <div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '8px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
              <thead style={{ backgroundColor: '#f8fafc', position: 'sticky', top: 0 }}>
                <tr>
                  <th style={{ padding: '10px 15px', borderBottom: '1px solid #e2e8f0', color: '#334155' }}>Câu hỏi</th>
                  <th style={{ padding: '10px 15px', borderBottom: '1px solid #e2e8f0', color: '#334155' }}>Đánh giá</th>
                </tr>
              </thead>
              <tbody>
                {evaluations.slice().reverse().map((e, i) => (
                  <tr key={i} style={{ borderBottom: '1px solid #f1f5f9' }}>
                    <td style={{ padding: '10px 15px', maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{e.question}</td>
                    <td style={{ padding: '10px 15px' }}>
                      {e.rating === 'correct' ? '✅ Đúng' : e.rating === 'partial' ? '⚠️ Tạm ổn' : '❌ Sai'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : null}
    </article>
  );
}

export default function App() {
  const [activeTab, setActiveTab] = useState("query"); // "query" | "eval"
  const [q, setQ] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [resultData, setResultData] = useState(null);
  const [evaluationResult, setEvaluationResult] = useState(null);
  const [evaluationLoading, setEvaluationLoading] = useState(false);
  const [evaluationError, setEvaluationError] = useState("");
  
  // Track latency and question for the currently viewed result
  const [currentExecution, setCurrentExecution] = useState(null);
  // Store all user ratings
  const [evaluations, setEvaluations] = useState([]);

  const ask = async (question = q) => {
    const nextQuestion = question.trim();
    if (!nextQuestion) {
      setError("Hãy nhập câu hỏi trước khi gửi.");
      return;
    }

    setLoading(true);
    setError("");
    setResultData(null);
    setCurrentExecution(null);

    const startTime = Date.now();

    try {
      const res = await axios.post(`${API_BASE}/generate/ask?method=${DEFAULT_METHOD}`, {
        question: nextQuestion
      });
      const latency = Date.now() - startTime;
      
      setResultData({
        sql: res.data.generatedSql || res.data.sql,
        result: res.data.result,
        error: null
      });
      setCurrentExecution({
        question: nextQuestion,
        latency: latency
      });
    } catch (err) {
      const latency = Date.now() - startTime;
      
      setResultData({
        sql: null,
        result: null,
        error: err.response?.data?.detail || "Lỗi khi gọi API"
      });
      setCurrentExecution({
        question: nextQuestion,
        latency: latency
      });
    } finally {
      setQ(nextQuestion);
      setLoading(false);
    }
  };

  const runEvaluation = async (file) => {
    setEvaluationLoading(true);
    setEvaluationError("");
    setEvaluationResult(null);

    try {
      const res = await axios.post(`${API_BASE}/evaluation/run-from-file?file=${file}`);
      setEvaluationResult(res.data);
    } catch (err) {
      setEvaluationError(err.response?.data?.detail || err.message || "Lỗi khi chạy đánh giá");
    } finally {
      setEvaluationLoading(false);
    }
  };

  const handleRate = (executionData, ratingValue) => {
    setEvaluations(prev => [...prev, { ...executionData, rating: ratingValue }]);
  };

  return (
    <div className="page-shell">
      <div className="ambient ambient-left" />
      <div className="ambient ambient-right" />

      <main className="app-card" style={{ maxWidth: '1000px', width: '95%' }}>
        <section className="hero" style={{ paddingBottom: '0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '18px', marginBottom: '20px', flexWrap: 'wrap' }}>
            <img
              src={uthLogo}
              alt="UTH Logo"
              style={{ width: '86px', height: '86px', objectFit: 'contain', borderRadius: '18px', backgroundColor: 'rgba(255,255,255,0.12)', padding: '10px' }}
            />
            <div style={{ minWidth: 0 }}>
              <h1 style={{ margin: 0, color: '#e2e8f0', letterSpacing: '-0.04em' }}>Text to SQL</h1>
              <p style={{ marginTop: '10px', color: 'rgba(226, 232, 240, 0.92)', fontSize: '1rem', maxWidth: '680px' }}>*Ask a question, get SQL instantly*</p>
            </div>
          </div>
        </section>

        {/* TAB NAVIGATION */}
        <div style={{ display: 'flex', gap: '15px', borderBottom: '2px solid #e2e8f0', marginBottom: '20px' }}>
          <button 
            onClick={() => setActiveTab("query")}
            style={{
              background: 'none', border: 'none', 
              padding: '10px 20px', fontSize: '1rem', fontWeight: '600', cursor: 'pointer',
              color: activeTab === "query" ? '#2563eb' : '#64748b',
              borderBottom: activeTab === "query" ? '3px solid #2563eb' : '3px solid transparent',
              marginBottom: '-2px'
            }}
          >
            Query Conversion
          </button>
          <button 
            onClick={() => setActiveTab("eval")}
            style={{
              background: 'none', border: 'none', 
              padding: '10px 20px', fontSize: '1rem', fontWeight: '600', cursor: 'pointer',
              color: activeTab === "eval" ? '#2563eb' : '#64748b',
              borderBottom: activeTab === "eval" ? '3px solid #2563eb' : '3px solid transparent',
              marginBottom: '-2px'
            }}
          >
            Evaluation
          </button>
        </div>

        {activeTab === "query" && (
          <>
            <section className="panel composer">
              <label htmlFor="question" className="panel-title">
                Đặt câu hỏi
              </label>

              <div className="composer-row" style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                <input
                  id="question"
                  value={q}
                  onChange={(e) => setQ(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      ask();
                    }
                  }}
                  placeholder="Ví dụ: Top 5 phim tình cảm có điểm cao nhất?"
                  style={{ flexGrow: 1, minWidth: '250px' }}
                />

                <button onClick={() => ask()} disabled={loading} style={{ minWidth: '120px' }}>
                  {loading ? "Đang xử lý..." : "Gửi"}
                </button>
              </div>

              {error ? <p className="status error">{error}</p> : null}
            </section>

            <section style={{ marginTop: '30px' }}>
              {(resultData || loading) && (
                <ResultPanel 
                  title="Kết quả"
                  data={resultData} 
                  loading={loading}
                  execution={currentExecution}
                  onRetry={() => ask(currentExecution?.question)}
                  onRate={handleRate}
                />
              )}
            </section>
          </>
        )}

        {activeTab === "eval" && (
          <EvaluationPanel
            evaluations={evaluations}
            evaluationResult={evaluationResult}
            evaluationLoading={evaluationLoading}
            evaluationError={evaluationError}
            runEvaluation={runEvaluation}
          />
        )}
      </main>
    </div>
  );
}
