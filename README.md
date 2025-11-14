# ![AntaShop Banner](https://dummyimage.com/1200x250/111/fff&text=AntaShop+E-Commerce+Platform)

<p align="center">
  <strong>E-Commerce Platform for Shoes & Fashion</strong><br>
  Spring Boot Microservices • MySQL • JDK 21 • Docker • React Frontend
</p>

<p align="center">
  <img src="https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/java-21-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/spring--boot-3.x-6DB33F?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/mysql-8.0-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/maven-4.0.0-red?style=for-the-badge"/>
</p>

---

# 🛍️ **AntaShop – E-Commerce Microservice Platform**

**AntaShop** is a modern e-commerce system designed for selling **shoes and fashion items**, built with a clean and scalable **Microservice Architecture**.

The project is developed using **IntelliJ IDEA**, **Spring Boot**, **MySQL**, **Redis**, and **RabbitMQ**, applying real-world backend development practices.

### 🎯 Project Goals
• Build a scalable and maintainable e-commerce backend  
• Apply microservices for real business workflows  
• Improve API development, caching, authentication, and async messaging  
• Separate independent services for easy scaling and deployment  

---

# 🏗️ **System Architecture**

Backend services included in the system:

• **Identity Service** – JWT authentication, login & registration  
• **Product Service** – Product CRUD and stock management  
• **Category Service** – Category and product grouping management  
• **Cart Service** – User shopping cart operations  
• **Order Service** – Order creation and tracking  
• **Payment Service** – Payment workflow (structure ready)  
• **Notification Service** – Email OTP & notifications  
• **Cloud Service** – Image upload and media management  

Frontend:  
• **React + Vite** modern web application  

---

# 🛠️ **Tech Stack**

## Backend
• Java 21  
• Spring Boot 3.x  
• Spring Web  
• Spring Data JPA  
• Spring Security + JWT  
• Redis Cache  
• RabbitMQ Messaging  
• Maven  

## Database
• MySQL 8.0  

## Frontend
• React.js  
• Vite  
• TailwindCSS / SCSS  

## DevOps
• Docker  
• Docker Compose  
• Swagger / OpenAPI  

---

# 📂 **Project Structure**

```
AntaShop/
│── services/
│   ├── cart-service/
│   ├── category-service/
│   ├── cloud-service/
│   ├── identity-service/
│   ├── notification-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── product-service/
│
└── README.md
```

---

# 🚀 **Getting Started**

## ⭐ Prerequisites
• JDK 21  
• Maven 4.x  
• MySQL 8  
• Node.js (for the frontend)  
• Redis (optional)  
• Docker (optional)

---

## 🔧 Backend Setup

Clone repository:
```bash
git clone https://github.com/your-repo/AntaShop.git
cd AntaShop
```

Configure your environment in:
```
src/main/resources/application.yaml
```

Build the backend:
```bash
mvn clean install
```

Run a service:
```bash
mvn spring-boot:run
```

Open Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

# 🐳 **Run with Docker**

If Docker Compose is configured:
```bash
docker-compose up --build
```

---

# 📌 **Core Features**

• Secure JWT authentication  
• Product CRUD operations  
• Category management  
• Shopping cart functionality  
• Order placement & tracking  
• Email notifications (OTP & system alerts)  
• Payment service structure  
• RabbitMQ microservice communication  
• Redis caching for performance  

---

# 📘 **API Documentation Template**

### Auth
```
POST /api/auth/register  
POST /api/auth/login
```

### Products
```
GET /api/products  
POST /api/products  
PUT /api/products/{id}  
DELETE /api/products/{id}
```

### Orders
```
POST /api/orders  
GET /api/orders/user/{userId}
```

*(Expand this section with your actual API list.)*

---

# 👥 Team

### 👨‍💻 Leader  
• **Nguyễn Bá Viên**  
• 📧 **nguyenbavien.26092005@gmail.com**

### 👥 Members  
• **Nguyễn Danh Thao**  
• **Trần Thiên Phú**


---

# 📜 **License**
This project is for **educational and practice use only** and not intended for commercial deployment.
