// Profile.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/profile.css";

const Profile = () => {
  const [user, setUser] = useState(null);
  const [displayName, setDisplayName] = useState("");
  const [bio, setBio] = useState("");
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    // Fetch current user
    const fetchUser = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/user/me", {
          method: "GET",
          credentials: "include",
        });
        const data = await res.json();
        if (res.ok) {
          setUser(data);
          setDisplayName(data.displayName || "");
          setBio(data.bio || "");
        } else {
          setError(data.error || "Failed to fetch user");
        }
      } catch (err) {
        console.error(err);
        setError("Error fetching user data");
      }
    };

    fetchUser();
  }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();
    setMessage(null);
    setError(null);

    try {
      const res = await fetch("http://localhost:8080/api/user/profile", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          displayName,
          bio,
        }),
      });

      const data = await res.json();
      if (res.ok) {
        setUser(data);
        setMessage("Profile updated successfully!");
      } else {
        setError(data.error || "Failed to update profile");
      }
    } catch (err) {
      console.error(err);
      setError("Error updating profile");
    }
  };

  if (!user) return <p>Loading...</p>;

  return (
    <div className="container profile-container">
      <div className="profile-header">
        {user.avatarUrl && (
          <img src={user.avatarUrl} alt="Profile" className="profile-picture" />
        )}
        <h1>{user.displayName}</h1>
        <p className="email">{user.email}</p>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-error">{error}</div>}

      <form onSubmit={handleUpdate}>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input type="email" id="email" value={user.email} readOnly />
          <p className="info-text">Email cannot be changed</p>
        </div>

        <div className="form-group name">
          <label htmlFor="displayName">Display Name</label>
          <input
            type="text"
            id="displayName"
            name="displayName"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            placeholder="Enter your display name"
            required
          />
        </div>

        <div className="form-group bio">
          <label htmlFor="bio">Bio</label>
          <textarea
            id="bio"
            name="bio"
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            placeholder="Tell us about yourself..."
          />
        </div>

        <div className="button-group">
          <button type="submit" className="btn-primary">
            Update Profile
          </button>
          <button
            type="button"
            className="btn-secondary"
            onClick={() => navigate("/home")}
          >
            Back to Home
          </button>
        </div>
      </form>
    </div>
  );
};

export default Profile;
