import Button from "./button";
import React from "react";
import "./AddWordForm.css";

export default function AddWordForm({
  newWord,
  setNewWord,
  categories,
  selectedCategory,
  setSelectedCategory,
  onSubmit,
}) {
  return (
    <form className="form-container" onSubmit={onSubmit}>
      <h2>Add word</h2>

      <input
        className="form-input"
        type="text"
        placeholder="Word"
        value={newWord.word}
        onChange={(e) => setNewWord({ ...newWord, word: e.target.value })}
        required
      />

      <input
        className="form-input"
        type="text"
        placeholder="Translation"
        value={newWord.translation}
        onChange={(e) =>
          setNewWord({ ...newWord, translation: e.target.value })
        }
        required
      />

      <select
        className="form-select"
        value={selectedCategory}
        onChange={(e) => setSelectedCategory(e.target.value)}
      >
        {categories.map((cat) => (
          <option key={cat.id} value={cat.id}>
            {cat.name}
          </option>
        ))}
      </select>

      <Button text="Save" type="submit" />
    </form>
  );
}
