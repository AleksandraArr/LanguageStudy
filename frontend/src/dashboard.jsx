import React, { useEffect, useState } from "react";
import './dashboard.css';
import SideMenu from "./components/SideMenu";

export default function Dashboard({ userId }) {
  const [words, setWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [newWord, setNewWord] = useState({ word: "", translation: "", catId: "" });

  useEffect(() => {
    if (!userId) return;

    fetch(`http://localhost:3000/api/words?user-id=${userId}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          setWords(data.words);
        } else {
          console.error("Error fetching words:", data.message);
        }
      })
      .catch((err) => console.error("Fetch error:", err))
      .finally(() => setLoading(false));
  }, [userId]);

 useEffect(() => {
    if (!userId) return;

    fetch(`http://localhost:3000/api/categories?user-id=${userId}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          setCategories(data.categories); // [{id:1, name:"X"}, ...]
          if (data.categories.length > 0) {
            setSelectedCategory(data.categories[0].id);
          }
        }
      })
      .catch((err) => console.error(err));
  }, [userId]);


const handleAddWord = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:3000/api/words", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          "user-id": userId,
          "word": newWord.word,
          "translation": newWord.translation,
          "cat-id": Number(selectedCategory),
        }),
      });

      const data = await res.json();
      if (data.success) {
        setWords((prev) => [...prev, { id: Date.now(), ...newWord }]);
        setNewWord({ word: "", translation: "", catId: "" });
        setShowForm(false);
      } else {
        console.error("Add word failed:", data.error || data.message);
      }
    } catch (err) {
      console.error("Add word fetch error:", err);
    }
  };


  if (loading) return <div>Loading words...</div>;

 return (
    <div className="dashboard-wrapper">
      <SideMenu />

      <div className="dashboard-content">
        <h1>Dashboard</h1>

        <button
          style={{
            marginBottom: 16,
            padding: "8px 16px",
            backgroundColor: "#3C91E6",
            color: "#fff",
            borderRadius: 8,
            cursor: "pointer",
          }}
          onClick={() => setShowForm((prev) => !prev)}
        >
          Add Word
        </button>

        {showForm && (
          <form onSubmit={handleAddWord} style={{ marginBottom: 24 }}>
            <input
              type="text"
              placeholder="Word"
              value={newWord.word}
              onChange={(e) =>
                setNewWord({ ...newWord, word: e.target.value })
              }
              required
              style={{ marginRight: 8 }}
            />
            <input
              type="text"
              placeholder="Translation"
              value={newWord.translation}
              onChange={(e) =>
                setNewWord({ ...newWord, translation: e.target.value })
              }
              required
              style={{ marginRight: 8 }}
            />
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              style={{ marginRight: 8 }}
            >
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>

            <button
              type="submit"
              style={{
                padding: "4px 12px",
                backgroundColor: "#3C91E6",
                color: "#fff",
                borderRadius: 4,
                cursor: "pointer",
              }}
            >
              Save
            </button>
          </form>
        )}

        <div className="table-data">
          <div className="order">
            <div className="head">
              <h3>Your Words</h3>
            </div>
            <table>
              <thead>
                <tr>
                  <th>Word</th>
                  <th>Translation</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {words.length === 0 ? (
                  <tr>
                    <td colSpan={3}>No words found.</td>
                  </tr>
                ) : (
                  words.map((word) => (
                    <tr key={word.id}>
                      <td>{word.word}</td>
                      <td>{word.translation}</td>
                      <td>
                        <button className="edit">Edit</button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );


}
