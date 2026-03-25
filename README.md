# Retailer Rewards Program — Spring Boot REST API

A RESTful Spring Boot application that calculates customer reward points based on purchase transactions over a 3-month period.

---

## Points Calculation Rules

| Spend Range         | Points Earned              |
|---------------------|----------------------------|
| Under $50           | 0 points                   |
| $50.01 – $100       | 1 point per dollar over $50 |
| Over $100           | 2 points per dollar over $100 + 1 point per dollar between $50–$100 |

**Example:** $120 purchase = (2 × $20) + (1 × $50) = **90 points**

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+

### Start
```bash
mvn spring-boot:run
```

The app runs on **http://localhost:8080**

### Run Tests
```bash
mvn test
```

---

## API Endpoints

### 1. Get All Customer Rewards (last 3 months)
```
GET /api/rewards
```
**Response:**
```json
[
  {
    "customerId": "C001",
    "customerName": "Alice Johnson",
    "pointsPerMonth": {
      "2024-01": 365,
      "2024-02": 160,
      "2024-03": 220
    },
    "totalPoints": 745
  }
]
```

---

### 2. Get Rewards for a Specific Customer
```
GET /api/rewards/{customerId}
```
**Example:** `GET /api/rewards/C001`

---

### 3. Get Rewards by Custom Date Range
```
GET /api/rewards/range?startDate=2024-01-01&endDate=2024-03-31
```

---

### 4. Calculate Points for a Single Amount
```
POST /api/rewards/calculate?amount=120
```
**Response:**
```json
{
  "amount": 120.0,
  "points": 90,
  "breakdown": "2x$20 + 1x$50 = 90 points"
}
```

---

### 5. View All Transactions (debug)
```
GET /api/rewards/transactions
```

---

### 6. H2 Database Console
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:rewardsdb
Username: sa
Password: (empty)
```

---

## Sample Dataset

4 customers with transactions across January–March 2024:

| Customer       | Jan Points | Feb Points | Mar Points | Total |
|----------------|-----------|-----------|-----------|-------|
| Alice Johnson  | 365       | 160       | 220       | 745   |
| Bob Smith      | 35        | 460       | 50        | 545   |
| Carol White    | 0         | 37        | 98        | 135   |
| David Brown    | 350       | 0         | 280       | 630   |

---

## Project Structure

```
src/
├── main/java/com/retailer/rewards/
│   ├── RewardsApplication.java         # Entry point
│   ├── config/
│   │   └── DataSeeder.java             # Sample data loader
│   ├── controller/
│   │   └── RewardsController.java      # REST endpoints
│   ├── model/
│   │   ├── Transaction.java            # JPA entity
│   │   └── CustomerRewardsResponse.java # Response DTO
│   ├── repository/
│   │   └── TransactionRepository.java  # Spring Data JPA
│   └── service/
│       └── RewardsService.java         # Business logic
└── test/java/com/retailer/rewards/
    └── RewardsServiceTest.java         # Unit tests
```

---

## Key Design Decisions

- **H2 in-memory DB** — zero setup, data seeded on startup
- **Points formula** isolated in `RewardsService.calculatePoints()` — easy to test
- **Stream + groupingBy** — clean monthly aggregation per customer
- **TreeMap** — months returned in chronological order
- **Mockito** tests — service tested in isolation from DB
