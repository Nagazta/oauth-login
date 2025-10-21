import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/home.css";

const Home = ({ user }) => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      // Call Spring Boot logout endpoint
      await fetch("http://localhost:8080/logout", { 
        method: "GET", 
        credentials: "include" 
      });
      
      // Clear any local state if needed
      console.log("✅ Logged out successfully");
      
      // Redirect to login
      navigate("/login");
    } catch (error) {
      console.error("Logout error:", error);
      // Still redirect to login even if there's an error
      navigate("/login");
    }
  };

  return (
    <div className="container">
      <div className="profile">
        {user.avatarUrl && (
          <img src={user.avatarUrl} alt="Profile" className="profile-picture" />
        )}
        <h1>Welcome, {user.displayName || "User"}!</h1>
        <p className="email">{user.email}</p>
        {user.bio && <p className="bio">{user.bio}</p>}

        <button onClick={handleLogout} className="logout-btn">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Home;