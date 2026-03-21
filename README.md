SmartSure Insurance - Microservices Project

Hi, I built this project to understand how real backend systems work using microservices architecture.
Instead of building everything in a single monolithic app, I split the system into smaller services and connected them using Spring Boot and Spring Cloud.

This project helped me learn how authentication, service communication, and API routing actually work in real-world applications.

What this project does

This is a simple insurance system where:

Users can register and login

Admin can create insurance policies

Policies can be fetched and managed

All services communicate through a central API Gateway

Tech Stack

Java 21

Spring Boot

Spring Security

JWT Authentication

Spring Cloud (Eureka + Gateway)

MySQL

Maven

Architecture Overview

The system is divided into multiple services:

Auth Service → handles login, register, JWT

Policy Service → handles policy CRUD operations

Eureka Server → service registry

API Gateway → single entry point

How the flow works

When a request is made:

Client hits API Gateway

Gateway routes request to correct service

Auth Service generates JWT token

Policy Service validates token

Based on role (ADMIN), access is granted

Security

I implemented JWT-based authentication.

Token is generated at login

Token is passed in Authorization header

Policy Service verifies token

Only ADMIN can create policies

API Endpoints

Auth Service

POST /auth/register

POST /auth/login

Policy Service

POST /policies → create policy (ADMIN only)

GET /policies → get all policies

GET /policies/{id} → get policy by id

DELETE /policies/{id} → delete policy

Testing

I tested all APIs using Postman.

Flow I followed:

Register user

Login to get JWT token

Use token in Authorization header

Access protected APIs

Example Request

POST /policies

{
  "policyName": "Health Plus",
  "policyType": "Health",
  "premium": 5000,
  "duration": 12
}

What I learned

While building this project, I learned:

How microservices communicate

How API Gateway works

How Eureka service discovery works

How JWT authentication is implemented

How to secure APIs using roles

How real backend systems are structured

Challenges I faced

JWT filter errors and debugging

401 and 403 issues

Service registration issues in Eureka

Gateway routing confusion

But solving these gave me much better understanding.

Future Improvements

Add customer role features

Add policy purchase functionality

Add frontend (React)

Add logging and monitoring

Final Thoughts

This project really helped me move from basic Spring Boot to understanding how real scalable systems are designed.

Still learning, but this was a big step for me.

🙌 Thanks for checking this out

If you have suggestions or feedback, feel free to share.
