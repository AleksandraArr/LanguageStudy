import React, { useEffect, useState } from "react";
export default function TranslateWord({ userId }) {
  const [word, setWord] = useState(null);
  const [wordId, setWordId] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadWord = async () => {
    setLoading(true);
    setAnswer("");
    setResult(null);

    try {
      const res = await fetch("http://localhost:3000/api/exercise/translate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ "user-id": userId }),
      });

      const data = await res.json();

      if (data.success) {
        setWord(data.data.word);
        setWordId(data.data["word-id"]);
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
      console.log("wordId:", wordId);
      const res = await fetch(
        "http://localhost:3000/api/exercise/translate/check",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            "word-id": wordId,
            answer,
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
    loadWord();
  }, []);

  return (
    <div className="form-container">
      <h2>Translate the word</h2>

      {loading && <p>Loading...</p>}

      {!loading && word && (
        <>
          <h3>{word}</h3>

          <input
            className="form-input"
            type="text"
            placeholder="Your translation"
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            disabled={result}
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
                  color: result.correct ? "green" : "red",
                  marginTop: 10,
                }}
              >
                {result.correct
                  ? "Correct!"
                  : `Wrong. Correct answer: ${result["correct-answer"]}`}
              </p>

              <button className="form-button" onClick={loadWord}>
                Next word
              </button>
            </>
          )}
        </>
      )}
    </div>
  );
}
