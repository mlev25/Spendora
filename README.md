# Spendora

Spendora is a full-stack personal expense tracking web application developed as a university thesis project. It allows users to record, categorize, and analyze their spending through an interactive interface with AI-assisted features and multilingual support.

---

## Features

- User registration and login with JWT authentication and CAPTCHA verification
- Expense management: create, edit, delete expenses with amount, date, category, and description
- Custom expense categories per user
- AI-assisted category prediction when adding expenses (Google Gemini 2.5 Flash)
- Monthly and yearly spending statistics with interactive charts
- Calendar view showing daily expense summaries
- AI chatbot for financial questions (OpenRouter)
- Contact form with email delivery (Gmail SMTP)
- Hungarian and English language support
- Admin panel for user management
- Responsive design

---

## Technologies

**Backend**
- Java 21
- Spring Boot 3.5.6
- Spring Security with stateless JWT authentication
- Spring Data JPA / Hibernate
- PostgreSQL (hosted on Supabase)
- JJWT 0.11.5
- Kaptcha (CAPTCHA generation)
- Lombok
- Maven

**Frontend**
- Vue 3
- Vite 7
- Vue Router 4
- Pinia
- vue-i18n 11
- Chart.js 4 + vue-chartjs
- Axios
- Bootstrap 5

**Infrastructure**
- Docker + Docker Compose
- nginx (static file serving and SPA routing in Docker)

---

## Prerequisites

**For Docker (recommended)**
- Docker Desktop

**For manual startup**
- Java 21
- Maven 3.9+
- Node.js 20+
- npm

---

## Running the Application

### Option 1 - Docker (recommended)

1. Copy the environment file and fill in the values:

```bash
cp .env.example .env
```

2. Start the application:

```bash
docker compose up --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080

To stop: `Ctrl+C`, then `docker compose down`

To rebuild after code changes: `docker compose up --build`

---

### Option 2 - Manual

**Backend**

```bash
cd backend
./mvnw spring-boot:run
```

The backend starts on port 8080. Database credentials and API keys are read from `src/main/resources/application.properties`. The default values in that file point to the development environment and work out of the box.

**Frontend**

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on http://localhost:5173 and communicates with the backend at `http://localhost:8080/api` by default.

---

## API Endpoints

All endpoints are prefixed with `/api`. Endpoints marked as **protected** require a valid JWT token in the `Authorization: Bearer <token>` header. Endpoints marked as **admin** additionally require the `ADMIN` role.

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/auth/captcha` | Public | Returns a CAPTCHA image (PNG) and sets a session token |
| `POST` | `/api/auth/login` | Public | Authenticates a user, returns JWT token |
| `POST` | `/api/auth/register` | Public | Registers a new user account |

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/users/me` | Protected | Returns the authenticated user's profile |
| `PUT` | `/api/users/update-profile` | Protected | Updates name and email |
| `PUT` | `/api/users/change-password` | Protected | Changes the user's password |
| `DELETE` | `/api/users/delete-account` | Protected | Permanently deletes the user's account |

### Expenses

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/expenses` | Protected | Returns all expenses for the authenticated user |
| `GET` | `/api/expenses/{id}` | Protected | Returns a single expense by ID |
| `POST` | `/api/expenses` | Protected | Creates a new expense |
| `PUT` | `/api/expenses/{id}` | Protected | Updates an existing expense |
| `DELETE` | `/api/expenses/{id}` | Protected | Deletes an expense |
| `POST` | `/api/expenses/predict-category` | Protected | Predicts a category for a given expense description using Gemini AI |
| `GET` | `/api/expenses/calendar?year=&month=` | Protected | Returns daily expense totals for calendar display |

### Categories

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/categories` | Protected | Returns all categories for the authenticated user |
| `GET` | `/api/categories/{id}` | Protected | Returns a single category by ID |
| `POST` | `/api/categories` | Protected | Creates a new category |
| `PUT` | `/api/categories/{id}` | Protected | Updates an existing category |
| `DELETE` | `/api/categories/{id}` | Protected | Deletes a category |

### Statistics

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/statistics/summary` | Protected | Returns overall KPIs: total spent, averages, highest/lowest expense |
| `GET` | `/api/statistics/monthly?year=&month=` | Protected | Monthly breakdown with daily totals and category distribution |
| `GET` | `/api/statistics/yearly?year=` | Protected | Yearly breakdown with per-month totals |
| `GET` | `/api/statistics/categories?startDate=&endDate=` | Protected | Category spending with amounts and percentages for a date range |

### Chat

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/chat/ask` | Protected | Sends a message to the AI chatbot, returns a response |

### Contact

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/contact` | Public | Sends a contact form message via email |

### Admin

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/admin/users` | Admin | Returns a list of all registered users |
| `GET` | `/api/admin/stats` | Admin | Returns global application statistics |

---

## Environment Variables

When running with Docker, all configuration is provided through the `.env` file. Copy `.env.example` to `.env` and set the following values:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection URL for PostgreSQL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for signing JWT tokens (min. 256-bit) |
| `JWT_EXPIRATION_MS` | Token validity in milliseconds (default: 3600000) |
| `OPENROUTER_API_KEY` | API key for the AI chatbot (openrouter.ai) |
| `OPENROUTER_MODEL` | Model identifier for OpenRouter |
| `GEMINI_API_KEY` | API key for AI category prediction (aistudio.google.com) |
| `MAIL_USERNAME` | Gmail address used for sending contact form emails |
| `MAIL_PASSWORD` | Gmail App Password (not the account password) |
| `CONTACT_RECIPIENT_EMAIL` | Address where contact form submissions are delivered |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins |
| `VITE_API_URL` | Backend URL used by the frontend at build time |

For manual startup, all of these have working default values in `application.properties` and do not need to be configured separately.

---

## Project Structure

```
Spendora/
  backend/
    src/main/java/com/spendora/backend/
      config/         Security, JWT, CAPTCHA, RestTemplate configuration
      controller/     REST controllers (8 controllers)
      dto/            Data transfer objects grouped by domain
      entity/         JPA entities: User, Expense, Category, Role
      exception/      Global exception handler
      repository/     Spring Data JPA repositories
      service/        Service interfaces and implementations
    src/main/resources/
      application.properties
  frontend/
    src/
      components/     Reusable Vue components (ExpenseList, AddExpenseModal, ChatWidget, ...)
      views/          Page-level Vue components (11 views)
      stores/         Pinia stores
      services/       Axios API service modules
      locales/        i18n translation files (hu, en)
      router/         Vue Router configuration
  docker-compose.yml
  .env.example
```

