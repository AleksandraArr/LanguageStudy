import React, { useEffect, useState } from "react";
import "./dashboard.css";
import Modal from "./components/modal";
import AddWordForm from "./components/addWordForm";
import Button from "./components/button";
import ExportTxtForm from "./components/exportTxtForm";
import { FaTrash, FaEdit } from "react-icons/fa";

export default function Dashboard({ userId }) {
  const [words, setWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [fileName, setFileName] = useState("");
  const [showFormExport, setShowFormExport] = useState(false);

  const [newWord, setNewWord] = useState({
    word: "",
    translation: "",
    catId: "",
  });

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
          setCategories(data.categories);
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
          word: newWord.word,
          translation: newWord.translation,
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

  const handleDeleteWord = async (wordId) => {
    try {
      const res = await fetch(`http://localhost:3000/api/words/${wordId}`, {
        method: "DELETE",
      });

      const data = await res.json();

      if (data.success) {
        setWords(words.filter((w) => w.id !== wordId));
      }
    } catch (err) {
      console.error("Delete error:", err);
    }
  };

  const handleExportTxt = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:3000/api/export-xlsx", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          "user-id": userId,
          "file-name": fileName,
        }),
      });
    } catch (err) {
      console.error("Error exporting to txt", err);
    }
  };

  if (loading) return <div>Loading words...</div>;

  return (
    <div>
      <div>
        <h1>Dashboard</h1>
        <Modal isOpen={showForm} onClose={() => setShowForm(false)}>
          <AddWordForm
            newWord={newWord}
            setNewWord={setNewWord}
            categories={categories}
            selectedCategory={selectedCategory}
            setSelectedCategory={setSelectedCategory}
            onSubmit={handleAddWord}
          />
        </Modal>
        <Modal isOpen={showFormExport} onClose={() => setShowFormExport(false)}>
          <ExportTxtForm
            fileName={fileName}
            setFileName={setFileName}
            onSubmit={handleExportTxt}
          />
        </Modal>

        <div className="table-data">
          <div className="order">
            <div className="head">
              <h3>Your Words</h3>
              <Button
                text="Add word"
                onClick={() => setShowForm(true)}
              ></Button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>Word</th>
                  <th>Translation</th>
                  <th>Success rate</th>
                  <th>Actions</th>
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
                      <td>{word.success} %</td>
                      <td>
                        <button className="edit">
                          <FaEdit />
                        </button>
                      </td>
                      <td>
                        <button
                          className="edit"
                          onClick={() => handleDeleteWord(word.id)}
                        >
                          <FaTrash />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
            <div>
              <Button
                text="Export to txt"
                onClick={() => setShowFormExport(true)}
              ></Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
