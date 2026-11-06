# Employee Management

A Java 17 / Spring Boot REST API with an H2 database and a separately run HTML, CSS, and JavaScript frontend. The frontend is served by a small Java 17 standard-library server, so it has no Node.js, npm, or additional runtime dependency. Bootstrap is loaded from its CDN.

## Requirements

- Java 17 or newer
- Maven 3.9+
- A browser with internet access for the Bootstrap and font CDN assets

## Start the application

Open two terminals in the project root.

1. Start the backend:

   **Windows PowerShell**

   ```powershell
   $env:SERVER_PORT = "8080"
   mvn spring-boot:run
   ```

   **macOS/Linux**

   ```bash
   SERVER_PORT=8080 mvn spring-boot:run
   ```

   The backend defaults to port `8080`. It creates a persistent H2 database in `./data/employee-management` and seeds six sample employees the first time the database is empty. The H2 console is available at `/h2-console` (JDBC URL: `jdbc:h2:file:./data/employee-management`, user: `sa`, password: blank).

2. Start the frontend in the project root:

   **Windows PowerShell**

   ```powershell
   $env:FRONTEND_PORT = "3000"
   $env:API_BASE_URL = "http://localhost:8080"
   java frontend\FrontendServer.java
   ```

   **macOS/Linux**

   ```bash
   FRONTEND_PORT=3000 API_BASE_URL=http://localhost:8080 java frontend/FrontendServer.java
   ```

   Open `http://localhost:3000`. `FRONTEND_PORT` defaults to `3000`; `API_BASE_URL` defaults to `http://localhost:8080`. The frontend server injects this value through `/config.js`; the browser code does not hardcode the backend address.

To use a different backend port, set `SERVER_PORT` and set `API_BASE_URL` to that backend's URL. If the frontend is served from a different origin, set `FRONTEND_ORIGIN` for the backend to that frontend origin so CORS allows it.

## Features

- Dashboard totals for all and active employees, plus department-wise counts.
- Employee directory with name search, department filter, active status, and salary.
- Add, edit, and delete employees.
- H2-backed sample data and persistent local database.

## REST API

All endpoints use JSON except the count endpoint, which returns a number. The employee fields are `employeeId`, `name`, `email`, `department`, `designation`, `salary`, and `active`. Employee ID is the primary key and must stay the same during an update.

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/employees` | List employees |
| GET | `/api/employees/{id}` | Get one employee |
| POST | `/api/employees` | Create an employee |
| PUT | `/api/employees/{id}` | Replace employee details |
| DELETE | `/api/employees/{id}` | Delete an employee |
| GET | `/api/employees/count` | Count all employees |
| GET | `/api/employees/department/{department}` | List one department (case-insensitive) |
| GET | `/api/employees/active/count` | Count active employees |

### Curl examples

The following examples use the seeded `EMP-1001` employee. Run them after starting the backend. On Windows, `curl.exe` avoids PowerShell's legacy `curl` alias.

```bash
# List all employees
curl http://localhost:8080/api/employees

# Get one employee
curl http://localhost:8080/api/employees/EMP-1001

# Create an employee
curl -i -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d '{"employeeId":"EMP-2001","name":"Jordan Lee","email":"jordan.lee@example.com","department":"Product","designation":"Product Manager","salary":105000,"active":true}'

# Update that employee
curl -i -X PUT http://localhost:8080/api/employees/EMP-2001 -H "Content-Type: application/json" -d '{"employeeId":"EMP-2001","name":"Jordan Lee-Smith","email":"jordan.lee@example.com","department":"Product","designation":"Senior Product Manager","salary":112000,"active":true}'

# Delete that employee
curl -i -X DELETE http://localhost:8080/api/employees/EMP-2001

# Count all employees
curl http://localhost:8080/api/employees/count

# List a department
curl http://localhost:8080/api/employees/department/Engineering

# Count active employees
curl http://localhost:8080/api/employees/active/count
```

In PowerShell, use `curl.exe` instead of `curl` (which can be an alias). These examples are one-line commands in both shells. The create, update, and delete examples are intended to be run in order.

## Run tests

```bash
mvn test
```

Tests use a separate in-memory H2 database and do not modify the local persistent database.
