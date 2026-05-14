
# Intelligent Submission and Review System  
**COS730 – Assignment 2**  
**Student:** Khayelihle Majozi  

---

## Overview

This project implements and optimises an **Intelligent Submission and Review System** based on a provided sequence diagram.

The system supports the full workflow of:

- Artefact submission
- Validation of submission rules
- Reviewer assignment
- Evaluation and scoring
- Final outcome generation and notification

The repository contains both the **baseline implementation** (direct interpretation of the sequence diagram) and the **optimised implementation** (improved design based on software engineering principles).

---

## Project Structure

```
submission-review-system/
│
├── Original/               # Baseline implementation (Task 1)
├── Optimised/             # Improved implementation (Task 4)
│
├── src/main/java/         # Application code
├── src/main/resources/    # Configuration (H2, templates, etc.)
│
├── src/test/java/         # Benchmark and test cases
│   └── ExecutionTimeBenchmarkTest.java
│
└── README.md
```


## How to Run the Application

### 1. Clone the repository

```
git clone https://github.com/your-username/submission-review-system.git
cd submission-review-system
```

### 2. Run the application

```
mvn spring-boot:run
```

### 3. Access the system

Open your browser and navigate to:

```
http://localhost:9090
```

---

## How to Run Benchmark Tests

```
mvn test
```

The benchmark results will be printed in the console.


---

## Repository Notes

- The `Original/` folder contains the unmodified baseline implementation  
- The `Optimised/` folder contains the improved system  
- Both versions are retained to enable clear compariso
