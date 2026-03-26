<h1 align="center" style="font-size:40px;">
 🛡️ SmartSure Insurance Microservices System 
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/SpringBoot-3.x-brightgreen?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/SpringCloud-Gateway-red?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-Security-orange?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
  <img src="https://img.shields.io/badge/Eureka-Discovery-purple?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Zipkin-Tracing-yellow?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/JUnit5-Testing-success?style=for-the-badge&logo=junit5&logoColor=white"/>
</p>

----

# 📖 Project Overview

<p align="justify">

**SmartSure Insurance Management System** is an advanced, enterprise-grade backend platform designed to completely digitize the insurance lifecycle using a scalable microservices architecture.

Customers can register, purchase customized insurance policies, calculate dynamic premiums, upload physical claim documents securely, and initiate insurance claims through secure REST APIs. 

Administrative users can manage active insurance products, verify physical claim documentation, approve or reject lifecycle claims, and generate operational analytics reports.

The ecosystem is built upon independent **Spring Boot 3** microservices, centrally orchestrated by **Spring Cloud Gateway** for strict API routing. Each module maintains isolated state structures and local databases, communicating asynchronously via REST APIs and **OpenFeign** clients, while maintaining robust **Micrometer & Zipkin** distributed tracing.

</p>

<p align="center">
  <img src="https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExcmpweXg2emk4c2J6bjBpcmZ1NXMwc2Y2ZHIzZWR5dzg2eHZkaWFtNyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/K3htdZ1XuVWVD5DZDZ/giphy.gif" width="420" style="border-radius: 10px;"/>
</p>

---

## 🏛️ System Architecture (High Level Design)

```mermaid
graph TD

U[User / Admin]
P[API Client]

G[Spring Cloud Gateway<br>Gateway Filter]

A[Auth Service<br>JWT & Identity]
S[Policy Service<br>Purchase & CRUD]
C[Claims Service<br>Uploads & Review]
AD[Admin Service<br>OpenFeign Facade]

E[Netflix Eureka<br>Service Discovery]
Z[Zipkin Server<br>Distributed Tracing]

DB[(MySQL Relational DB)]
FS[(Local File Storage)]

U --> P
P --> G

G --> A
G --> S
G --> C
G --> AD

AD -->|Feign Client| C
AD -->|Feign Client| S

A --> DB
S --> DB
C --> DB

C --> FS

A -.-> E
S -.-> E
C -.-> E
AD -.-> E
G -.-> E

G -.-> Z
A -.-> Z
S -.-> Z
C -.-> Z
AD -.-> Z

classDef gateway fill:#ffcfdf,stroke:#ff9a9e,stroke-width:2px;
class G gateway;
classDef service fill:#d4fc79,stroke:#96e6a1,stroke-width:2px;
class A,S,C,AD service;
```

---

# 🔄 Claim Lifecycle (Business Flow)

```mermaid
stateDiagram-v2
    [*] --> UPLOADED
    UPLOADED --> SUBMITTED : Proof Attached
    SUBMITTED --> UNDER_REVIEW : Admin Assigned
    UNDER_REVIEW --> APPROVED
    UNDER_REVIEW --> REJECTED
    APPROVED --> CLOSED
    REJECTED --> CLOSED
```

---

# 🔐 Authentication Flow (JWT Security)

```mermaid
sequenceDiagram
    participant User
    participant Gateway
    participant AuthService
    participant ClaimsService

    User->>AuthService: Login Request (Credentials)
    AuthService->>User: Signed JWT Token Generated

    User->>Gateway: API Request + Bearer JWT
    Gateway->>AuthService: Validate & Extract Claims
    AuthService-->>Gateway: Validation Verified

    Gateway->>ClaimsService: Forward Authenticated Request
    ClaimsService-->>User: Secure Response
```

---

# 👥 Roles & Permissions

## 🟢 Customer Focus
* Secure Login & receive JWT validation tokens.
* Execute policy purchases and bindings.
* Interrogate personal policy statuses.
* Upload physical claim validation documents `(multipart/form-data)`.
* Initiate official claim processing routines.
* Track lifecycle status of active claims in real-time.

## 🔴 Administrative Focus
* Create, update, and deprecate system Policies.
* Review all active system claims and associated `uploads/` files.
* Authoritatively Approve or Reject claims modifying their downstream state.
* Aggregate and generate cross-service operational reports.
* Override general customer operational boundaries securely.

---

# 🛠️ Tech Stack & Technologies

| Category | Technology |
|---|---|
| **Core Framework** | Java 21, Spring Boot 3.x |
| **Microservices Cloud** | Spring Cloud Gateway, Netflix Eureka |
| **Communication** | REST APIs, OpenFeign Clients |
| **Security & Auth** | Spring Security, JSON Web Tokens (JWT) |
| **Observability** | Micrometer, Zipkin (Distributed Tracing) |
| **Testing** | JUnit 5, Mockito, Spring WebMvcTest |
| **Database** | MySQL (Isolated DBs), Spring Data JPA |
| **Tooling** | Maven, Lombok, Swagger / OpenAPI 3.0 |

