import React, { useEffect, useState } from "react";
import "./dashboard.css";
import Button from "./components/button";
import AddCategoryForm from "./components/addCategoryForm";
import Modal from "./components/modal";
import { FaEdit } from "react-icons/fa";
import { ClipLoader } from "react-spinners";

export default function Categories({ userId }) {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [categoryName, setCategoryName] = useState("");
  const [editingCategory, setEditingCategory] = useState(null);

  useEffect(() => {
    if (!userId) return;

    fetch(`http://localhost:3000/api/categories?user-id=${userId}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          setCategories(data.categories);
        } else {
          console.error(data.message);
        }
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [userId]);

  const handleSaveCategory = () => {
    if (editingCategory) {
      fetch(`http://localhost:3000/api/categories/${editingCategory.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: categoryName,
        }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setCategories(
              categories.map((cat) =>
                cat.id === editingCategory.id
                  ? { ...cat, name: categoryName }
                  : cat,
              ),
            );
            resetModal();
          }
        });
    } else {
      fetch("http://localhost:3000/api/categories", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          "user-id": userId,
          name: categoryName,
        }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setCategories([...categories, { id: data.id, name: categoryName }]);
            resetModal();
          }
        });
    }
  };

  const resetModal = () => {
    setCategoryName("");
    setEditingCategory(null);
    setShowForm(false);
  };

  if (loading)
    return (
      <div>
        <ClipLoader />
      </div>
    );

  return (
    <div className="container">
      <div className="inside-container">
        <div className="head">
          <h3>Your categories</h3>
          <Button
            text="Add category"
            onClick={() => {
              setEditingCategory(category);
              setCategoryName(category.name);
              setShowForm(true);
            }}
          ></Button>
        </div>
        <Modal isOpen={showForm} onClose={() => setShowForm(false)}>
          <AddCategoryForm
            categoryName={categoryName}
            setCategoryName={setCategoryName}
            onSubmit={(e) => {
              e.preventDefault();
              handleSaveCategory();
            }}
          />
        </Modal>
        <table>
          <thead>
            <tr>
              <th>Name</th>
            </tr>
          </thead>
          <tbody>
            {categories.length === 0 ? (
              <tr>
                <td colSpan={3}>No category found.</td>
              </tr>
            ) : (
              categories.map((category) => (
                <tr key={category.id}>
                  <td>{category.name}</td>
                  <td>
                    <button
                      className="edit"
                      onClick={() => {
                        setEditingCategory(category);
                        setCategoryName(category.name);
                        setShowForm(true);
                      }}
                    >
                      <FaEdit />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
