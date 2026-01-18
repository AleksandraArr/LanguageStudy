import React, { useState } from "react";

import Dashboard from "./Dashboard";

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [user, setUser] = useState(null);

  const handleLogin = async () => {
    if (!username || !password) {
      setMessage("Enter both username and password");
      return;
    }

    try {
      const res = await fetch("http://localhost:3000/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        setUser(data.user);
        setMessage("");
      } else {
        setMessage(data.message || "Error while logging in");
      }
    } catch (err) {
      console.error(err);
      setMessage("Server not available");
    }
  };

  if (user) {
    return <Dashboard userId={user.id} />;
  }

  return (
    <div style={styles.container}>
      <h1>Login</h1>

      <input
        style={styles.input}
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        style={styles.input}
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button style={styles.button} onClick={handleLogin}>
        Login
      </button>

      {message && <p style={styles.message}>{message}</p>}
    </div>
  );
}

const styles = {
  container: {
    padding: 40,
    display: "flex",
    flexDirection: "column",
    width: 300,
    margin: "50px auto",
    border: "1px solid #ccc",
    borderRadius: 8,
    boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
    fontFamily: "Arial, sans-serif",
  },
  input: {
    padding: 10,
    marginBottom: 15,
    fontSize: 16,
    borderRadius: 4,
    border: "1px solid #ccc",
  },
  button: {
    padding: 10,
    fontSize: 16,
    borderRadius: 4,
    border: "none",
    backgroundColor: "#4CAF50",
    color: "white",
    cursor: "pointer",
  },
  message: {
    marginTop: 15,
    color: "red",
    fontWeight: "bold",
  },
};

export default App;
