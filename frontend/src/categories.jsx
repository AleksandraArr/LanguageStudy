import React, { useEffect, useState } from "react";
import "./dashboard.css";
import SideMenu from "./components/SideMenu";

export default function Categories({ userId }) {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

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

  if (loading) return <div>Loading categories...</div>;

  return (
    <div className="dashboard-wrapper">
      <SideMenu />
      <div className="dashboard-content">
        <div className="table-data">
          <div className="order">
            <div className="head">
              <h3>Your categories</h3>
            </div>
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
