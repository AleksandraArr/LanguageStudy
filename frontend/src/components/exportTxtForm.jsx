import React from "react";
import Button from "./button";
import "./form.css";

export default function ExportTxtForm({ fileName, setFileName, onSubmit }) {
  return (
    <form className="form-container" onSubmit={onSubmit}>
      <h2>Export to txt</h2>
      <input
        className="form-input"
        type="text"
        placeholder="Enter name od file"
        value={fileName}
        onChange={(e) => setFileName(e.target.value)}
        required
      />
      <Button text="Export" type="submit" />
    </form>
  );
}
