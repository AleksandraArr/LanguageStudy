import Button from "./button";
import React from "react";
import "./form.css";

export default function GenerateWordsAIForm({ userId, onSave }) {
  const [level, setLevel] = React.useState("A1");
  const [number, setNumber] = React.useState(5);
  const [language, setLanguage] = React.useState("");
  const [targetLanguage, setTargetLanguage] = React.useState("");
  const [notes, setNotes] = React.useState("");
  const [loading, setLoading] = React.useState(false);

  const handleGenerate = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await fetch("http://localhost:3000/api/ai/generate-words", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          "user-id": userId,
          level,
          number: Number(number),
          language,
          "target-language": targetLanguage,
          notes,
        }),
      });

      const data = await res.json();

      if (data.success) {
        onSave(data.data);
      } else {
        console.error(data.message);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="form-container" onSubmit={handleGenerate}>
      <h2>Generate Words with AI</h2>

      <select
        className="form-select"
        value={level}
        onChange={(e) => setLevel(e.target.value)}
      >
        <option value="A1">A1</option>
        <option value="A2">A2</option>
        <option value="B1">B1</option>
        <option value="B2">B2</option>
        <option value="B1">C1</option>
        <option value="B2">C2</option>
      </select>

      <input
        className="form-input"
        type="number"
        min="1"
        max="50"
        value={number}
        onChange={(e) => setNumber(e.target.value)}
        required
      />

      <input
        className="form-input"
        value={language}
        placeholder="Your language"
        onChange={(e) => setLanguage(e.target.value)}
        required
      />

      <input
        className="form-input"
        value={targetLanguage}
        placeholder="Target language"
        onChange={(e) => setTargetLanguage(e.target.value)}
        required
      />

      <Button text={loading ? "Generating..." : "Generate"} type="submit" />
    </form>
  );
}
