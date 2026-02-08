import { useState } from "react";
import { useNavigate } from "react-router-dom";
import React from "react";
import "./components/form.css";
import Button from "./components/button";

export default function Register() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:3000/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      const data = await res.json();

      if (data.success) {
        navigate("/login");
      } else {
        setMessage(data.message || "Registration failed");
      }
    } catch {
      setMessage("Server not available");
    }
  };

  return (
    <form className="form-container" onSubmit={handleRegister}>
      <h1>Register</h1>

      <input
        className="form-input"
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
      />

      <input
        className="form-input"
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />

      <Button text="Register" type="submit" />

      {message && <p className="form-message">{message}</p>}
    </form>
  );
}
