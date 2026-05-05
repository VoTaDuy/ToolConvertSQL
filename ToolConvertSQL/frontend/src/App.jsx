import { useState } from "react";
import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export default function App() {
  const [q, setQ] = useState("");
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const warning = data?.warning ?? "";

  const ask = async (question = q) => {
    const nextQuestion = question.trim();

    if (!nextQuestion) {
      setError("Hãy nhập câu hỏi trước khi gửi.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await axios.post(
        `${API_BASE}/generate/ask`,
        {
          question: nextQuestion,
        },
        {
          params: {
            method: "aiSchema", 
          },
        }
      );

      setData(res.data);
      setQ(nextQuestion);
    } catch (err) {
      console.error("API ERROR:", err);

      setData(null);
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          err.message ||
          "Không gọi được API. Kiểm tra backend (8080) và endpoint /generate/ask"
      );
    } finally {
      setLoading(false);
    }
  };

  const rows = data?.result?.rows ?? [];
  const columns = data?.result?.columns ?? [];
  const hasData = Boolean(data);
  const submittedQuestion = data?.question ?? q;

  return (
    <div className="page-shell">
      <div className="ambient ambient-left" />
      <div className="ambient ambient-right" />

      <main className="app-card">
        {/* HERO */}
        <section className="hero">
          <span className="eyebrow">Buildi</span>
          <h1>A tool to convert user questions into SQL queries.</h1>
          <p>
            Nhập câu hỏi bằng ngôn ngữ tự nhiên, hệ thống sẽ sinh SQL và trả về dữ liệu.
          </p>
        </section>

        {/* INPUT */}
        <section className="panel composer">
          <label htmlFor="question" className="panel-title">
            Đặt câu hỏi
          </label>

          <div className="composer-row">
            <input
              id="question"
              value={q}
              onChange={(e) => setQ(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") ask();
              }}
              placeholder="Ví dụ: List all movies with rating > 8"
            />

            <button onClick={() => ask()} disabled={loading}>
              {loading ? "Đang hỏi..." : "Gửi"}
            </button>
          </div>

          {error && <p className="status error">{error}</p>}
          {!error && warning && (
            <p className="status warning">{warning}</p>
          )}
        </section>

        {/* INFO */}
        <section className="results-grid">
          <article className="panel">
            <div className="panel-head">
              <h2>SQL</h2>
              <span className="badge">
                {data?.mode === "local_fallback"
                  ? "Chế độ nội bộ"
                  : "AI Schema"}
              </span>
            </div>

            <pre>
              {data?.sql || "SQL sẽ xuất hiện sau khi bạn đặt câu hỏi."}
            </pre>
          </article>

          <article className="panel">
            <div className="panel-head">
              <h2>Thông tin truy vấn</h2>
              <span className="badge neutral">
                {hasData ? "Đã xử lý" : "Chờ"}
              </span>
            </div>

            <div className="query-meta">
              <p>
                <strong>Câu hỏi:</strong> {submittedQuestion || "Chưa có"}
              </p>
              <p>
                <strong>Trạng thái:</strong>{" "}
                {loading ? "Đang chạy..." : hasData ? "OK" : "Sẵn sàng"}
              </p>
              <p>
                <strong>Số dòng:</strong> {rows.length}
              </p>
            </div>
          </article>
        </section>

        {/* TABLE */}
        <section className="panel">
          <div className="panel-head">
            <h2>Kết quả</h2>
            <span className="badge neutral">{rows.length} dòng</span>
          </div>

          {data ? (
            Array.isArray(columns) && columns.length > 0 ? (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      {columns.map((col) => (
                        <th key={col}>{col}</th>
                      ))}
                    </tr>
                  </thead>

                  <tbody>
                    {rows.map((row, i) => (
                      <tr key={i}>
                        {row.map((cell, j) => (
                          <td key={j}>{String(cell)}</td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <pre>{JSON.stringify(data.result, null, 2)}</pre>
            )
          ) : (
            <div className="empty-state">
              <p>Chưa có dữ liệu</p>
              <span>Hãy nhập câu hỏi để bắt đầu</span>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}