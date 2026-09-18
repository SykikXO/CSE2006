# CSE2006 - Library Management System (CLI)

## Overview

Simple CLI app for college library. Students can search, issue and return books. Librarian can add books, see stock, and see reports. Data is saved in CSV files.

## Features

### User management

- Register as member, login, role check
- Admin can add member and librarian

### Catalog and circulation

- Add, list, search, edit and delete books
- Issue book if available, return with fine check
- My books list

### Reporting and analytics

- Total books and copies
- Overdue list
- Most borrowed book

## Technologies and tools

- Java 17
- Maven
- Java standard library (java.util, java.nio.file)
- JUnit 5
- CLI with Scanner

## Steps to install and run

```sh
git clone https://github.com/SykikXO/CSE2006.git
cd CSE2006
mvn compile
mvn exec:java -Dexec.mainClass="com.cse2006.library.Main"
```

Alternative jar run:

```sh
mvn package
java -jar target/library-1.0.0.jar
```

## Demo accounts

Seeded on first run:

| Username | Password | Role | Access |
|---|---|---|---|
| admin | admin123 | ADMIN | full, can create librarians via Admin -> Manage members |
| alice | pass123 | MEMBER | student, can register as member |

Public registration at auth menu creates `MEMBER` only. Librarian accounts are manager-only.

## Instructions for testing

```sh
mvn test
```

Tests: 7 tests for validator, user register, book add/search, issue and report.

## Screenshots

Saved in docs/screenshots if needed. To make:

```sh
mvn exec:java
# login as admin/admin123 and try manage books
```

## Docs

- Product definition: [statement.md](statement.md)
- Full design: [docs/DESIGN.md](docs/DESIGN.md)
