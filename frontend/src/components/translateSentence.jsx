import React, { useEffect, useState } from "react";

export default function TranslateSentence({ userId }) {
  const [sentence, setSentence] = useState(null);
  const [words, setWords] = useState([]);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadSentence = async () => {
    setLoading(true);
    setAnswer("");
    setResult(null);

    try {
      const res = await fetch(
        "http://localhost:3000/api/exercise/generate-sentence",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ "user-id": userId }),
        },
      );

      const data = await res.json();

      if (data.success) {
        setSentence(data.data.sentence);
        setWords(data.data.words);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const checkAnswer = async () => {
    if (!answer.trim()) return;

    try {
      const res = await fetch(
        "http://localhost:3000/api/exercise/check-sentence",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            sentence,
            "user-input": answer,
          }),
        },
      );

      const data = await res.json();

      if (data.success) {
        setResult(data.feedback);
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadSentence();
  }, []);

  return (
    <div className="form-container">
      <h2>Translate the sentence</h2>

      {loading && <p>Loading...</p>}

      {!loading && sentence && (
        <>
          <p style={{ fontStyle: "italic" }}>{sentence}</p>

          <input
            className="form-input"
            type="text"
            placeholder="Your translation"
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            disabled={!!result}
          />

          {!result && (
            <button className="form-button" onClick={checkAnswer}>
              Check
            </button>
          )}

          {result && (
            <>
              <p
                style={{
                  color: result.includes("Correct") ? "green" : "red",
                  marginTop: 10,
                  whiteSpace: "pre-line",
                }}
              >
                {result}
              </p>

              <button className="form-button" onClick={loadSentence}>
                Next sentence
              </button>
            </>
          )}
        </>
      )}
    </div>
  );
}
