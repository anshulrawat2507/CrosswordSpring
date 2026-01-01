# 🧩 Crossword Crafter - 3D Algorithmic Word Puzzle Game

Welcome to **Crossword Crafter**, a full-stack web application that dynamically generates and solves crossword puzzles using advanced algorithmic techniques. Features an immersive **3D game experience** powered by Three.js with animations, scoring system, hints, and victory celebrations!

Developed as part of the **Design and Analysis of Algorithms (TCS-409)** coursework, this project demonstrates the practical application of backtracking, Trie structures, BFS/DFS, and local search optimization strategies.

![Crossword Crafter](https://img.shields.io/badge/React-18.x-61DAFB?logo=react) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?logo=springboot) ![Three.js](https://img.shields.io/badge/Three.js-3D-black?logo=threedotjs) ![Java](https://img.shields.io/badge/Java-23-ED8B00?logo=openjdk)

## 📌 Project Overview

Crossword puzzles have long challenged players with language, logic, and pattern recognition. Our project moves beyond static puzzle design to **dynamically generate and solve crosswords** using efficient algorithms and optimized data structures.

This innovative solution showcases real-world use of algorithms in game-based systems and pushes the boundaries of NP-hard problem-solving using modern tech stacks.

## 🎮 Live Demo

- **Frontend**: `http://localhost:5173`
- **Backend API**: `http://localhost:8081/api/crossword`

## 🚀 Features

### Core Features
- ✅ Dynamic grid generation with optimal word placement
- ✅ Clue validation and automatic solver using backtracking
- ✅ Trie-based fast dictionary lookup (511 words with clues)
- ✅ Full-stack architecture with Spring Boot and React.js
- ✅ H2 in-memory database (no external DB setup required!)
- ✅ REST API backend with Axios communication

### 🎮 3D Game Experience (NEW!)
- 🎲 **Three.js 3D Grid** - Immersive 3D crossword puzzle with realistic lighting
- ✨ **Cell Animations** - Hover effects, correct/incorrect feedback with colors
- ⏱️ **Timer System** - Track your solving time
- 🏆 **Score System** - Points with streak multipliers (up to 5x combo!)
- 💡 **Hint System** - Get hints when stuck (limited per puzzle)
- 📊 **Progress Bar** - Visual completion tracking
- 🎉 **Victory Modal** - Confetti celebration on completion!
- 🤖 **Watch Solver** - Watch the AI solve the puzzle step-by-step
- 🔄 **2D/3D Toggle** - Switch between classic and 3D modes

### UI/UX Enhancements
- 🌈 Gradient themes with purple/blue color scheme
- 🎨 Framer Motion animations throughout
- 📱 Responsive design for all screen sizes
- 🔊 Visual feedback for correct/incorrect letters

---

## 🧱 Tech Stack

**Frontend**:
- React.js 18.x
- Three.js / @react-three/fiber / @react-three/drei
- Framer Motion (animations)
- Canvas Confetti (victory celebration)
- Tailwind CSS
- Axios

**Backend**:
- Java 23 with Spring Boot 3.5.0
- Spring Web, JPA, Jackson
- H2 In-Memory Database
- Server-Sent Events (SSE) for real-time solver

**Architecture**:
- Clear separation of concerns with Controllers, Services, and Models
- RESTful APIs using JSON for frontend-backend communication
- Context-based state management in React
- Component-based 3D scene architecture

## 🧠 Algorithms Used

- **Backtracking** for word placement and solving
- **DFS / BFS** for clue navigation and validation
- **Trie** for efficient dictionary operations
- **Grid Optimization** to maximize intersections and reduce empty space
- **Intersection Algorithm** for placing words with shared letters

---

## 🏃 Quick Start

### Prerequisites
- Java 23+
- Node.js 18+
- npm or yarn

### Backend Setup
```bash
cd CrosswordFrontendBackend/crossword-solver
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8081`

### Frontend Setup
```bash
cd CrosswordFrontendBackend/frontend/frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/crossword/puzzle` | Get a random crossword puzzle |
| POST | `/api/crossword/generate` | Generate a new puzzle |
| GET | `/api/crossword/solve` | SSE stream for watching solver |

---

## 📊 Team Information

**Team Name**: Divide and Conquer  
*Inspired by the classic algorithmic paradigm.*

| Member | Role | University ID | Email |
|--------|------|---------------|-------|
| **Mayank Singh Rawat** | Team Lead, Backend Lead | 23011119 | [mayanksinghrawat.23011119@gehu.ac.in](mailto:mayanksinghrawat.23011119@gehu.ac.in) |
| **Anshul Rawat** | UI Developer, Spring Boot Configuration | 230114762 | [anshulrawat.230114762@gehu.ac.in](mailto:anshulrawat.230114762@gehu.ac.in) |
| **Krishiv Mohan** | Frontend Developer & State Manager | 23011033 | [krishivmohan.23011033@gehu.ac.in](mailto:krishivmohan.23011033@gehu.ac.in) |
| **Aditya Kapruwan** | Grid Generator & Clue Validator | 23011050 | [adityakapruwan.23011050@gehu.ac.in](mailto:adityakapruwan.23011050@gehu.ac.in) |

---

## 📦 Deliverables

- ✅ Crossword Grid Engine with dynamic word fitting
- ✅ React.js-based interactive crossword frontend
- ✅ Spring Boot backend with API-based clue validation
- ✅ H2 In-Memory database (zero configuration!)
- ✅ Fully integrated frontend-backend communication using REST
- ✅ 3D Game mode with Three.js
- ✅ Score, timer, hints, and victory celebration
- ✅ Watch Solver feature with real-time SSE
- ✅ Responsive UI and clue interaction features

---

## 📸 Screenshots

### 🎮 3D Game Mode
*Immersive Three.js powered crossword experience*

### 🏠 Home Page
*Animated hero section with floating 3D letters*

### 🏆 Victory Modal
*Confetti celebration on puzzle completion*

---

## 🔗 Original Repository

This project was originally hosted in the team leader's repository:  
🔗 [https://github.com/MayankisG/CrosswordSpring](https://github.com/MayankisG/CrosswordSpring)

---

## 📄 License

This project is developed for educational purposes as part of TCS-409 coursework.

---

Made with ❤️ by Team Divide and Conquer