---

# 📁 Microservices Structure

```bash
SmartSure-Insurance/
│
├── api-gateway/       # Port 8080 : Handles Routing & Authentication Filters
├── auth-service/      # Port 8081 : Security Contexts, JWT logic & Registration
├── policy-service/    # Port 8082 : Insurance Product Offerings & Purchasing
├── claims-service/    # Port 8083 : Multipart File Storage & Lifecycle Routing
├── admin-service/     # Port 8084 : Aggregation Facade & OpenFeign Integrations 
├── eureka-server/     # Port 8761 : Local Service Registry & Discovery
```

---

# 📡 Key API Endpoints

### 🔑 Auth Service
* `POST /api/auth/register` - Register a Customer/Admin identity.
* `POST /api/auth/login` - Authenticate and yield a signed token.

### 📜 Policy Service
* `GET /api/policies` - (Public) List all active policies.
* `POST /api/policies/purchase` - (Customer) Formally purchase an active policy.
* `POST /api/admin/policies` - (Admin) Inject a new valid policy into the market.

### 📋 Claims Service
* `POST /api/claims/upload` - (Customer) Upload a physical `PDF` / `Image` to local file storage.
* `POST /api/claims/initiate` - (Customer) Link an upload ID to a formal system claim.
* `GET /api/claims/internal/claims` - (Internal) OpenFeign fetching routes bypassing external security configurations.

---

# 🔬 Quality Assurance & Testing Strategy

To ensure enterprise-level code quality, strict testing paradigms are heavily utilized within every microservice boundary.

* **✅ Unit Testing (`@MockBean`)**: Isolated `@Service` business-logic validation using **JUnit 5** and **Mockito** to strictly mock database calls and secondary dependencies without booting a heavy context.
* **✅ Controller Integration (`MockMvc`)**: `MockMvcBuilders` are utilized across all modules mimicking raw HTTP protocol transactions ensuring Servlet Exceptions, JSON serializers, and headers intercept correctly.
* **✅ API Definition**: Fully documented interactive GUI visualizations mapped over **Swagger / OpenAPI 3.0**.
* **✅ Lifecycle Flow**: Advanced postman scripting evaluating multipart mappings and token interceptions dynamically.

---

# 🔍 Observability & Distributed Tracing

This project features a fully capable **Micrometer Tracing** bridge configured to export telemetry data natively to **Zipkin**. 
This establishes complete visual visibility tracking a single origin request from the `api-gateway` traversing down to `admin-service` invoking OpenFeign networks internally directly within a live visual dashboard.

---

# 📊 End-to-End System Flow

```mermaid
flowchart LR

A[Login] --> B[JWT Handshake]
B --> C[API Gateway Filter]
C --> D[Multipart File Upload]
D --> E[Claim Struct Initialization]
E --> F[Admin Feign Aggregation]
F --> G{Decision Boundary}
G -->|Accepted| H[Approve Route]
G -->|Denied| I[Reject Route]
H --> J[Tracing Captured -> Zipkin]
I --> J
```

---

# 🏆 Key Architectural Solutions Demonstrated

* **Distributed Authentication**: Intercepting headless JWT tokens reliably using custom Spring Cloud Gateway filter mappings.
* **Service Networking**: Eliminating static routing boundaries by implementing dynamic Eureka DNS lookup variables.
* **Synchronous Aggregation**: Leveraging Spring OpenFeign to establish Internal-only controller endpoints strictly utilized server-to-server.
* **Unstructured Input Management**: Handling raw `multipart/form-data` inside distributed nodes seamlessly translating byte-streams into physical OS `/uploads`.
* **Deep System Transparency**: 100% Probability Sampling routing into Zipkin via Micrometer allowing millisecond bottleneck analysis across remote instances.
* **Strict Unit Coverage**: Validating internal methods systematically assuring continuous deployment integrity without DB dependencies.

---

# 🚀 Future Enhancements

* React-based frontend dashboard visualization.
* Advanced Docker containerization & orchestration mappings.
* Message Queues (RabbitMQ/Kafka) for asynchronous notification deliveries.
* Cloud Provider deployment routing (AWS EC2 / Render).

---

# 📄 Academic License & Acknowledgement

This project is expertly constructed as part of a **Capgemini Spring Boot Microservices Evaluation Program.**
It operates as a demonstration and evaluation architecture designed for strict academic and simulated organizational environments.

You are free to:
- View and audit the modular code configurations.
- Utilize architectural design choices for practice and learning.
- Reference internal dependencies in comparable environments.

⚠️ You are NOT allowed to:
- Copy or clone this system for your own direct academic assessments.
- Monetize these endpoints for immediate commercial viability without express engineering permission.

---

# © Copyright

**© 2026 SmartSure Insurance System**  

Engineered structurally under the **Capgemini Spring Boot Microservices Evaluation Program**.  
*All engineering rights reserved.*

---

**Final Note:**  
*This implementation is designed to reflect best-practice engineering standards prevalent in scalable Fortune 500 tech stacks today.*
