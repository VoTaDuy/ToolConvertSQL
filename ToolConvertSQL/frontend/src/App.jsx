import { useState } from "react";
import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://127.0.0.1:8000";

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
      const res = await axios.get(`${API_BASE}/query`, {
        params: { q: nextQuestion },
      });
      setData(res.data);
      setQ(nextQuestion);
    } catch (err) {
      setData(null);
      setError(
        err.response?.data?.detail ||
          "Không gọi được API. Kiểm tra backend và OPENAI_API_KEY/API_KEY trong backend/.env rồi thử lại."
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
        <section className="hero">
          <span className="eyebrow">Buildi</span>
          <h1>A tool to convert user questions into SQL queries.</h1>
          <p>
            Nhập câu hỏi bằng ngôn ngữ tự nhiên, hệ thống sẽ sinh SQL và trả về
            dữ liệu để bạn kiểm tra nhanh.
          </p>
        </section>

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
                if (e.key === "Enter") {
                  ask();
                }
              }}
              placeholder="Nhập câu hỏi để sinh SQL..."
            />
            <button onClick={() => ask()} disabled={loading}>
              {loading ? "Đang hỏi..." : "Gửi"}
            </button>
          </div>

          {error ? <p className="status error">{error}</p> : null}
          {!error && warning ? <p className="status warning">{warning}</p> : null}
        </section>

        <section className="results-grid">
          <article className="panel">
            <div className="panel-head">
              <h2>SQL</h2>
              <span className="badge">
                {data?.mode === "local_fallback" ? "Chế độ nội bộ" : "Tự động sinh"}
              </span>
            </div>
            <pre>{data?.sql || "SQL sẽ xuất hiện ở đây sau khi bạn đặt câu hỏi."}</pre>
          </article>

          <article className="panel">
            <div className="panel-head">
              <h2>Thông tin truy vấn</h2>
              <span className="badge neutral">
                {hasData ? "Đã xử lý" : "Chờ truy vấn"}
              </span>
            </div>
            <div className="query-meta">
              <p>
                <strong>Câu hỏi:</strong>{" "}
                {submittedQuestion || "Bạn chưa nhập câu hỏi."}
              </p>
              <p>
                <strong>Trạng thái:</strong>{" "}
                {loading
                  ? "Đang gửi yêu cầu..."
                  : hasData
                  ? "Hoàn tất"
                  : "Sẵn sàng"}
              </p>
              <p>
                <strong>Số dòng trả về:</strong> {rows.length}
              </p>
            </div>
          </article>
        </section>

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
                      {columns.map((column) => (
                        <th key={column}>{column}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {rows.map((row, index) => (
                      <tr key={`${index}-${row.join("-")}`}>
                        {row.map((cell, cellIndex) => (
                          <td key={`${index}-${cellIndex}`}>{String(cell)}</td>
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
              <p>Chưa có dữ liệu.</p>
              <span>Hãy nhập câu hỏi của bạn.</span>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
