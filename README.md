# 🏦 LoanTrack — Enterprise Fintech Loan Management Platform

> A full-stack Java Spring Boot application that automates the complete loan lifecycle — from digital KYC onboarding to EMI repayment tracking, automated penalty processing, and real-time financial analytics.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [System Architecture](#-system-architecture)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Configuration Reference](#-configuration-reference)
- [Testing the Application](#-testing-the-application)
- [How EMI Calculation Works](#-how-emi-calculation-works)
- [Credit Score Engine](#-credit-score-engine)
- [Scheduled Automation Jobs](#-scheduled-automation-jobs)
- [Security Notes](#-security-notes)
- [API & Controller Reference](#-api--controller-reference)
- [Screenshots](#-screenshots)
- [Future Enhancements](#-future-enhancements)

---

## 🌟 Overview

LoanTrack is an enterprise-grade fintech platform built with **Java Spring Boot 3** that models the complete internal operations of a digital lending institution. It features a multi-role architecture with dedicated portals for Borrowers, Loan Officers, and Bank Administrators — each with distinct workflows, permissions, and dashboards.

### What makes LoanTrack unique?

| Feature | Description |
|---|---|
| **Real Financial Logic** | EMI calculation uses the industry-standard Reducing Balance Method, not simplified flat-rate formulas |
| **Gamified Credit Scoring** | Experian-style dynamic credit score engine (300–900) that reacts to payment behavior in real time |
| **Full Lifecycle Automation** | `@Scheduled` jobs handle midnight penalty processing, morning email reminders, and default detection autonomously |
| **Enterprise Security** | Spring Security with role-based URL-level and method-level access control throughout |
| **PDF Generation** | Legally formatted loan statements generated server-side using OpenPDF |
<img src="assets\loan_statement.png" alt="Loan Statement PDF" width="600"/>
<h3> Loan Statement PDF  </h3>

---

## ✨ Key Features

### 👨‍💼 Borrower Portal

- **Digital Onboarding** — Secure registration with multi-step loan application form
- **KYC Document Upload** — Upload Aadhaar card, PAN card, salary slips, and bank statements
- **Loan Status Tracking** — Real-time application status with a full history timeline
- **EMI Schedule View** — Month-by-month amortization table showing principal, interest, and outstanding balance
- **Payment Processing** — Mock payment gateway supporting UPI, Card, and NEFT modes
- **Credit Score Dashboard** — Dynamic gauge chart showing your current score and score history log
- **PDF Statement Download** — Instantly generate and download formatted loan statements

### 🕵️ Loan Officer Portal

- **Verification Queue** — Centralized inbox for all pending loan applications
- **KYC Document Audit** — Direct download and review of uploaded identity documents
- **Checklist Verification** — Mandatory verification milestones before forwarding to Admin
- **Internal Remarks** — Add review notes visible to the Admin during final approval

### 👑 Admin Command Center

- **Financial Analytics Dashboard** — KPIs for Total Disbursed, Total Collected, Total Outstanding, Active Portfolios
- **One-Click Loan Disbursement** — Approving an application instantly creates a live Loan entity and auto-generates the entire EMI schedule
- **Transaction Ledger** — Deep-dive view of complete payment history for any active loan
- **CSV Export** — Export the active loan portfolio with localized INR currency formatting

### ⚙️ System Automation

- **Midnight Penalty Engine** — Scans all EMIs daily at 00:00, applies 2% prorated late fees, deducts credit score points
- **Morning Email Reminders** — Automated 9:00 AM emails for upcoming due payments
- **Real-Time Status Emails** — Instant email notifications on application approval or rejection
- **Default Detection** — Flags loans with 3+ consecutive missed EMIs as `DEFAULTED`

---

## 🛠️ Tech Stack

### Backend

| Technology | Version | Purpose |
|---|---|---|
| Java | 17+ | Core language |
| Spring Boot | 3.x | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | ORM and database abstraction |
| Hibernate | 6.x | JPA implementation |
| MySQL Connector/J | 8.x | Database driver |
| OpenPDF (LibrePDF) | 1.3.x | PDF document generation |
| Spring Boot Starter Mail | 3.x | Email via SMTP |
| Spring Task Scheduling | Built-in | `@Scheduled` background jobs |
| Lombok | 1.18.x | Boilerplate reduction |

### Frontend

| Technology | Version | Purpose |
|---|---|---|
| Thymeleaf | 3.x | Server-side HTML templating |
| Bootstrap | 5.3 | Responsive UI components |
| Chart.js | 4.x | Credit score gauge & analytics charts |
| Custom CSS | — | Role-specific theming |

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT BROWSER                           │
│           Thymeleaf + Bootstrap 5 + Chart.js                │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP
┌─────────────────────▼───────────────────────────────────────┐
│                  SPRING BOOT APPLICATION                     │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   Security   │  │  Controller  │  │    Scheduler     │  │
│  │   Layer      │  │   Layer      │  │    (Cron Jobs)   │  │
│  │              │  │              │  │                  │  │
│  │ Spring       │  │ Auth         │  │ PenaltyEngine    │  │
│  │ Security     │  │ Borrower     │  │ EmailReminder    │  │
│  │ RBAC         │  │ Officer      │  │ DefaultChecker   │  │
│  │ BCrypt       │  │ Admin        │  │                  │  │
│  └──────────────┘  └──────┬───────┘  └──────────────────┘  │
│                           │                                 │
│  ┌────────────────────────▼────────────────────────────┐    │
│  │                  SERVICE LAYER                       │    │
│  │                                                      │    │
│  │  LoanApplicationService  │  EMIService              │    │
│  │  LoanService             │  PaymentService          │    │
│  │  CreditScoreService      │  PDFService              │    │
│  │  EmailService            │  DocumentService         │    │
│  └────────────────────────┬─────────────────────────────┘   │
│                           │                                  │
│  ┌────────────────────────▼─────────────────────────────┐   │
│  │              REPOSITORY LAYER (Spring Data JPA)       │   │
│  └────────────────────────┬─────────────────────────────┘   │
└───────────────────────────┼─────────────────────────────────┘
                            │ JDBC
┌───────────────────────────▼─────────────────────────────────┐
│                      MySQL 8.0 Database                      │
│   users │ loan_applications │ loans │ emi_schedule           │
│   payments │ credit_scores │ documents │ status_history      │
└─────────────────────────────────────────────────────────────┘
```

### Loan Lifecycle State Machine

```
  [APPLICATION SUBMITTED]
           │
           ▼
       [PENDING]  ──────────────────────────► [CANCELLED]
           │                                   (by user)
           ▼
    [UNDER_REVIEW]  ───────────────────────► [REJECTED]
           │                                  (by Admin)
           ▼
      [APPROVED]
           │
           ▼ (EMI Schedule Auto-Generated)
       [ACTIVE]  ──────────────────────────► [DEFAULTED]
           │                                 (3+ missed EMIs)
           ▼
       [CLOSED]
       (all EMIs paid)
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
users (1) ──────────────────────── (N) loan_applications
  │                                           │
  │                                           ├── (N) documents
  │                                           └── (N) loan_status_history
  │
  ├── (N) credit_scores
  │
  └── (1) loan_applications ── (1) loans
                                    │
                                    ├── (N) emi_schedule
                                    └── (N) payments
```

### Core Tables

**`users`**
```sql
CREATE TABLE users (
    user_id      BIGINT       PRIMARY KEY AUTO_INCREMENT,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,           -- BCrypt hashed
    phone        VARCHAR(15)  NOT NULL,
    role         ENUM('BORROWER','OFFICER','ADMIN') NOT NULL DEFAULT 'BORROWER',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**`loan_applications`**
```sql
CREATE TABLE loan_applications (
    application_id   BIGINT         PRIMARY KEY AUTO_INCREMENT,
    user_id          BIGINT         NOT NULL,
    loan_amount      DECIMAL(12,2)  NOT NULL,
    loan_purpose     ENUM('PERSONAL','HOME','MEDICAL','EDUCATION','VEHICLE') NOT NULL,
    tenure_months    INT            NOT NULL,
    employment_type  ENUM('SALARIED','SELF_EMPLOYED','BUSINESS') NOT NULL,
    monthly_income   DECIMAL(10,2)  NOT NULL,
    status           ENUM('PENDING','UNDER_REVIEW','APPROVED','REJECTED','CANCELLED')
                     NOT NULL DEFAULT 'PENDING',
    applied_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    reviewed_by      BIGINT,                      -- FK → users (Officer)
    approved_by      BIGINT,                      -- FK → users (Admin)
    remarks          TEXT,
    FOREIGN KEY (user_id)    REFERENCES users(user_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    FOREIGN KEY (approved_by) REFERENCES users(user_id)
);
```

**`loans`**
```sql
CREATE TABLE loans (
    loan_id              BIGINT        PRIMARY KEY AUTO_INCREMENT,
    application_id       BIGINT        NOT NULL UNIQUE,
    user_id              BIGINT        NOT NULL,
    principal_amount     DECIMAL(12,2) NOT NULL,
    interest_rate        DECIMAL(5,2)  NOT NULL,   -- Annual %
    tenure_months        INT           NOT NULL,
    emi_amount           DECIMAL(10,2) NOT NULL,
    disbursement_date    DATE          NOT NULL,
    end_date             DATE          NOT NULL,
    outstanding_balance  DECIMAL(12,2) NOT NULL,
    total_paid           DECIMAL(12,2) NOT NULL DEFAULT 0,
    loan_status          ENUM('ACTIVE','CLOSED','DEFAULTED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (application_id) REFERENCES loan_applications(application_id),
    FOREIGN KEY (user_id)        REFERENCES users(user_id)
);
```

**`emi_schedule`**
```sql
CREATE TABLE emi_schedule (
    emi_id               BIGINT        PRIMARY KEY AUTO_INCREMENT,
    loan_id              BIGINT        NOT NULL,
    emi_number           INT           NOT NULL,
    due_date             DATE          NOT NULL,
    emi_amount           DECIMAL(10,2) NOT NULL,
    principal_component  DECIMAL(10,2) NOT NULL,
    interest_component   DECIMAL(10,2) NOT NULL,
    payment_status       ENUM('UPCOMING','PAID','LATE','MISSED') NOT NULL DEFAULT 'UPCOMING',
    paid_date            DATE,
    paid_amount          DECIMAL(10,2),
    penalty_amount       DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id)
);
```

**`payments`**
```sql
CREATE TABLE payments (
    payment_id       BIGINT        PRIMARY KEY AUTO_INCREMENT,
    loan_id          BIGINT        NOT NULL,
    emi_id           BIGINT        NOT NULL,
    user_id          BIGINT        NOT NULL,
    amount_paid      DECIMAL(10,2) NOT NULL,
    penalty_paid     DECIMAL(10,2) NOT NULL DEFAULT 0,
    payment_date     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    payment_mode     ENUM('UPI','NEFT','IMPS','CARD','CASH') NOT NULL,
    transaction_ref  VARCHAR(100),
    status           ENUM('SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'SUCCESS',
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id),
    FOREIGN KEY (emi_id)  REFERENCES emi_schedule(emi_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

**`credit_scores`**
```sql
CREATE TABLE credit_scores (
    score_id        BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    score_value     INT          NOT NULL,          -- 300-900
    change_amount   INT          NOT NULL,          -- +5, -30, etc.
    change_reason   VARCHAR(200) NOT NULL,
    related_emi_id  BIGINT,
    recorded_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)        REFERENCES users(user_id),
    FOREIGN KEY (related_emi_id) REFERENCES emi_schedule(emi_id)
);
```

---

## 📁 Project Structure

```
loantrack/
├── src/
│   ├── main/
│   │   ├── java/com/loantrack/
│   │   │   ├── LoantrackApplication.java          # Entry point, @EnableScheduling
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java             # RBAC, BCrypt, URL patterns
│   │   │   │   ├── MailConfig.java                 # JavaMailSender bean
│   │   │   │   └── WebMvcConfig.java               # Static resource handlers
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java             # /register, /login, /logout
│   │   │   │   ├── BorrowerController.java         # /borrower/**
│   │   │   │   ├── LoanApplicationController.java  # /apply, /applications
│   │   │   │   ├── PaymentController.java          # /borrower/pay/**
│   │   │   │   ├── OfficerController.java          # /officer/**
│   │   │   │   ├── AdminController.java            # /admin/**
│   │   │   │   └── PDFController.java              # /pdf/download/**
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── UserService.java                # Registration, UserDetails loading
│   │   │   │   ├── LoanApplicationService.java     # Application lifecycle
│   │   │   │   ├── LoanService.java                # Loan creation on approval
│   │   │   │   ├── EMIService.java                 # Reducing balance calculation
│   │   │   │   ├── PaymentService.java             # Payment processing
│   │   │   │   ├── PenaltyService.java             # Late fee calculation
│   │   │   │   ├── CreditScoreService.java         # Score update events
│   │   │   │   ├── PDFService.java                 # OpenPDF document builder
│   │   │   │   ├── EmailService.java               # SMTP sending
│   │   │   │   └── DocumentService.java            # Multipart file storage
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── LoanApplication.java
│   │   │   │   ├── Loan.java
│   │   │   │   ├── EMISchedule.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── CreditScore.java
│   │   │   │   ├── Document.java
│   │   │   │   └── LoanStatusHistory.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── LoanApplicationRepository.java
│   │   │   │   ├── LoanRepository.java
│   │   │   │   ├── EMIScheduleRepository.java
│   │   │   │   ├── PaymentRepository.java
│   │   │   │   └── CreditScoreRepository.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── LoanApplicationDTO.java
│   │   │   │   └── PaymentDTO.java
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   ├── Role.java                       # BORROWER, OFFICER, ADMIN
│   │   │   │   ├── ApplicationStatus.java          # PENDING, UNDER_REVIEW, APPROVED, REJECTED
│   │   │   │   ├── LoanStatus.java                 # ACTIVE, CLOSED, DEFAULTED
│   │   │   │   └── PaymentStatus.java              # UPCOMING, PAID, LATE, MISSED
│   │   │   │
│   │   │   └── scheduler/
│   │   │       └── EMISchedulerJob.java            # Midnight + morning cron jobs
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── auth/
│   │       │   │   ├── login.html
│   │       │   │   └── register.html
│   │       │   ├── borrower/
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── apply-loan.html
│   │       │   │   ├── my-loans.html
│   │       │   │   ├── emi-schedule.html
│   │       │   │   ├── make-payment.html
│   │       │   │   └── credit-score.html
│   │       │   ├── officer/
│   │       │   │   ├── dashboard.html
│   │       │   │   └── review-application.html
│   │       │   └── admin/
│   │       │       ├── dashboard.html
│   │       │       ├── all-applications.html
│   │       │       ├── approve-loan.html
│   │       │       └── analytics.html
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── uploads/                        # KYC documents (git-ignored)
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/loantrack/
│           ├── service/
│           │   ├── EMIServiceTest.java
│           │   └── CreditScoreServiceTest.java
│           └── controller/
│               └── AuthControllerTest.java
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 📦 Prerequisites

Before running LoanTrack, ensure you have the following installed:

| Tool | Minimum Version | Check Command |
|---|---|---|
| Java JDK | 17 | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| MySQL Server | 8.0 | `mysql --version` |
| MySQL Workbench | Any | (optional, for DB inspection) |
| Git | Any | `git --version` |

> **Tip:** Use [SDKMAN!](https://sdkman.io/) on Linux/macOS to easily manage Java versions: `sdk install java 17-open`

---

## 🚀 Installation & Setup

### Step 1 — Clone the Repository

```bash
git clone https://github.com/yourusername/loantrack.git
cd loantrack
```

### Step 2 — Create the MySQL Database

Open your MySQL client or Workbench and run:

```sql
CREATE DATABASE loantrack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Step 3 — Configure `application.properties`

Navigate to `src/main/resources/application.properties` and fill in your values:

```properties
# ─── Database ────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/loantrack_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ─── JPA / Hibernate ─────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# ─── Thymeleaf ───────────────────────────────────────────────
spring.thymeleaf.cache=false

# ─── File Uploads ────────────────────────────────────────────
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=src/main/resources/static/uploads

# ─── Email / SMTP ────────────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ─── Scheduling ──────────────────────────────────────────────
spring.task.scheduling.pool.size=5

# ─── Server ──────────────────────────────────────────────────
server.port=8080
```

> **Gmail App Password:** Go to your Google Account → Security → 2-Step Verification → App Passwords → Generate one for "Mail". Use that 16-character code as your password.

### Step 4 — Build the Project

```bash
# Downloads all Maven dependencies including OpenPDF
./mvnw clean install -DskipTests
```

### Step 5 — Run the Application

```bash
./mvnw spring-boot:run
```

The application starts at: **[http://localhost:8080](http://localhost:8080)**

Spring Boot will automatically create all database tables on first run (`ddl-auto=update`).

---

## ⚙️ Configuration Reference

### Full `application.properties` Options

| Property | Default | Description |
|---|---|---|
| `spring.jpa.hibernate.ddl-auto` | `update` | Set to `validate` in production |
| `spring.jpa.show-sql` | `true` | Set to `false` in production |
| `file.upload-dir` | `static/uploads` | Directory for KYC document storage |
| `spring.servlet.multipart.max-file-size` | `5MB` | Max individual file upload size |
| `server.port` | `8080` | Application port |

### Security URL Patterns (defined in `SecurityConfig.java`)

| URL Pattern | Allowed Roles | Description |
|---|---|---|
| `/`, `/register`, `/login` | Public | Auth pages |
| `/css/**`, `/js/**` | Public | Static assets |
| `/borrower/**` | `BORROWER` | Borrower portal |
| `/officer/**` | `OFFICER` | Loan Officer portal |
| `/admin/**` | `ADMIN` | Admin command center |
| `/pdf/**` | All authenticated | PDF downloads |

---

## 🧪 Testing the Application

### Setting Up Test Accounts

LoanTrack uses role-based registration. Set up all three roles to test the complete workflow:

**1. Register as a Borrower** — Use the standard registration form at `/register`.

**2. Create a Loan Officer account:**
```sql
-- After registering a second account normally, update its role:
UPDATE users SET role = 'OFFICER' WHERE email = 'officer@test.com';
```

**3. Create an Admin account:**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@test.com';
```

### Full End-to-End Test Flow

Follow these steps in order to test the complete loan lifecycle:

```
┌─────────────────────────────────────────────────────────┐
│  STEP 1 — Borrower applies                              │
│  Login as Borrower → Apply for Loan → Upload KYC docs   │
│  Expected: Application status = PENDING                 │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│  STEP 2 — Officer reviews                               │
│  Login as Officer → Open application → Review docs      │
│  → Complete verification checklist → Forward to Admin   │
│  Expected: Application status = UNDER_REVIEW            │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│  STEP 3 — Admin approves                                │
│  Login as Admin → Open application → Approve & Disburse │
│  Expected: Loan created + EMI schedule generated        │
│            Borrower receives approval email             │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│  STEP 4 — Borrower repays                               │
│  Login as Borrower → View EMI Schedule                  │
│  → Make Payment → Observe credit score increase         │
│  → Download PDF Statement                               │
│  Expected: EMI marked PAID, credit score +5, PDF opens  │
└─────────────────────────────────────────────────────────┘
```

### Testing the Penalty Engine

To test the automated penalty system without waiting until midnight:

```java
// Temporarily change the cron expression in EMISchedulerJob.java to run every minute:
@Scheduled(cron = "0 * * * * *")  // Every minute (testing only)
public void applyPenalties() { ... }
```

Then manually backdate an EMI's `due_date` in the database:
```sql
UPDATE emi_schedule SET due_date = '2024-01-01' WHERE emi_id = 1;
```

---

## 📐 How EMI Calculation Works

LoanTrack uses the **Reducing Balance Method** — the industry standard used by all major Indian banks and NBFCs.

### Formula

```
EMI = P × r × (1 + r)^n
      ─────────────────
         (1 + r)^n - 1

Where:
  P = Principal loan amount (₹)
  r = Monthly interest rate = Annual Rate ÷ 12 ÷ 100
  n = Loan tenure in months
```

### Example Calculation

| Parameter | Value |
|---|---|
| Principal (P) | ₹2,00,000 |
| Annual Interest Rate | 12% |
| Monthly Rate (r) | 12 ÷ 12 ÷ 100 = **0.01** |
| Tenure (n) | 24 months |
| **Monthly EMI** | **₹9,415** |
| Total Amount Payable | ₹2,25,960 |
| Total Interest | ₹25,960 |

### Auto-Generated Amortization Table (Sample)

| Month | EMI (₹) | Principal (₹) | Interest (₹) | Balance (₹) |
|---|---|---|---|---|
| 1 | 9,415 | 7,415 | 2,000 | 1,92,585 |
| 2 | 9,415 | 7,489 | 1,926 | 1,85,096 |
| 3 | 9,415 | 7,564 | 1,851 | 1,77,532 |
| ... | ... | ... | ... | ... |
| 24 | 9,415 | 9,321 | 94 | 0 |

### Core Java Implementation

```java
// EMIService.java
public BigDecimal calculateEMI(BigDecimal principal, double annualRate, int tenureMonths) {
    double r = annualRate / 12 / 100;  // Convert to monthly rate
    double pow = Math.pow(1 + r, tenureMonths);
    double emi = principal.doubleValue() * r * pow / (pow - 1);
    return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
}

public void generateSchedule(Loan loan) {
    BigDecimal balance = loan.getPrincipalAmount();
    double r = loan.getInterestRate() / 12 / 100;
    LocalDate dueDate = loan.getDisbursementDate().plusMonths(1);

    for (int i = 1; i <= loan.getTenureMonths(); i++) {
        BigDecimal interest = balance.multiply(BigDecimal.valueOf(r))
                                     .setScale(2, RoundingMode.HALF_UP);
        BigDecimal principal = loan.getEmiAmount().subtract(interest);
        balance = balance.subtract(principal);

        EMISchedule emi = new EMISchedule();
        emi.setEmiNumber(i);
        emi.setDueDate(dueDate);
        emi.setPrincipalComponent(principal);
        emi.setInterestComponent(interest);
        emi.setPaymentStatus(PaymentStatus.UPCOMING);
        emiRepository.save(emi);

        dueDate = dueDate.plusMonths(1);
    }
}
```

---

## 🎯 Credit Score Engine

LoanTrack simulates a real-world Experian-style credit scoring system with a range of **300–900**.

### Starting Score

Every borrower begins with a base score of **650** upon loan approval.

### Score Change Events

| Event | Score Change |
|---|---|
| EMI paid on time (within due date) | **+5** |
| EMI paid 1–7 days late | **-5** |
| EMI paid 8–30 days late | **-15** |
| EMI paid after 30+ days | **-25** |
| EMI missed entirely | **-30** |
| Loan fully repaid on schedule | **+50** |
| Pre-closure (early full repayment) | **+30** |
| Multiple active loans (2+) | **-10** |

### Score Rating Bands

| Range | Rating | Loan Eligibility |
|---|---|---|
| 750–900 | 🟢 Excellent | Eligible for all products at best rates |
| 650–749 | 🟡 Good | Eligible with standard terms |
| 550–649 | 🟠 Fair | Eligible with higher interest rates |
| 300–549 | 🔴 Poor | Applications likely to be rejected |

### Score History

Every score change is logged to the `credit_scores` table with the event reason, allowing borrowers to see a full audit trail of what affected their score.

---

## ⏰ Scheduled Automation Jobs

All jobs are configured in `EMISchedulerJob.java` and require `@EnableScheduling` on the main application class.

```java
@Component
public class EMISchedulerJob {

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void applyPenaltiesAndUpdateMissedEMIs() {
        // 1. Find all UPCOMING EMIs past their due date (beyond 3-day grace period)
        // 2. Calculate penalty: EMI Amount × 2% × (overdue days / 30)
        // 3. Mark as MISSED, store penalty amount
        // 4. Deduct 30 credit score points
        // 5. Check if loan has 3+ consecutive MISSED EMIs → mark as DEFAULTED
    }

    // Runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendPaymentReminders() {
        // Find all UPCOMING EMIs due in exactly 3 days
        // Send personalized reminder email to each borrower
    }

    // Runs on the 1st of every month at 8:00 AM
    @Scheduled(cron = "0 0 8 1 * *")
    public void sendMonthlyStatements() {
        // Email all active borrowers their monthly account summary
    }
}
```

### Penalty Calculation Formula

```
Penalty = EMI Amount × Penalty Rate × (Overdue Days ÷ 30)

Default Penalty Rate: 2% per month
Grace Period: 3 days after due date

Example:
  EMI Amount    = ₹9,415
  Overdue Days  = 15 days
  Penalty       = 9415 × 0.02 × (15/30) = ₹94.15
```

---

## 🔒 Security Notes

### Critical: Do Not Commit Uploads Folder

KYC documents (Aadhaar, PAN cards) are stored locally in `src/main/resources/static/uploads/`. This directory is **explicitly excluded** in `.gitignore` to prevent exposure of PII.

```gitignore
# .gitignore
src/main/resources/static/uploads/
*.env
application-prod.properties
```

### Production Security Checklist

- [ ] Move all secrets to OS environment variables or a Secrets Manager (e.g., AWS SSM)
- [ ] Change `spring.jpa.hibernate.ddl-auto` from `update` to `validate`
- [ ] Disable `spring.jpa.show-sql`
- [ ] Enable HTTPS with an SSL certificate
- [ ] Store uploaded files in S3 or equivalent cloud storage, not on the local server
- [ ] Add rate limiting on `/login` endpoint to prevent brute force attacks
- [ ] Enable Spring Security's CSRF protection (enabled by default — do not disable)
- [ ] Implement file type validation server-side (not just client-side) for uploads
- [ ] Set `HttpOnly` and `Secure` flags on session cookies

### Password Security

All passwords are hashed using **BCrypt** with a strength factor of 12:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

---

## 🗺️ API & Controller Reference

### Auth Routes

| Method | URL | Role | Description |
|---|---|---|---|
| GET | `/register` | Public | Show registration form |
| POST | `/register` | Public | Submit registration |
| GET | `/login` | Public | Show login form |
| POST | `/logout` | Authenticated | Logout |

### Borrower Routes

| Method | URL | Role | Description |
|---|---|---|---|
| GET | `/borrower/dashboard` | BORROWER | Main dashboard |
| GET | `/borrower/apply` | BORROWER | Loan application form |
| POST | `/borrower/apply` | BORROWER | Submit application |
| GET | `/borrower/loans` | BORROWER | View all my loans |
| GET | `/borrower/emi/{loanId}` | BORROWER | View EMI schedule |
| GET | `/borrower/pay/{emiId}` | BORROWER | Payment page |
| POST | `/borrower/pay/{emiId}` | BORROWER | Process payment |
| GET | `/borrower/credit-score` | BORROWER | Credit score dashboard |
| GET | `/pdf/statement/{loanId}` | BORROWER | Download PDF statement |

### Officer Routes

| Method | URL | Role | Description |
|---|---|---|---|
| GET | `/officer/dashboard` | OFFICER | Officer dashboard |
| GET | `/officer/applications` | OFFICER | All pending applications |
| GET | `/officer/review/{id}` | OFFICER | Application detail |
| POST | `/officer/forward/{id}` | OFFICER | Forward to Admin |

### Admin Routes

| Method | URL | Role | Description |
|---|---|---|---|
| GET | `/admin/dashboard` | ADMIN | Analytics dashboard |
| GET | `/admin/applications` | ADMIN | All applications |
| POST | `/admin/approve/{id}` | ADMIN | Approve & disburse loan |
| POST | `/admin/reject/{id}` | ADMIN | Reject application |
| GET | `/admin/loans` | ADMIN | All active loans |
| GET | `/admin/loan/{id}` | ADMIN | Loan transaction ledger |
| GET | `/admin/export/csv` | ADMIN | Export portfolio as CSV |

---

## 🚀 Future Enhancements

| Feature | Technology | Priority |
|---|---|---|
| Razorpay payment gateway integration | Razorpay Java SDK | High |
| REST API layer for mobile app | Spring REST + JWT | High |
| OTP verification on registration | Twilio SMS API | Medium |
| Docker containerization | Docker + Compose | Medium |
| Loan pre-closure calculator | Custom formula | Medium |
| Two-factor authentication | Google Authenticator | Medium |
| Real-time notifications | WebSocket | Low |
| Spring Batch for bulk approvals | Spring Batch | Low |
| Cloud file storage | AWS S3 SDK | High (Production) |
| CI/CD pipeline | GitHub Actions | Medium |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Built with ❤️ using Java Spring Boot

⭐ Star this repo if it helped you!

</div>
