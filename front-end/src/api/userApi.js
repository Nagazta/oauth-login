import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Fetch current authenticated user with retry logic for auth callback
export const fetchCurrentUser = async (retries = 3, delay = 500) => {
  for (let i = 0; i < retries; i++) {
    try {
      console.log(`Fetching user... (attempt ${i + 1}/${retries})`);
      
      // Add delay for first attempt to let session establish
      if (i === 0 && delay > 0) {
        await new Promise(resolve => setTimeout(resolve, delay));
      }
      
      const response = await api.get("/api/user/me");
      console.log("✅ User fetched successfully:", response.data);
      return response.data;
    } catch (error) {
      console.error(`Error fetching user (attempt ${i + 1}):`, error);
      
      if (error.response?.status === 401) {
        console.log("User not authenticated");
        return null;
      }
      
      // If not the last retry and it's a network/timeout error, wait and retry
      if (i < retries - 1 && (!error.response || error.response.status >= 500)) {
        console.log(`Retrying in ${delay}ms...`);
        await new Promise(resolve => setTimeout(resolve, delay));
        continue;
      }
      
      return null;
    }
  }
  return null;
};

// Simplified version without retries (for normal use)
export const fetchCurrentUserSimple = async () => {
  try {
    const response = await api.get("/api/user/me");
    return response.data;
  } catch (error) {
    console.error("Error fetching user:", error);
    if (error.response?.status === 401) {
      console.log("User not authenticated");
    }
    return null;
  }
};

// Auth callback handler - call this right after OAuth redirect
export const handleAuthCallback = async () => {
  try {
    console.log("🔄 Handling auth callback...");
    
    // Wait a bit for session to be fully established
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // Try to fetch user with retries
    const user = await fetchCurrentUser(3, 1000);
    
    if (user) {
      console.log("✅ Auth callback successful:", user);
      return { success: true, user };
    } else {
      console.error("❌ Auth callback failed: No user found");
      return { success: false, error: "No user found after authentication" };
    }
  } catch (error) {
    console.error("❌ Auth callback error:", error);
    return { success: false, error: error.message };
  }
};

// Update user profile
export const updateUserProfile = async (updates) => {
  try {
    const response = await api.put("/api/user/profile", updates);
    return response.data;
  } catch (error) {
    console.error("Error updating profile:", error);
    throw error;
  }
};

// Get all users
export const getAllUsers = async () => {
  try {
    const response = await api.get("/api/user/all");
    return response.data;
  } catch (error) {
    console.error("Error fetching all users:", error);
    throw error;
  }
};

export default api;