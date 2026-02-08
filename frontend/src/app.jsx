import React, { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./dashboard";
import Categories from "./categories";
import Login from "./login";
import Exercises from "./exercies";
import SideMenu from "./components/sidemenu";
import Register from "./register";

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      setUser({ id: Number(storedUserId) });
    }
  }, []);

  return (
    <BrowserRouter>
      {user ? (
        <div className="wrapper">
          <SideMenu setUser={setUser} />

          <div className="content">
            <Routes>
              <Route
                path="/dashboard"
                element={<Dashboard userId={user.id} />}
              />
              <Route
                path="/categories"
                element={<Categories userId={user.id} />}
              />
              <Route
                path="/exercises"
                element={<Exercises userId={user.id} />}
              />
              <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
          </div>
        </div>
      ) : (
        <Routes>
          <Route path="/login" element={<Login setUser={setUser} />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      )}
    </BrowserRouter>
  );
}

export default App;
