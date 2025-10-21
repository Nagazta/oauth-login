import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/home.css";

const Home = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Fetch the logged-in user's info
  useEffect(() => {
    fetch("http://localhost:8080/api/user/me", { credentials: "include" })
      .then((res) => {
        if (!res.ok) throw new Error("Not authenticated");
        return res.json();
      })
      .then((data) => {
        setUser(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        navigate("/login"); // redirect if not authenticated
      });
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await fetch("http://localhost:8080/logout", {
        method: "POST",
        credentials: "include",
      });
      navigate("/login");
    } catch (err) {
      console.error(err);
      navigate("/login");
    }
  };

  if (loading) return <p>Loading user info...</p>;

  return (
    <div className="container">
      <div className="profile">
        {user.avatarUrl && (
          <img
            src={user.avatarUrl}
            alt="Profile Picture"
            className="profile-picture"
          />
        )}
        <h1>Welcome, {user.displayName}!</h1>
        <p className="email">{user.email}</p>
        {user.bio && <p className="bio">{user.bio}</p>}

        <a
          href="/profile"
          className="login-btn"
          style={{ marginBottom: "15px" }}
        >
          View Profile
        </a>

        <button onClick={handleLogout} className="logout-btn">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Home;
