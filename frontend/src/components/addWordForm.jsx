import Button from "./button";
import React from "react";
import "./form.css";

export default function AddWordForm({
  editingWord,
  categories,
  userId,
  onSave,
}) {
  const [word, setWord] = React.useState(editingWord?.word || "");
  const [translation, setTranslation] = React.useState(
    editingWord?.translation || "",
  );
  const [selectedCategory, setSelectedCategory] = React.useState(
    editingWord?.category_id || categories[0]?.id || "",
  );

  React.useEffect(() => {
    if (editingWord) {
      setWord(editingWord.word);
      setTranslation(editingWord.translation);
      setSelectedCategory(editingWord.category_id);
    }
  }, [editingWord]);

  const addWord = async () => {
    const res = await fetch("http://localhost:3000/api/words", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        "user-id": userId,
        word: word,
        translation: translation,
        "cat-id": Number(selectedCategory),
      }),
    });

    const data = await res.json();

    if (data.success) {
      onSave({
        id: data.id,
        word,
        translation,
        catId: Number(selectedCategory),
        category_name:
          categories.find((c) => c.id === Number(selectedCategory))?.name || "",
        success: 0,
      });
    } else {
      console.error("Add word failed:", data.error || data.message);
    }
  };

  const editWord = async () => {
    if (!editingWord) return;
    const res = await fetch(
      `http://localhost:3000/api/words/${editingWord.id}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          word: word,
          translation: translation,
          "cat-id": Number(selectedCategory),
        }),
      },
    );

    const data = await res.json();

    if (data.success) {
      onSave({
        id: editingWord.id,
        word,
        translation,
        category_id: Number(selectedCategory),
        category_name:
          categories.find((c) => c.id === Number(selectedCategory))?.name || "",
        success: editingWord.success,
      });
    } else {
      console.error("Edit word failed:", data.error || data.message);
    }
  };

  const handleSave = (e) => {
    e.preventDefault();
    if (editingWord) {
      editWord();
    } else {
      addWord();
    }
  };

  return (
    <form className="form-container" onSubmit={handleSave}>
      <h2>{editingWord ? "Edit word" : "Add word"}</h2>
      <input
        className="form-input"
        value={word}
        onChange={(e) => setWord(e.target.value)}
        placeholder="Word"
        required
      />
      <input
        className="form-input"
        value={translation}
        onChange={(e) => setTranslation(e.target.value)}
        placeholder="Translation"
        required
      />
      <select
        className="form-select"
        value={selectedCategory}
        onChange={(e) => setSelectedCategory(Number(e.target.value))}
      >
        {categories.map((c) => (
          <option key={c.id} value={c.id}>
            {c.name}
          </option>
        ))}
      </select>
      <Button text={editingWord ? "Save" : "Add"} type="submit" />
    </form>
  );
}
