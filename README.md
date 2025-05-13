Seckill is a full-stack online shopping website designed to provide a seamless e-commerce experience.

Feature: Handle the high concurrency requests and large flows, oversell, overbuy and overdue payment.

Front-End:
Built with HTML, JavaScript, CSS, and Thymeleaf, the interactive user interface allows customers to browse products, manage their shopping carts, and place orders. 

Back-End:
Developed using Spring Boot to create a robust RESTful API handling all business logic and security requirements. 

Functions: user management, order and purchase process, and stock management.

Database & Performance:

MyBatis is used for database access (SQL Server) covering authority, customer, product, seckill activity and sales order data.

Redis caching optimizes performance under high-concurrency loads.

DB Optimistic Locking and Index Optimization ensure consistency and efficiency.

Scalability & Reliability:

RocketMQ enables decoupled order processing for peak load handling, also using RocketMQ delayMessage to handle the overdue payment.

Automated API tests are implemented using Karate for fast, reliable deployments.

The website can is deployed on Azure with Docker and Kubernetes for containerization and load balancing.

This project emphasizes security, performance optimization, and scalability, providing a solid foundation for a production-ready online shopping platform.
