Seckill is a full-stack online shopping website designed to provide a seamless e-commerce experience, especially to handle the high concurrency requests and large flows.

Front-End:
Built with HTML, JavaScript, CSS, and React, the interactive user interface allows customers to browse products, manage their shopping carts, and place orders. 

Back-End:
Developed using Spring Boot to create a robust RESTful API handling all business logic and security requirements. Features include user management, order and purchase process, and stock management.

Database & Performance:

MyBatis is used for database access (SQL Server) covering authority, customer, product, and sales order data.

Redis caching optimizes performance under high-concurrency loads.

DB Optimistic Locking and Index Optimization ensure consistency and efficiency.

Scalability & Reliability:

RocketMQ enables decoupled order processing for peak load handling.

Automated API tests are implemented using Karate for fast, reliable deployments.

The system is deployed on Azure with Docker and Kubernetes for containerization and load balancing.

This project emphasizes security, performance optimization, and scalability, providing a solid foundation for a production-ready online shopping platform.
