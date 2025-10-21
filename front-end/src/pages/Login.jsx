import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { handleAuthCallback } from "../api/userApi";
import "../styles/login.css";

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isAuthenticating, setIsAuthenticating] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const urlError = params.get('error');
    const isCallback = params.get('callback') === 'true';

    if (urlError === 'no_email') {
      setError("Could not get email from provider. Please ensure email permissions are granted.");
      return;
    }

    if (isCallback) {
      setIsAuthenticating(true);

      const authenticate = async () => {
        const result = await handleAuthCallback();

        if (result.success) {
          navigate("/home", { replace: true });
        } else {
          setError(result.error || "Authentication failed. Please try again.");
          setIsAuthenticating(false);
          navigate("/login", { replace: true });
        }
      };

      authenticate();
    }
  }, [location, navigate]);

  const handleGoogleLogin = () => {
    setError(null);
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const handleGithubLogin = () => {
    setError(null);
    window.location.href = "http://localhost:8080/oauth2/authorization/github";
  };

  if (isAuthenticating) {
    return (
      <div className="container">
        <div style={{ 
          display: "flex", 
          flexDirection: "column", 
          alignItems: "center", 
          gap: "20px",
          marginTop: "30px"
        }}>
          <div className="spinner"></div>
          <p style={{ color: "#666666", fontSize: "16px" }}>Setting up your account...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <h1>Welcome!</h1>
      <p className="subtitle">
        Sign in or create an account instantly with your Google or GitHub account.
      </p>

      {error && (
        <div className="alert alert-error" style={{ marginBottom: "25px" }}>
          {error}
        </div>
      )}

      {/* Google Login Button */}
      <button onClick={handleGoogleLogin} className="google-login-btn">
        Continue with Google
      </button>

      {/* Divider */}
      <div style={{ 
        margin: "20px 0", 
        color: "#666666", 
        fontSize: "14px",
        display: "flex",
        alignItems: "center",
        gap: "10px"
      }}>
        <div style={{ flex: 1, height: "1px", backgroundColor: "#e0e0e0" }}></div>
        or
        <div style={{ flex: 1, height: "1px", backgroundColor: "#e0e0e0" }}></div>
      </div>

      {/* GitHub Login Button */}
      <button onClick={handleGithubLogin} className="github-login-btn">
        Continue with GitHub
      </button>

      {/* Security Note */}
      <div className="security-note" style={{ marginTop: "30px" }}>
        <strong>Secure Authentication:</strong> Your credentials are never stored. We use OAuth 2.0 for safe, encrypted sign-in.
      </div>

      <p style={{ 
        marginTop: "20px", 
        fontSize: "13px", 
        color: "#666666",
        textAlign: "center",
        lineHeight: "1.6"
      }}>
        By continuing, you agree to our Terms of Service and Privacy Policy.
        <br/>
        New users will have an account created automatically.
      </p>
    </div>
  );
};

export default Login;
