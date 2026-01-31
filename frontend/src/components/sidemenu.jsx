import React, { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import "./sidemenu.css";

export default function SideMenu({ setUser }) {
  const handleLogout = () => {
    localStorage.removeItem("userId");
    setUser(null);
    navigate("/login");
  };

  return (
    <nav className="site-nav">
      <ul>
        <li>
          <NavLink to="/dashboard">Dashboard</NavLink>
        </li>
        <li>
          <NavLink to="/exercises">Exercises</NavLink>
        </li>

        <li>
          <NavLink to="/categories">Categories</NavLink>
        </li>
        <li>
          <button onClick={handleLogout}>Logout</button>
        </li>
      </ul>
    </nav>
  );
}
