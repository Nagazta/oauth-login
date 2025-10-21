import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { fetchCurrentUser } from "../api/userApi";
import Home from "../pages/home";

const ProtectedRoute = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCurrentUser().then((data) => {
      setUser(data);
      setLoading(false);
    });
  }, []);

  if (loading) return <p>Loading...</p>;

  return user ? <Home user={user} /> : <Navigate to="/login" />;
};

export default ProtectedRoute;
