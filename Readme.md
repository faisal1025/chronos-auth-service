Chronos Job Scheduler System - README
# Chronos - Distributed Job Scheduler System

Chronos is a distributed microservices-based job scheduling and execution platform built using Spring Boot, Kafka, Docker, and MySQL.

The system allows users to:
- Register/Login users
- Create scheduled jobs
- Execute Email, HTTP, and Script jobs
- Run recurring and one-time jobs
- Execute jobs asynchronously using Kafka

---

# Architecture

The system contains 5 microservices:

| Service | Description |
|---|---|
| AuthService | Authentication + API Gateway |
| UserService | User Management |
| JobService | Job CRUD & Scheduling Metadata |
| SchedulerService | Polls DB and pushes due jobs to Kafka |
| ExecutorService | Consumes Kafka jobs and executes them |

---

# Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Cloud Gateway MVC
- Spring Data JPA
- MySQL
- Apache Kafka
- Docker & Docker Compose
- WebClient
- MailTrap SMTP

---

# Microservice Communication

- API Gateway routes requests to internal services
- SchedulerService polls jobs every 2 seconds
- Due jobs are published to Kafka topic:
  
```text
job_to_execute
ExecutorService consumes Kafka events and executes jobs
Supported Job Types
1. EMAIL

Sends emails using SMTP.

Example:

{
  "type": "EMAIL",
  "toEmail": "test@test.com",
  "subject": "Reminder",
  "body": "Meeting at 5PM"
}
2. HTTP

Executes HTTP requests using WebClient.

Example:

{
  "type": "HTTP",
  "url": "https://jsonplaceholder.typicode.com/posts",
  "method": "POST",
  "body": "{\"title\":\"test\"}"
}
3. SCRIPT

Executes shell commands.

Example:

{
  "type": "SCRIPT",
  "command": "echo hello"
}
Docker Images

Docker Hub Images:

faisal1025/auth-service-api:latest
faisal1025/user-service-api:latest
faisal1025/job-service-api:latest
faisal1025/scheduler-api:latest
faisal1025/executor-api:latest

Required Software
Docker Desktop
Docker Compose
Project Setup
1. Clone Repository
git clone <repository-url>
cd chronos
2. Create .env

Create a .env file in project root.

Example:

MYSQL_ROOT_PASSWORD=root

AUTH_DB=chronos_auth
JOB_DB=chronos_job

DB_USERNAME=root
DB_PASSWORD=root

AUTH_SERVICE_PORT=8080
USER_SERVICE_PORT=8081
JOB_SERVICE_PORT=9091
SCHEDULER_SERVICE_PORT=9000
EXECUTOR_SERVICE_PORT=9001

JWT_SECRET_KEY=change-me-change-me-change-me-change-me

KAFKA_SERVER=<your-confluent-server>

KAFKA_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username='<username>' password='<password>';

KAFKA_PRODUCER_CLIENT=chronos-client

SPRING_MAIL_HOST=sandbox.smtp.mailtrap.io
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=<mailtrap-username>
SPRING_MAIL_PASSWORD=<mailtrap-password>
Start Application

Run:

docker compose up -d
Verify Running Containers
docker ps

Expected containers:

chronos-auth
chronos-user
chronos-job
chronos-scheduler
chronos-executor
chronos-mysql
Service Ports
Service	Port
API Gateway/Auth	8080
UserService	8081
JobService	9091
SchedulerService	9000
ExecutorService	9001
MySQL	3306
API Flow
User Registration
POST /api/auth/register
Login
POST /api/auth/login
Create Job
POST /api/jobs

Authorization:

Bearer Token Required
Recurring Jobs

If:

"isRecurring": true

Scheduler automatically updates:

nextExecutionTime

after execution.

Kafka Topic
job_to_execute

Used for asynchronous job execution.

Scheduler Logic
Polls database every 2 seconds
Finds jobs where:
nextExecutionTime <= now
status = SCHEDULED
Publishes jobs to Kafka
Executor Logic

Consumes jobs from Kafka and delegates execution to:

EmailJobExecutor
HttpJobExecutor
ScriptJobExecutor
Security
JWT Authentication
Stateless Session Management
Spring Security Filters
Important Notes
SchedulerService and ExecutorService share Job DB
AuthService acts as API Gateway
Kafka used for decoupled async processing
Docker Compose manages all services
Future Improvements
Retry Mechanism
Dead Letter Queue
Outbox Pattern
Cron Expression Support
Distributed Locking
Monitoring & Metrics
Circuit Breakers
Kubernetes Deployment