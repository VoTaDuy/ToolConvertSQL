import { useState, useEffect } from "react";
import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8082";

function ResultPanel({ title, data, loading, execution, onRate }) {
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
                <div style={{ padding: '15px', backgroundColor: '#f8fafc' }}>
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
                    <div className="table-wrap" style={{ maxHeight: '350px', overflowY: 'auto', margin: 0 }}>
                      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead style={{ position: 'sticky', top: 0, backgroundColor: '#f8fafc', zIndex: 1 }}>
                          <tr>
                            {columns.map((column) => (
                              <th key={column} style={{ padding: '10px 15px', textAlign: 'left', borderBottom: '2px solid #e2e8f0', fontSize: '0.85rem', color: '#475569', fontWeight: '600' }}>
                                {column}
                              </th>
                            ))}
                          </tr>
                        </thead>
                        <tbody>
                          {rows.map((row, index) => (
                            <tr key={`${index}-${row.join("-")}`} style={{ borderBottom: '1px solid #f1f5f9' }}>
                              {row.map((cell, cellIndex) => (
                                <td key={`${index}-${cellIndex}`} style={{ padding: '10px 15px', fontSize: '0.85rem', color: '#334155' }}>
                                  {String(cell)}
                                </td>
                              ))}
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  ) : (
                    <div style={{ padding: '20px', color: '#64748b', fontSize: '0.9rem' }}>
                      {data.result ? <pre style={{margin: 0, fontSize: '0.85rem'}}>{JSON.stringify(data.result, null, 2)}</pre> : "Không có dữ liệu trả về."}
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
          
          {execution && (
            <div style={{ marginTop: '15px', padding: '15px', backgroundColor: '#f1f5f9', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                <span style={{ fontWeight: '600', color: '#334155' }}>Đánh giá kết quả này:</span>
                <span style={{ fontSize: '0.85rem', color: '#64748b' }}>Thời gian phản hồi: {execution.latency}ms</span>
              </div>
              
              {!rated ? (
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button 
                    onClick={() => { onRate(execution, 'correct'); setRated(true); }}
                    style={{ flex: 1, padding: '8px', backgroundColor: '#dcfce7', color: '#166534', border: '1px solid #bbf7d0', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
                  >
                    ✅ Đúng
                  </button>
                  <button 
                    onClick={() => { onRate(execution, 'partial'); setRated(true); }}
                    style={{ flex: 1, padding: '8px', backgroundColor: '#fef9c3', color: '#854d0e', border: '1px solid #fef08a', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
                  >
                    ⚠️ Tạm ổn
                  </button>
                  <button 
                    onClick={() => { onRate(execution, 'wrong'); setRated(true); }}
                    style={{ flex: 1, padding: '8px', backgroundColor: '#fee2e2', color: '#991b1b', border: '1px solid #fecaca', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
                  >
                    ❌ Sai
                  </button>
                </div>
              ) : (
                <div style={{ padding: '8px', textAlign: 'center', backgroundColor: '#f8fafc', border: '1px dashed #cbd5e1', borderRadius: '6px', color: '#10b981', fontWeight: '600' }}>
                  Đã ghi nhận kết quả đánh giá!
                </div>
              )}
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

function EvaluationPanel({ evaluations, methodNames }) {
  const stats = Object.keys(methodNames).map(key => {
    const evals = evaluations.filter(e => e.method === key);
    const total = evals.length;
    const correct = evals.filter(e => e.rating === 'correct').length;
    const avgLatency = total > 0 ? Math.round(evals.reduce((sum, e) => sum + e.latency, 0) / total) : 0;
    const accuracy = total > 0 ? Math.round((correct / total) * 100) : 0;
    
    let sqlCorrectIcon = "➖";
    if (total > 0) {
       if (accuracy >= 80) sqlCorrectIcon = "✅";
       else if (accuracy >= 50) sqlCorrectIcon = "⚠️";
       else sqlCorrectIcon = "❌";
    }

    let timeDesc = "➖";
    if (total > 0) {
       if (avgLatency < 500) timeDesc = `⚡ nhanh (~${avgLatency}ms)`;
       else if (avgLatency < 1200) timeDesc = `⏳ TB (~${avgLatency}ms)`;
       else timeDesc = `🐢 chậm (~${avgLatency}ms)`;
    }

    let note = "Chưa đủ dữ liệu";
    if (total > 0) {
       if (accuracy >= 80) note = "Kết quả rất tốt, đáng tin cậy.";
       else if (accuracy >= 50) note = "Cần kiểm tra lại schema/prompt.";
       else note = "Tỉ lệ lỗi cao, cần tinh chỉnh.";
    }

    return {
       method: methodNames[key],
       sqlCorrectIcon,
       timeDesc,
       accuracy: total > 0 ? `${accuracy}%` : "0%",
       correctRatio: `${correct}/${total}`,
       note
    };
  });

  return (
    <article className="panel method-panel" style={{ padding: '20px' }}>
      <div className="panel-head" style={{ marginBottom: '20px' }}>
        <h2 style={{ margin: 0, fontSize: '1.25rem', color: '#1e293b' }}>Báo Cáo Đánh Giá Thực Tế</h2>
        <p style={{ margin: '5px 0 0 0', color: '#64748b', fontSize: '0.95rem' }}>
        </p>
      </div>

      <div style={{ border: '1px solid #e2e8f0', borderRadius: '8px', overflow: 'hidden', marginBottom: '20px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead style={{ backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0' }}>
            <tr>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600' }}>Method</th>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600', textAlign: 'center' }}>Đánh giá chung</th>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600' }}>Thời gian (TB)</th>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600' }}>Độ chính xác</th>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600' }}>Tỉ lệ (Đúng/Tổng)</th>
              <th style={{ padding: '12px 15px', color: '#334155', fontWeight: '600' }}>Ghi chú</th>
            </tr>
          </thead>
          <tbody>
            {stats.map((row, idx) => (
              <tr key={idx} style={{ borderBottom: '1px solid #e2e8f0', backgroundColor: idx % 2 === 0 ? '#ffffff' : '#f8fafc' }}>
                <td style={{ padding: '12px 15px', fontWeight: '600', color: '#0f172a' }}>{row.method}</td>
                <td style={{ padding: '12px 15px', fontSize: '1.2rem', textAlign: 'center' }}>{row.sqlCorrectIcon}</td>
                <td style={{ padding: '12px 15px', color: '#475569' }}>{row.timeDesc}</td>
                <td style={{ padding: '12px 15px' }}>
                  <span style={{ 
                    backgroundColor: row.accuracy === "0%" ? '#f1f5f9' : parseInt(row.accuracy) >= 80 ? '#dcfce7' : parseInt(row.accuracy) >= 50 ? '#fef9c3' : '#fee2e2',
                    color: row.accuracy === "0%" ? '#64748b' : parseInt(row.accuracy) >= 80 ? '#166534' : parseInt(row.accuracy) >= 50 ? '#854d0e' : '#991b1b',
                    padding: '4px 8px', borderRadius: '4px', fontWeight: 'bold'
                  }}>
                    {row.accuracy}
                  </span>
                </td>
                <td style={{ padding: '12px 15px', color: '#0f172a', fontWeight: '500' }}>{row.correctRatio}</td>
                <td style={{ padding: '12px 15px', color: '#64748b' }}>{row.note}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {evaluations.length > 0 ? (
        <div>
          <h3 style={{ fontSize: '1.1rem', color: '#334155', marginBottom: '10px' }}>Lịch sử đánh giá chi tiết ({evaluations.length} lượt)</h3>
          <div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '8px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
               <thead style={{ backgroundColor: '#f8fafc', position: 'sticky', top: 0 }}>
                 <tr>
                    <th style={{ padding: '10px 15px', borderBottom: '1px solid #e2e8f0', color: '#334155' }}>Câu hỏi</th>
                    <th style={{ padding: '10px 15px', borderBottom: '1px solid #e2e8f0', color: '#334155' }}>Phương thức</th>
                    <th style={{ padding: '10px 15px', borderBottom: '1px solid #e2e8f0', color: '#334155' }}>Đánh giá</th>
                 </tr>
               </thead>
               <tbody>
                 {evaluations.slice().reverse().map((e, i) => (
                   <tr key={i} style={{ borderBottom: '1px solid #f1f5f9' }}>
                     <td style={{ padding: '10px 15px', maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{e.question}</td>
                     <td style={{ padding: '10px 15px', color: '#475569' }}>{methodNames[e.method]}</td>
                     <td style={{ padding: '10px 15px' }}>
                       {e.rating === 'correct' ? '✅ Đúng' : e.rating === 'partial' ? '⚠️ Tạm ổn' : '❌ Sai'}
                     </td>
                   </tr>
                 ))}
               </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div style={{ padding: '20px', textAlign: 'center', backgroundColor: '#f8fafc', borderRadius: '8px', color: '#64748b' }}>
          Chưa có đánh giá nào. Hãy thử đặt câu hỏi bên tab "Chuyển Đổi" và đánh giá kết quả!
        </div>
      )}
    </article>
  );
}

export default function App() {
  const [activeTab, setActiveTab] = useState("query"); // "query" | "eval"
  const [q, setQ] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [selectedMethod, setSelectedMethod] = useState("rule");
  const [resultData, setResultData] = useState(null);
  
  // Track latency and method for the currently viewed result
  const [currentExecution, setCurrentExecution] = useState(null);
  // Store all user ratings
  const [evaluations, setEvaluations] = useState([]);

  const methodNames = {
    rule: "Method 1: Rule-based",
    ai: "Method 2: LLM basic",
    aiSchema: "Method 3: LLM + Schema"
  };

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
      const res = await axios.post(`${API_BASE}/generate/ask?method=${selectedMethod}`, {
        question: nextQuestion
      });
      const latency = Date.now() - startTime;
      
      setResultData({
        sql: res.data.generatedSql || res.data.sql,
        result: res.data.result,
        error: null
      });
      setCurrentExecution({
        method: selectedMethod,
        question: nextQuestion,
        latency: latency
      });
    } catch (err) {
      const latency = Date.now() - startTime;
      
      setResultData({
        sql: null,
        result: null,
        error: err.response?.data?.detail || `Lỗi khi gọi API phương thức ${selectedMethod}`
      });
      setCurrentExecution({
        method: selectedMethod,
        question: nextQuestion,
        latency: latency
      });
    } finally {
      setQ(nextQuestion);
      setLoading(false);
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
          <span className="eyebrow">Buildi</span>
          <h1>A tool to convert user questions into SQL queries.</h1>
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
            Chuyển Đổi (Truy vấn)
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
            So sánh & Đánh giá
          </button>
        </div>

        {activeTab === "query" && (
          <>
            <section className="panel composer">
              <label htmlFor="question" className="panel-title">
                Đặt câu hỏi và chọn phương pháp
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
                
                <select 
                  value={selectedMethod}
                  onChange={(e) => setSelectedMethod(e.target.value)}
                  style={{
                    padding: '10px 15px',
                    borderRadius: '8px',
                    border: '1px solid #cbd5e1',
                    backgroundColor: '#f8fafc',
                    color: '#334155',
                    fontSize: '0.95rem',
                    cursor: 'pointer',
                    outline: 'none',
                    minWidth: '200px'
                  }}
                >
                  <option value="rule">{methodNames.rule}</option>
                  <option value="ai">{methodNames.ai}</option>
                  <option value="aiSchema">{methodNames.aiSchema}</option>
                </select>

                <button onClick={() => ask()} disabled={loading} style={{ minWidth: '120px' }}>
                  {loading ? "Đang xử lý..." : "Gửi"}
                </button>
              </div>

              {error ? <p className="status error">{error}</p> : null}
            </section>

            <section style={{ marginTop: '30px' }}>
              {(resultData || loading) && (
                <ResultPanel 
                  title={methodNames[selectedMethod]} 
                  data={resultData} 
                  loading={loading}
                  execution={currentExecution}
                  onRate={handleRate}
                />
              )}
            </section>
          </>
        )}

        {activeTab === "eval" && (
          <EvaluationPanel evaluations={evaluations} methodNames={methodNames} />
        )}
      </main>
    </div>
  );
}
