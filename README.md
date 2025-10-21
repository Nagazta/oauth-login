# Sepulveda OAuth2 Login

**Spring Boot application integrating OAuth2 login with Google and GitHub, including user profile management and full CRUD support.**

---

## Overview

This project is a **Spring Boot application** that demonstrates:

- **OAuth2 authentication** using Google and GitHub.  
- **Automatic user registration** on first login.  
- **Session-based security** with Spring Security.  
- **Profile management** for authenticated users.  
- **Full CRUD operations** for managing users.  
- **In-memory H2 database** for development (supports MySQL/PostgreSQL in production).  


## Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring OAuth2 Client**
- **Spring Data JPA**
- **H2 Database** (in-memory)

## Frontend
- **React 18**
- **Vite**
- **React Router DOM**
- **Axios**


## 🚀 How to Run

### 1️. Clone the Repository
```bash
git clone https://github.com/yourusername/sepulveda-oauth.git
cd oauth2-login
```

---

### 2️. Backend Setup (Spring Boot)

#### Step 1: Navigate to Backend Directory
```bash
cd oauth2-login
```

#### Step 2: Configure OAuth2 Credentials
Edit `src/main/resources/application.properties`:

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=profile,email

# GitHub OAuth2
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
spring.security.oauth2.client.registration.github.scope=read:user,user:email

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update

#### Step 3: Build and Run
#### ✅ Backend Running
The backend should now be running at: **http://localhost:8080**

### 3️. Frontend Setup (React + Vite)

#### Step 1: Navigate to Frontend Directory
```bash
cd ../front-end
```

#### Step 2: Install Dependencies
```bash
npm install
# or
yarn install
```

#### Step 3: Run Development Server
```bash
npm run dev
# or
yarn dev
```

#### ✅ Frontend Running
The frontend should now be running at: **http://localhost:5173**