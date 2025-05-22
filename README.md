Seckill is a full-stack online shopping website designed to provide a seamless e-commerce experience, especially for the activity of flash sell.

Feature: Handle the high concurrency requests and large flows, avoid the overselling, overbuy and overdue payment during flash sell.

Front-End:
Built with HTML, JavaScript, CSS, and Thymeleaf, the interactive user interface allows customers to browse products, manage their shopping carts, and place orders.
Using nginx and Thymeleaf template engin to enable page staticiization to handle the query request before the start of flash sell.  

Back-End:
Developed using Spring Boot to create a robust Restful API handling all business logic and security requirements. 
Functions: user management, order and purchase process, and stock management.
RocketMQ enables decoupled order processing for peak load handling, also using RocketMQ delayMessage to handle the overdue payment.

Database & Performance:
MyBatis is used for database access (SQL Server) covering authority, customer, product, seckill activity and sales order data.
Using Redis to optimize data query performance on all activity detail, commodity detail and available seckill activity under high-concurrency loadsRedis by enabling  cache preheating of seckill activity, commodity data from DB.
Using Redis set to enable buying restriction for users 
DB Optimistic Locking and Index Optimization ensure consistency and efficiency.

System Stability and High Reliability:
Rate limit & Flow Control: Using Sentinel to monitor the QPS of application traffic or the number of concurrent threads. When reaching the specified threshold,
    the flow is controlled to avoid the system being overwhelmed by instantaneous flow peaks, protecting the system from being crushed to ensure the high availability of the application.
Circuit-breaker：Using Sentinel to protect the overall availability of system by temporarily cutting off the invocation of downstream service which is malfunction.

Automated API tests are implemented using Karate for fast, reliable deployments.

The website can is deployed on Azure with Docker and Kubernetes for containerization and load balancing.

This project emphasizes security, performance optimization, and scalability, providing a solid foundation for a production-ready online shopping platform.
