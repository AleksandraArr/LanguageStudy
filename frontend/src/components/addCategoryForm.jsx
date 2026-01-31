import React from "react";
import Button from "./button";
import "./form.css";

export default function AddCategoryForm({
  categoryName,
  setCategoryName,
  onSubmit,
}) {
  return (
    <form className="form-container" onSubmit={onSubmit}>
      <h2>Add category</h2>

      <input
        className="form-input"
        type="text"
        placeholder="Category name"
        value={categoryName}
        onChange={(e) => setCategoryName(e.target.value)}
        required
      />

      <Button text="Save" type="submit" />
    </form>
  );
}
