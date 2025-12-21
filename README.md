# 01-Blog

01Blog is a premium fullstack social blogging platform designed for developers and students to share their learning experiences, discoveries, and progress. The platform features a highly reactive frontend with a secure, robust backend.

## 🚀 Getting Started

### Docker Setup (Recommended)
1. Ensure Docker and Docker Compose are installed.
2. From the root directory, run:
   ```bash
   docker-compose up --build
   ```
   This will start the **database**, **backend**, and **frontend** services simultaneously.
   - Frontend: `http://localhost:4200`
   - Backend API: `http://localhost:8080`
   - Database: Port `5432`

### Manual Setup

#### Prerequisites
- **Java 21**
- **Node.js 20+**
- **Maven**
- **PostgreSQL**

#### 1. Backend
```bash
cd backend
# Update application.properties with your DB credentials
mvn spring-boot:run
```

#### 2. Frontend
```bash
cd frontend
npm install
npm run start
```
---

## 🛠️ Technologies Used

### Frontend
- **Angular 20**: Reactive state management with **Signals**.
- **SCSS / CSS Variables**: Robust design system with **Dark/Light Mode**.
- **Angular Material**: Premium UI components.

### Backend
- **Java 21 & Spring Boot 3.5**: Core API and security.
- **Spring Security & JWT**: Secure authentication.
- **PostgreSQL**: Relational database.
- **Bucket4j**: API rate limiting.

### Infrastructure
- **Docker & Docker Compose**: Automated container orchestration.
- **Nginx**: High-performance web server and API reverse proxy.

---

## ✨ Key Features

- **Reactive User Experience**: Instant updates across the app using Angular Signals.
- **System-Aware Theming**: Automatic adaptation to system dark or light mode preferences.
- **Secure Authentication**: Robust registration and login flows with JWT-based security.
- **Comprehensive Post Management**: Create, edit, and delete posts with various media types (Images/Videos).
- **Interactive Comments Section**: Real-time feedback with refined layout and character count validation.
- **Content Moderation**: User reporting system and advanced admin dashboard for platform safety.
- **Performance Optimized**: Built with performance and scalability in mind using best practices.

---

## 🔒 Security Features

- **Rate Limiting**: Protection against brute-force and DDoS attempts using Bucket4j.
- **Alignment of Validation**: Frontend and Backend share the same validation rules, providing immediate user feedback while maintaining server-side integrity.
- **Private Data Protection**: Hidden posts are strictly restricted and only visible to authorized administrators.

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
