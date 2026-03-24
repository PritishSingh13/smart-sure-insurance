

<h1 align="center" style="font-size:40px;">
 SmartSure Insurance Microservices System 
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/SpringBoot-Microservices-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/SpringCloud-Gateway-red?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/JWT-Security-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Eureka-Service%20Discovery-purple?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge"/>
</p>


---

#  Project Overview

SmartSure Insurance Management System is a microservices-based backend platform designed to digitize the complete insurance lifecycle.

Customers can register, purchase insurance policies,

calculate premiums, upload claim documents, and initiate insurance claims through secure REST APIs.

Administrative users manage insurance products, verify claim documentation, approve or reject claims, and generate operational reports.

The system is built using Spring Boot microservices, with Spring Cloud Gateway acting as the API gateway for routing requests to backend 

services. Each microservice maintains its own 

database and communicates with other services through REST APIs or OpenFeign clients,Front End REACT or Angular.



<p align="center">
  <img src="https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExcmpweXg2emk4c2J6bjBpcmZ1NXMwc2Y2ZHIzZWR5dzg2eHZkaWFtNyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/K3htdZ1XuVWVD5DZDZ/giphy.gif" width="420"/>
</p>

---

##  System Architecture (High Level Design)

```mermaid
graph TD

U[User Customer/Admin]
P[Postman Client]

G[API Gateway Spring Cloud Gateway]

A[Auth Service JWT]
S[Policy Service]
C[Claims Service]

E[Eureka Server]

DB[(MySQL Database)]
FS[(File Storage uploads)]

U --> P
P --> G

G --> A
G --> S
G --> C

A --> DB
S --> DB
C --> DB

C --> FS

A --> E
S --> E
C --> E
G --> E
```

---

#  Claim Lifecycle (Business Flow)

```mermaid
stateDiagram-v2
    [*] --> UPLOADED
    UPLOADED --> SUBMITTED
    SUBMITTED --> UNDER_REVIEW
    UNDER_REVIEW --> APPROVED
    UNDER_REVIEW --> REJECTED
    APPROVED --> [*]
    REJECTED --> [*]
```

---

#  Authentication Flow (JWT Security)

```mermaid
sequenceDiagram
    participant User
    participant Gateway
    participant AuthService
    participant ClaimsService

    User->>AuthService: Login Request
    AuthService->>User: JWT Token Generated

    User->>Gateway: Request + JWT Token
    Gateway->>AuthService: Validate Token
    AuthService-->>Gateway: Valid

    Gateway->>ClaimsService: Forward Request
    ClaimsService-->>User: Response
```

---

#  Roles in System

##  Customer 
- Login & receive JWT token
- Can Purchase Policy
- View the policy 
- Upload claim documents (PDF/Image)  
- Track claim status  

##  Admin
- Create Policy
- Delete Policy
- View the Policy
- Approve / Reject claims  
- Monitor system activity  

---

#  Tech Stack

-  Java 21  
-  Spring Boot  
-  Spring Security + JWT  
-  Spring Cloud Gateway  
-  Eureka Service Discovery  
-  MySQL  
-  Maven  
-  Swagger API Docs  
-  Postman Testing  

---

#  Microservices Structure

```bash
SmartSure-Insurance/
│
├── api-gateway
├── auth-service
├── policy-service
├── claims-service
├── eureka-server
```

---

#  API Endpoints

##  Auth Service
- POST `/auth/register`
- POST `/auth/login`

##  Policy Service
- POST `/policies`
- GET `/policies`
- GET `/policies/{id}`
- DELETE `/policies/{id}`

##  Claims Service
- POST `/api/claims/upload`
- POST `/api/claims/initiate`
- GET `/api/claims/status/{id}`
- GET `/api/claims`
- PUT `/api/claims/admin/review/{id}`

---

#  File Upload System

- Supports PDF & Image uploads  
- Stored in local file system (`uploads/`)  
- Linked with claim records in DB  
- Used during claim submission lifecycle  

---

#  Testing Strategy

## ✔ Swagger UI
- API visualization & testing

## ✔ Postman
- End-to-end microservice testing  
- JWT authentication validation  
- File upload (multipart) testing  
- Full claim workflow testing  

---

#  End-to-End System Flow

```mermaid
flowchart LR

A[Login] --> B[JWT Token]
B --> C[API Gateway]
C --> D[Upload Claim Document]
D --> E[Claim Submitted]
E --> F[Admin Review]
F --> G{Decision}
G --> H[Approved]
G --> I[Rejected]
H --> J[Report Generated]
I --> J
```

---

#  Key Challenges Solved

- JWT authentication across microservices  
- API Gateway routing & filter chain issues  
- Eureka service registration & discovery  
- File upload handling in distributed system  
- End-to-end workflow consistency  

---

#  Future Enhancements

-  React-based frontend dashboard  
-  Email notifications (claim updates)  
-  Docker containerization  
-  Logging & monitoring system  
-  Cloud deployment (AWS / Render)  

---

#  What This Project Demonstrates

- Microservices architecture design  
- Secure authentication (JWT)  
- Real-world workflow simulation  
- Distributed system communication  
- Backend system design thinking  

---

#  Final Note

This project is a **production-style backend simulation** designed to understand how scalable enterprise systems are built using microservices architecture.

It reflects real-world backend engineering concepts used in modern companies.

---
