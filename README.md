<div align="center">

# ☕ Encave

**Campus Café Management System**

A full-stack point-of-sale and inventory management platform built for college cafeterias — handling student authentication, multi-branch menus, real-time stock tracking, and daily revenue reporting.

![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active%20Development-yellow?style=for-the-badge)

</div>

---

## Overview

Encave is a campus café management system designed for institutions with one or more café branches. Students register and log in using their college PRN, browse available menu items, and place orders. The system automatically tracks inventory, processes payments, and gives branch staff a live estimated wait time and end-of-day revenue report — all without any third-party payment gateway or cloud dependency.

---

## Features

| Feature | Status |
|---------|--------|
| Student registration & login (PRN-based) | ✅ Done |
| Multi-branch support | ✅ Done |
| Menu browsing with live availability | ✅ Done |
| Order placement with transactional integrity | ✅ Done |
| Automatic stock reduction on order | ✅ Done |
| Estimated wait time per branch | ✅ Done |
| Daily revenue report per branch | ✅ Done |
| Desktop GUI (Java Swing) | ✅ Done |
| Password hashing | 🔲 Planned |
| Web / mobile frontend | 🔲 Planned |
| Admin dashboard | 🔲 Planned |
| Email order confirmation | 🔲 Planned |
| REST API layer | 🔲 Planned |

---

## Tech Stack

| Layer | Technology | Role |
|-------|-----------|------|
| Database | MariaDB | Schema, stored procedures, triggers |
| Backend scripts | Python 3 + pymysql | DB operations called by the GUI |
| Frontend | Java Swing | Desktop GUI, student-facing interface |
| IPC | Java `ProcessBuilder` | GUI calls Python scripts as subprocesses |

---

## Architecture

```
┌──────────────────────────────┐
│        Java Swing GUI        │
│  (Main.java — login, shop)   │
└────────────┬─────────────────┘
             │ ProcessBuilder (subprocess call)
             ▼
┌──────────────────────────────┐
│        Python Scripts        │
│  login.py      addUser.py    │
│  placeOrder.py getWaitTime.py│
└────────────┬─────────────────┘
             │ pymysql
             ▼
┌──────────────────────────────┐
│           MariaDB            │
│  Tables · Procedures         │
│  Functions · Triggers        │
└──────────────────────────────┘
```

---

## Database Schema

### Entity Overview

| Table | Description |
|-------|-------------|
| `Customer` | Student accounts — PRN as primary key and login credential |
| `Branch` | Café locations with unique contact per branch |
| `Food_Item` | Menu items scoped to a branch, with live stock and availability |
| `Orders` | Order header — links customer, branch, amount, and status |
| `Order_Details` | Line items — one row per food item per order |
| `Payment` | Payment record tied to each order |

### Full DDL

