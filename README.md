🌐✨ AntaShop – E-Commerce Microservices Platform

A modern, scalable, and modular backend system for online fashion retail.
Designed with Spring Boot Microservices, optimized for performance & real-world production use.

<div align="center">
🔥 Microservices Architecture • 🛒 E-Commerce Engine • ☕ Java 21 • 🧵 RabbitMQ • 🚀 Docker Ready
</div>
📖 Introduction

AntaShop is a complete backend platform for an e-commerce system focused on shoes, clothing, and accessories.
Built using Spring Boot, Microservices, and modern cloud-ready design, the system provides clean domain separation, high scalability, and flexibility for integration with any frontend (React, Next.js, Flutter…).

🔍 Key objectives:

Create a modular e-commerce system separated into independent microservices.

Apply real-world engineering concepts: asynchronous messaging, secure authentication, REST API standards.

Support growth in traffic with caching, message queues, and distributed architecture.

Provide a strong backend foundation for a large-scale e-commerce project.

🏗️ System Architecture

Below is the complete ecosystem of AntaShop:

/services
 ├── identity-service        → Authentication, JWT, user management
 ├── product-service         → Products, attributes, inventory
 ├── category-service        → Category trees, filters
 ├── cart-service            → Shopping cart, Redis caching
 ├── order-service           → Orders, delivery, workflows
 ├── payment-service         → Payment flow, transactions
 ├── notification-service    → Email, OTP, async events (RabbitMQ)
 └── cloud-service           → Image/file uploads

🗺️ High-Level Architecture Diagram
flowchart LR
    A[Frontend<br/>(React / NextJS / Mobile)] -->|REST API| B(API Gateway - optional)

    B --> C1[Identity Service]
    B --> C2[Product Service]
    B --> C3[Category Service]
    B --> C4[Cart Service]
    B --> C5[Order Service]
    B --> C6[Payment Service]
    B --> C7[Notification Service]
    B --> C8[Cloud Service]

    C7 <-->|Asynchronous Events| R[(RabbitMQ)]
    C4 -->|Caching| D[(Redis)]
    C1 -->|User DB| M1[(MySQL)]
    C2 -->|Products DB| M2[(MySQL)]
    C5 -->|Orders DB| M3[(MySQL)]

🛠️ Tech Stack
🌍 Backend
Tech	Purpose
Java 21	Modern Java features, high performance
Spring Boot 3	Core framework for all microservices
Spring Security + JWT	Authentication & authorization
Spring Data JPA	ORM & database operations
Maven	Dependency management
🗄️ Databases

MySQL (main relational DB)

Supports UTF8MB4 and InnoDB

⚡ Performance / Messaging

RabbitMQ → async events (email, orders, OTP)

Redis → caching for cart & performance boost

🐳 DevOps

Docker

Docker Compose

Environment-based configuration

🔐 Security Layer

The system includes enterprise-level security:

✔ JWT token authentication
✔ Role-based access control (Admin/User)
✔ Password hashing with BCrypt
✔ Secure route protection
✔ OTP email verification (via Notification Service)

🧠 Core Features
🛍️ E-Commerce

Product catalog & categories

Product variants (size, color…)

Cart operations (add/remove/update)

Order placement & tracking

Payment transaction flow

👤 User Management

Registration & login

Token-based authentication

Profile updates

📩 Notifications

Email sending

OTP codes

System alerts

RabbitMQ event-driven architecture

☁️ Cloud / Media

Image upload

Local or external cloud storage

File validation

🧪 Installation & Setup
🔧 Requirements

JDK 21

Maven 3.9+

MySQL 8+

RabbitMQ

Redis (optional)

▶️ Build the project
mvn clean install

▶️ Run any microservice
cd identity-service
mvn spring-boot:run

▶️ Run with Docker
docker-compose up --build

📁 Folder Structure Overview
AntaShop-Service
 ├── services
 │    ├── identity-service
 │    ├── product-service
 │    ├── category-service
 │    ├── cart-service
 │    ├── order-service
 │    ├── payment-service
 │    ├── notification-service
 │    └── cloud-service
 ├── README.md
 └── docker-compose.yml (optional)

🎯 Project Goals

Build a production-ready e-commerce backend.

Practice advanced microservice architecture.

Utilize Docker, Redis, RabbitMQ, and distributed design.

Create a backend prepared for future scaling.

🤝 Contributions

All contributions are welcome!
Submit a Pull Request or open an Issue if you want to propose new features or fixes.
