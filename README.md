# ShortVibe - Backend

**ShortVibe** is a secure and scalable URL shortener backend built using **Java**, **Spring Boot**, and **PostgreSQL**. It provides robust APIs for URL shortening, redirection, authentication, and real-time analytics.

## 🚀 Tech Stack

- **Java 8+**
- **Spring Boot**
- **Spring Security (JWT Auth)**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker**
- **REST APIs**

---

## 🔐 Key Features

- User registration and login with **JWT-based authentication**
- **Role-based access control**
- Shorten any long URL to a custom short link
- Analytics tracking: click counts, timestamps
- Subdomain and path-based redirection
- Exception handling and validation
- Dockerized and production-ready

---

## 🎯 API Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/auth/register`    | Register a new user |
| POST   | `/api/auth/login`       | Authenticate and receive a JWT token |
| POST   | `/api/url/shorten`      | Shorten a long URL |
| GET    | `/api/url/{shortId}`    | Redirect to the original URL |
| GET    | `/api/url/analytics`    | View analytics for shortened URLs |

---

## ⚙️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/ShortVibe-Backend.git
cd ShortVibe-Backend