```sql
-- ─────────────────────────────────────────
-- CUSTOMER
-- PRN (Permanent Registration Number) is the
-- student's college roll number. Used as both
-- the primary key and the login identifier.
-- ─────────────────────────────────────────
CREATE TABLE Customer (
    prn        INT(11)      NOT NULL PRIMARY KEY,
    firstName  VARCHAR(20)  NOT NULL,
    lastName   VARCHAR(20)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      INT(11)      NOT NULL UNIQUE,
    password   VARCHAR(20)  NOT NULL,               -- plaintext for now; hashing planned
    creation   DATETIME     DEFAULT current_timestamp()
);

-- ─────────────────────────────────────────
-- BRANCH
-- Each physical café location is a branch.
-- Location and contact number must be unique.
-- ─────────────────────────────────────────
CREATE TABLE Branch (
    Branch_ID  INT(11)      NOT NULL PRIMARY KEY,
    Location   VARCHAR(150) NOT NULL UNIQUE,
    Contact_No INT          NOT NULL UNIQUE
);

-- ─────────────────────────────────────────
-- FOOD_ITEM
-- Menu items are branch-specific.
-- Availability defaults to 'Yes'.
-- The reduceStock trigger flips it to 'No'
-- when Quantity reaches zero.
-- ─────────────────────────────────────────
CREATE TABLE Food_Item (
    Item_ID      INT(11)      NOT NULL PRIMARY KEY,
    Branch_ID    INT(11)      NOT NULL,
    Name         VARCHAR(100) NOT NULL,
    Price        FLOAT        CHECK(Price > 0),
    Quantity     INT          CHECK(Quantity > 0),
    Availability VARCHAR(50)  DEFAULT 'Yes',
    FOREIGN KEY (Branch_ID) REFERENCES Branch(Branch_ID)
);

-- ─────────────────────────────────────────
-- ORDERS
-- One row per order. orderID is auto-increment.
-- Status defaults to 'Pending' and should be
-- updated to 'Completed' by staff on pickup.
-- ─────────────────────────────────────────
CREATE TABLE Orders (
    orderID     INT(11)      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    prn         INT(11),
    Branch_ID   INT(11),
    orderDate   DATETIME     DEFAULT current_timestamp(),
    status      VARCHAR(30)  DEFAULT 'Pending',
    totalAmount FLOAT        CHECK(totalAmount > 0),
    FOREIGN KEY (prn)       REFERENCES Customer(prn),
    FOREIGN KEY (Branch_ID) REFERENCES Branch(Branch_ID)
);

-- ─────────────────────────────────────────
-- ORDER_DETAILS
-- One row per food item per order.
-- Note: no composite primary key yet —
-- adding PRIMARY KEY (orderID, Item_ID) is planned.
-- ─────────────────────────────────────────
CREATE TABLE Order_Details (
    orderID   INT(11),
    Item_ID   INT(11),
    Item_Qty  INT   CHECK(Item_Qty > 0),
    Subtotal  FLOAT CHECK(Subtotal > 0),
    Branch_ID INT(11),
    FOREIGN KEY (orderID)   REFERENCES Orders(orderID),
    FOREIGN KEY (Item_ID)   REFERENCES Food_Item(Item_ID),
    FOREIGN KEY (Branch_ID) REFERENCES Branch(Branch_ID)
);

-- ─────────────────────────────────────────
-- PAYMENT
-- One payment record per order.
-- Payment_Mode defaults to 'UPI'.
-- Payment_Status is set to 'Success' by
-- the placeOrder procedure on commit.
-- ─────────────────────────────────────────
CREATE TABLE Payment (
    PaymentID      INT(11)     NOT NULL PRIMARY KEY,
    orderID        INT(11),
    Payment_Mode   VARCHAR(50) NOT NULL DEFAULT 'UPI',
    Payment_Status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (orderID) REFERENCES Orders(orderID)
);
```

---

## Stored Routines

### `placeOrder` — Procedure

Wraps order creation and payment record in a single transaction. If either insert fails, the whole operation rolls back.

| Param | Direction | Type | Description |
|-------|-----------|------|-------------|
| `p_userID` | IN | INT | Customer PRN |
| `p_branchID` | IN | INT | Branch ID |
| `p_amount` | IN | FLOAT | Total order amount |
| `p_paymentMode` | IN | VARCHAR(50) | e.g. `'UPI'`, `'Cash'` |
| `p_orderID` | OUT | INT | Auto-generated order ID |

```sql
SET @orderID = 0;
CALL placeOrder(1262242226, 1, 109.00, 'UPI', @orderID);
SELECT @orderID;
```

---

### `dailyrevenuereport` — Procedure

Uses a cursor to iterate today's orders for a branch and return the total revenue. Useful for end-of-shift reporting.

| Param | Direction | Type | Description |
|-------|-----------|------|-------------|
| `p_branchid` | IN | INT | Branch to report on |
| `p_totalrevenue` | OUT | FLOAT | Sum of today's order amounts |

```sql
SET @revenue = 0;
CALL dailyrevenuereport(1, @revenue);
SELECT @revenue;   -- e.g. 109.00
```

---

### `getWaitTime` — Function

Returns an estimated wait time in minutes based on pending orders at a branch.

**Formula:** `(pending_orders × 5) + 5`

```sql
SELECT getWaitTime(1);
-- 1 pending order → returns 10 (minutes)
```

---

### `reduceStock` — Trigger

Fires `AFTER INSERT` on `Order_Details`. Decrements `Food_Item.Quantity` by the ordered `Item_Qty`. If stock hits zero or below, sets `Availability = 'No'` automatically.

```sql
-- Fires automatically on line item insert:
INSERT INTO Order_Details VALUES (2, 1, 1, 109, 1);
-- Food_Item.Quantity for Item_ID=1 drops by 1
-- Availability flips to 'No' if Quantity <= 0
```

