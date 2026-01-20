import "./Button.css";
import React from "react";

export default function Button({
  text,
  onClick,
  type = "button",
  disabled = false,
}) {
  return (
    <button
      className="button"
      onClick={onClick}
      type={type}
      disabled={disabled}
    >
      {text}
    </button>
  );
}
