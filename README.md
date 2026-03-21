<h1 align="center"> SmartSure Insurance Microservices System</h1>

<p align="center">
A Spring Boot Microservices backend project built to understand real-world system design
</p>

<p align="center">
This project demonstrates authentication, API Gateway routing, service discovery, and secure policy management
</p>

---

<p align="center">
<img src="https://img.shields.io/badge/Java-21-blue" />
<img src="https://img.shields.io/badge/SpringBoot-Microservices-brightgreen" />
<img src="https://img.shields.io/badge/JWT-Security-orange" />
<img src="https://img.shields.io/badge/MySQL-Database-blue" />
<img src="https://img.shields.io/badge/Eureka-ServiceDiscovery-red" />
</p>

---

<p align="center">
<img src="https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExMWZ0ZG4wemJ4eTBmY2VmbWRmdG1iaHlpZTM4YWo4d3B3ZzdwbWZ2OSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/78XCFBGOlS6keY1Bil/giphy.gif" width="70%" />
</p>

---

## 🧠 About This Project

I built this project to understand how real backend systems work.

Instead of a single monolithic application, I split everything into microservices and connected them using Spring Cloud tools.

This helped me understand how authentication, routing, and service communication works in real production systems.


<p align="center">
<img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExaTdidXV3ZDB2ZXRxaG0xa2ViNms1NG8yOGgwMXZrdzNyZXZjbmlkdiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/12W5Sg2koWYnwA/giphy.gif" width="70%" />
</p

---

## 🏗️ Architecture

<pre style="background:#0d1117; color:#00ffcc; padding:15px; border-radius:10px;">
CLIENT
  |
  v
API GATEWAY (8080)
  |
  |-------------------|
  |                   |
AUTH SERVICE     POLICY SERVICE
  |                   |
JWT AUTH          MySQL DB
  |
EUREKA SERVER
</pre>

---

<p align="center">
<img src="https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExdzBjbHI4eG0ydTkxYzNrenJka240MXV1cXRyd3Zmd3FreDd6NTZheSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/zkRQ24mPZ1HvHj9pZ6/giphy.gif" width="50%" />
</p>

---

## ⚙️ Tech Stack

- Java 21  
- Spring Boot  
- Spring Security  
- JWT Authentication  
- Spring Cloud (Eureka + API Gateway)  
- MySQL  
- Maven  

<p align="center">
<img src="https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExbnJld3lreG1ydzg1YW40Z3ZucDI3enltdDNhM21vaGFhY25sYmZ4dCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/M3WRGRcGtJrFXMTZX5/giphy.gif" width="50%" />
</p>


---

## 🔐 Authentication Flow

1. User logs in via Auth Service  
2. JWT token is generated  
3. Token is sent in request header  
4. Policy Service validates token  
5. Role-based access is applied  

<p align="center">
<img src="https://media.giphy.com/media/QNFhOolVeCzPQ2Mx85/giphy.gif" width="80%" />
</p>

---

## 🌐 API Gateway Role

- Single entry point for all requests  
- Routes requests to correct microservice  
- Handles centralized security flow  


<p align="center">
<img src="https://media.giphy.com/media/3o7TKtnuHOHHUjR38Y/giphy.gif" width="40%" />
</p>

---

## 📌 API Endpoints

### Auth Service
- POST `/auth/register`  
- POST `/auth/login`  

### Policy Service
- POST `/policies`  
- GET `/policies`  
- GET `/policies/{id}`  
- DELETE `/policies/{id}` 

<p align="center">
<img src="https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExc3p6c3J4N3ZiNGh4aWprM3gxZXZtZXEwZjJsZmN3Z3V3anBmZ3JzMCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/iFiPHGTAGJQXHOaXcu/giphy.gif" width="60%" />
</p>

---

## 🧪 Testing Flow

- Register user  
- Login and get JWT token  
- Add token in Authorization header  
- Access secured APIs via Gateway
  
<p align="center">
<img src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExejM3NnAxdms1OXh4eGdkNmhuM3Y0bGMxYnd2NTdlY293ZGdtdm9pNiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/FfBoY4A3gMC9oetfU4/giphy.gif" width="60%" />
</p>

---

## ⚠️ Challenges Faced

- 401 Unauthorized errors  
- Spring Security filter issues  
- Eureka service registration confusion  
- API Gateway routing problems  

Fixing these helped me understand microservices deeply.

<p align="center">
<img src="https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExY2M2d212aDMzdXhiNWZ1MzZxZGdkenZiaDk2NGs0bDZoZ3FzbzgyNCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/Hu1EWP7aKFJKKTuIYF/giphy.gif" width="40%" />
</p
  
---

## 🚀 Future Improvements

- Add Customer module  
- Add policy purchase flow  
- Add frontend (React dashboard)  
- Add logging + monitoring  
- Docker deployment  

<p align="center">
<img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExdnNqeXVsdmYzaG02ZXN1dnF4MzVqMmRhdWJoc3BsbjR0a240ZDBlNiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/qy1VAsXjWNR6ZD6HBG/giphy.gif" width="40%" />
</p
  

---

## 💭 Final Thoughts

This project helped me understand real-world backend architecture beyond basic Spring Boot applications.

It was a big step in my learning journey.

<p align="center">
<img src="https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExOXYzbWRnOXV3OTR1ZDQ3bGIxc2s1dnl6OGs1ZGl2eDltdHkwcnl5dCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/kRUvGywYREgS3JYnib/giphy.gif" width="40%" />
</p
---



<p align="center">
Thanks for checking this project 
</p>
