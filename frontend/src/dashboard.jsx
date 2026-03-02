import React, { useEffect, useState } from "react";
import "./dashboard.css";
import Modal from "./components/modal";
import AddWordForm from "./components/addWordForm";
import GenerateWordsAIForm from "./components/generateWordsAIForm";
import Button from "./components/button";
import ExportTxtForm from "./components/exportTxtForm";
import { FaTrash, FaEdit } from "react-icons/fa";
import ReactPaginate from "react-paginate";

export default function Dashboard({ userId }) {
  const [words, setWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [editingWord, setEditingWord] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [showAiForm, setShowAiForm] = useState(false);
  const [fileName, setFileName] = useState("");
  const [showFormExport, setShowFormExport] = useState(false);

  const [currentPage, setCurrentPage] = useState(0);
  const itemsPerPage = 10;

  const offset = currentPage * itemsPerPage;
  const currentItems = words.slice(
    currentPage * itemsPerPage,
    currentPage * itemsPerPage + itemsPerPage,
  );
  const pageCount = Math.ceil(words.length / itemsPerPage);

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

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

  const handleSaveFromModal = (savedWord) => {
    if (editingWord) {
      setWords((prev) =>
        prev.map((w) => (w.id === editingWord.id ? savedWord : w)),
      );
    } else {
      setWords((prev) => [...prev, savedWord]);
    }
    setEditingWord(null);
    setShowForm(false);
  };

  if (loading) return <div>Loading words...</div>;

  return (
    <div>
      <div>
        <Modal isOpen={showForm} onClose={() => setShowForm(false)}>
          <AddWordForm
            editingWord={editingWord}
            categories={categories}
            userId={userId}
            onSave={handleSaveFromModal}
          />
        </Modal>

        <Modal isOpen={showFormExport} onClose={() => setShowFormExport(false)}>
          <ExportTxtForm
            fileName={fileName}
            setFileName={setFileName}
            onSubmit={handleExportTxt}
          />
        </Modal>

        <Modal isOpen={showAiForm} onClose={() => setShowAiForm(false)}>
          <GenerateWordsAIForm
            userId={userId}
            onSave={(newWords) => {
              setWords((prev) => [...prev, ...newWords]);
              setShowAiForm(false);
            }}
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
              <Button
                text="Generate words with AI"
                onClick={() => setShowAiForm(true)}
              ></Button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>Word</th>
                  <th>Translation</th>
                  <th>Success rate</th>
                  <th>Category</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {currentItems.length === 0 ? (
                  <tr>
                    <td colSpan={3}>No words found.</td>
                  </tr>
                ) : (
                  currentItems.map((word) => (
                    <tr key={word.id}>
                      <td>{word.word}</td>
                      <td>{word.translation}</td>
                      <td>{word.success} %</td>
                      <td>{word.category_name}</td>
                      <td>
                        <button
                          className="edit"
                          onClick={() => {
                            setEditingWord(word);
                            setShowForm(true);
                          }}
                        >
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
              {pageCount > 1 && (
                <ReactPaginate
                  previousLabel={"← Previous"}
                  nextLabel={"Next →"}
                  breakLabel={"..."}
                  pageCount={pageCount}
                  marginPagesDisplayed={2}
                  pageRangeDisplayed={5}
                  onPageChange={handlePageClick}
                  containerClassName={"pagination"}
                  pageClassName={"page-item"}
                  pageLinkClassName={"page-link"}
                  previousClassName={"page-item"}
                  previousLinkClassName={"page-link"}
                  nextClassName={"page-item"}
                  nextLinkClassName={"page-link"}
                  breakClassName={"page-item"}
                  breakLinkClassName={"page-link"}
                  activeClassName={"active"}
                />
              )}
            </div>
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
