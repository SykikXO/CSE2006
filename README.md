# CSE2006 - Library Management System (CLI)

## Overview

<!-- TODO: 2 to 3 sentences on what the system does, why it exists, who it is for -->

## Features

<!-- TODO: fill per module -->

### User management

-

### Catalog and circulation

-

### Reporting and analytics

-

## Technologies and tools

- Java 17
- Maven
- Java standard library (java.util, java.nio.file, java.security, java.util.logging)
- JUnit 5
- CLI with Scanner, nicer UX via JLine3 or picocli planned

## Steps to install and run

<!-- TODO: replace with verified commands -->

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

<!-- TODO: verify after pom and tests exist -->

```sh
mvn test
```

## Screenshots

<!-- TODO: add after CLI runs, save to docs/screenshots/ -->

- Login
- Admin course management
- Member borrow and return
- Report

## Docs

- Product definition: [statement.md](statement.md)
- Full design: [docs/DESIGN.md](docs/DESIGN.md)
