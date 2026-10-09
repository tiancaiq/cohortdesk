# CohortDesk

A full-stack training operations dashboard for managing cohorts, learners, employees, departments, and reports.

Maintained by [tiancaiq](https://github.com/tiancaiq).

> **Project origin:** CohortDesk is an English-language adaptation of the Tlias application from the 2024 Heima Java Web course. The frontend was subsequently rewritten in React, with a new dashboard, password-change flow, additional reporting, validation, and exception handling. This is a local demo, not a production deployment.

## What it does

- Sign in and manage departments and employees.
- Create cohorts, assign a cohort lead, and manage learners.
- Filter and page through records.
- Review employee and learner charts, login activity, and operation logs.
- Change the signed-in employee's password.

## Technology

| Layer | Tools |
| --- | --- |
| Backend | Java 21 target, Spring Boot 3.5, MyBatis, Maven |
| Database | MySQL 8 |
| Frontend | React 19, React Router 7, Vite 8, Axios, ECharts |
| File upload | Aliyun OSS integration (optional credentials required) |

## Run locally

The English demo database is separate from the original database. Its name is `cohortdesk_demo`, and the seed file contains synthetic names and activity.

### 1. Start MySQL

With Docker Desktop running:

```powershell
docker run --name cohortdesk-mysql -e MYSQL_ROOT_PASSWORD=123456 -p 127.0.0.1:3306:3306 -d mysql:8.4
docker cp assets/database/cohortdesk-demo.sql cohortdesk-mysql:/tmp/cohortdesk-demo.sql
docker exec -e MYSQL_PWD=123456 cohortdesk-mysql mysql -uroot -e "source /tmp/cohortdesk-demo.sql"
```

If the container already exists, run `docker start cohortdesk-mysql`. The seed script is intended for a new `cohortdesk_demo` database and should only be imported once.

### 2. Build and start the backend

Install a JDK of version 21 or newer. From the repository root:

```powershell
./cohortdesk-api/mvnw.cmd -f pom.xml -DskipTests clean package
java -jar cohortdesk-api/target/app.jar
```

The API listens on `http://127.0.0.1:8081`. Database settings are in `cohortdesk-api/src/main/resources/application.yaml`.

### 3. Start the frontend

In a second terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Open `http://127.0.0.1:5173/login`.

**Demo username:** `linchong`  
**Demo password:** `123456`

The frontend proxies `/api` requests to the backend on port 8081.

## Project structure

- `cohortdesk-api/`: controllers, services, MyBatis mappers, logging, and configuration
- `cohortdesk-model/`: entities, request objects, and response objects
- `cohortdesk-util/`: JWT and Aliyun OSS helpers
- `frontend/`: React application
- `assets/database/cohortdesk-demo.sql`: synthetic English-language MySQL seed
- `assets/documents/api-reference.md`: API endpoint reference

## Current limitations

This remains a local learning project. Passwords in the demo database are stored in plain text, and the authentication and logging code require security work before any public deployment. Avatar uploads require `OSS_ACCESS_KEY_ID` and `OSS_ACCESS_KEY_SECRET` plus a valid Aliyun OSS bucket. The current interface also retains the original demo's field model, including some fields specific to its course setting.

The `assets/prototype` directory and older demo media remain as legacy course artifacts. They are not used by the running application and still contain Chinese text. The app, demo database, and active documentation use English.

## License

See [LICENSE](LICENSE).