---

## Project Structure

```
Encave/
├── Main.java          # Java Swing GUI — login, signup, shop UI
├── login.py           # Authenticates PRN + password against Customer table
├── addUser.py         # Inserts a new customer record
├── placeOrder.py      # Calls placeOrder stored procedure (WIP)
├── getWaitTime.py     # Queries getWaitTime function
├── encave.txt         # MariaDB session log — full schema + test runs
└── .gitignore
```

---

## Getting Started

### Prerequisites

- [MariaDB](https://mariadb.org/download/) 10.6+
- Python 3.8+
- JDK 8+
- `python` on system PATH

### 1. Install Python dependencies

```bash
pip install pymysql
```

### 2. Set up the database

```sql
-- Run in MariaDB as root
CREATE DATABASE Encave;
CREATE USER 'encave'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON Encave.* TO 'encave'@'%';
FLUSH PRIVILEGES;
```

### 3. Import the schema

Copy the DDL from the [Full DDL](#full-ddl) section into `schema.sql`, then:

```bash
mysql -u encave -p Encave < schema.sql
```

### 4. Compile and run

```bash
javac Main.java
java Main
```

> Java and Python scripts must share the same directory. The GUI spawns Python via `ProcessBuilder` — confirm `python` resolves correctly (`python3` on some Linux distros).

---

## Deployment

### Local / Lab Machine

1. Install MariaDB, Python 3, and JDK on the target machine
2. Complete the setup steps above
3. Create a run script for convenience:

```bash
# run.sh (Linux / macOS)
#!/bin/bash
cd /path/to/encave
java Main
```

```bat
REM run.bat (Windows)
cd C:\encave
java Main
```

### Networked Deployment (Multiple Terminals, One DB Server)

For a lab with multiple student terminals sharing one database:

1. Run MariaDB on a central server (lab server, Raspberry Pi, etc.)
2. Update `host` in every Python script:

```python
# Before
database = pymysql.connect(host='localhost', ...)

# After
database = pymysql.connect(host='192.168.1.100', ...)
```

3. Grant remote access:

```sql
CREATE USER 'encave'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON Encave.* TO 'encave'@'%';
```

4. Open port `3306` in the server firewall
5. Deploy the Java + Python files to each terminal

---

## Known Issues

| Issue | Severity | Notes |
|-------|----------|-------|
| Passwords stored in plaintext | High | Hash with `bcrypt` before any real-world use |
| `placeOrder.py` incomplete | Medium | Connects to DB but doesn't call the procedure |
| Shop UI: all `+`/`-` buttons modify `count[0]` | Medium | Lambda closure bug — each item needs its own index |
| `Order_Details` has no primary key | Low | Add composite key `(orderID, Item_ID)` |
| PRN stored as `INT` | Low | Leading zeros in PRN are silently dropped |

---

## Roadmap

### v1.1 — Security & Stability
- [ ] Hash passwords with bcrypt
- [ ] Complete `placeOrder.py` to call the stored procedure
- [ ] Fix quantity counter bug in shop UI
- [ ] Add composite primary key to `Order_Details`
- [ ] Input validation on all Python scripts

### v1.2 — Staff Features
- [ ] Separate admin login with role table
- [ ] Order status update UI (Pending → Ready → Completed)
- [ ] Manual stock replenishment screen
- [ ] Branch management panel (add / edit / deactivate branches)

### v1.3 — Reporting & Analytics
- [ ] Graphical daily revenue chart per branch
- [ ] Best-selling items report
- [ ] Low-stock alert system
- [ ] Export reports to PDF / CSV

### v2.0 — Web & Mobile
- [ ] REST API backend (Flask or FastAPI) replacing subprocess calls
- [ ] Web frontend for students (React or plain HTML)
- [ ] Android app using the REST API
- [ ] QR code ordering at branch tables
- [ ] Payment gateway integration (Razorpay / PhonePe)

### v2.1 — Cloud & Scale
- [ ] Docker container for the database
- [ ] Hosted deployment (Railway, Render, or AWS RDS)
- [ ] Multi-institution support (separate schema per college)
- [ ] Role-based access control (student / staff / admin)
        
