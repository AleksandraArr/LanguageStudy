import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./sidemenu.css";

export default function SideMenu() {
  return (
    <nav className="site-nav">
      <ul>
        <li className="active">
          <a href="#">Exercises</a>
          <ul>
            <li>
              <a href="#">Translate words</a>
            </li>
            <li>
              <a href="#">Multiple choice</a>
            </li>
            <li>
              <a href="#">Translate the sentence</a>
            </li>
          </ul>
        </li>

        <li>
          <Link to="/categories">Categories</Link>
        </li>
      </ul>
    </nav>
  );
}
