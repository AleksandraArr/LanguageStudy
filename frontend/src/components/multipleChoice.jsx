import React, { useEffect, useState } from "react";

export default function MultipleChoice({ userId }) {
  const [word, setWord] = useState(null);
  const [wordId, setWordId] = useState(null);
  const [options, setOptions] = useState([]);
  const [selected, setSelected] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadQuestion = async () => {
    setLoading(true);
    setSelected(null);
    setResult(null);

    try {
      const res = await fetch(
        "http://localhost:3000/api/exercise/multiple-choice",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ "user-id": userId }),
        },
      );

      const data = await res.json();

      if (data.success) {
        setWord(data.data.word);
        setWordId(data.data["word-id"]);
        setOptions(data.data.options);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const checkAnswer = async () => {
    if (!selected) return;

    try {
      const res = await fetch(
        "http://localhost:3000/api/exercise/translate/check",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            "word-id": wordId,
            answer: selected,
          }),
        },
      );

      const data = await res.json();
      if (data.success) {
        setResult(data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadQuestion();
  }, []);

  return (
    <div className="form-container">
      <h2>Choose the correct translation</h2>

      {loading && <p>Loading...</p>}

      {!loading && word && (
        <>
          <h3>{word}</h3>

          <div className="options">
            {options.map((opt, idx) => (
              <label key={idx} style={{ display: "block", marginBottom: 8 }}>
                <input
                  type="radio"
                  name="option"
                  value={opt}
                  disabled={result}
                  checked={selected === opt}
                  onChange={() => setSelected(opt)}
                />{" "}
                {opt}
              </label>
            ))}
          </div>

          {!result && (
            <button
              className="form-button"
              onClick={checkAnswer}
              disabled={!selected}
            >
              Check
            </button>
          )}

          {result && (
            <>
              <p
                style={{
                  color: result.correct ? "green" : "red",
                  marginTop: 10,
                }}
              >
                {result.correct
                  ? "Correct!"
                  : `Wrong. Correct answer: ${result["correct-answer"]}`}
              </p>

              <button className="form-button" onClick={loadQuestion}>
                Next question
              </button>
            </>
          )}
        </>
      )}
    </div>
  );
}
