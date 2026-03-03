import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { NavLink } from "react-router-dom";
import "./sidemenu.css";
import { FaBookOpen, FaTasks, FaTags, FaSignOutAlt } from "react-icons/fa";
import logo from "../img/logo.png";

export default function SideMenu({ setUser }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("userId");
    setUser(null);
    navigate("/login");
  };

  return (
    <nav className="site-nav">
      <div className="sidebar-header">
        <img src={logo} alt="Logo" className="sidebar-logo" />
        <span className="sidebar-title">WordsVoyage</span>
      </div>

      <ul>
        <li>
          <NavLink to="/dashboard">
            <FaBookOpen />
            Words
          </NavLink>
        </li>
        <li>
          <NavLink to="/exercises">
            <FaTasks />
            Exercises
          </NavLink>
        </li>
        <li>
          <NavLink to="/categories">
            <FaTags />
            Categories
          </NavLink>
        </li>
        <li>
          <NavLink to="#" onClick={handleLogout}>
            <FaSignOutAlt />
            Logout
          </NavLink>
        </li>
      </ul>
    </nav>
  );
}
