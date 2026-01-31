import React, { useEffect, useState } from "react";
import "./dashboard.css";
import Button from "./components/button";
import AddCategoryForm from "./components/addCategoryForm";
import Modal from "./components/modal";

export default function Categories({ userId }) {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [categoryName, setCategoryName] = useState("");

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

  const handleAddCategory = () => {
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
          setCategories([...categories, { name: categoryName }]);
          setCategoryName("");
          setShowForm(false);
        }
      })
      .catch(console.error);
  };

  if (loading) return <div>Loading categories...</div>;

  return (
    <div>
      <div>
        <div className="table-data">
          <div className="order">
            <div className="head">
              <h3>Your categories</h3>
              <Button
                text="Add category"
                onClick={() => setShowForm(true)}
              ></Button>
            </div>
            <Modal isOpen={showForm} onClose={() => setShowForm(false)}>
              <AddCategoryForm
                categoryName={categoryName}
                setCategoryName={setCategoryName}
                onSubmit={(e) => {
                  e.preventDefault();
                  handleAddCategory();
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
