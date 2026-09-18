# CSE2006 - Library Management System - Project Report

## Cover

- Project: Library Management System (CLI)
- Course: CSE2006
- Stack: Java 17, Maven
- Repo: https://github.com/SykikXO/CSE2006

## Abstract

This project is a simple CLI app for college library. It helps librarian manage books and students issue and return books. Data is saved in CSV files. The app has login, book management, issue and return, and simple reports.

## Table of Contents

1. Introduction
2. Problem Statement
3. Objectives
4. Scope
5. Target Users
6. Functional Requirements
7. Non-Functional Requirements
8. System Architecture
9. Process Flow
10. UML Diagrams
11. Database Design
12. Implementation
13. Testing
14. Screenshots
15. Conclusion
16. References

## 1. Introduction

College library was using manual register. It was slow and had errors. This project makes it digital with a simple menu system. It is made for college level, easy to run and understand.

## 2. Problem Statement

Manual tracking makes it hard to know which books are available, who has taken books, and when to return. Records get lost. Students wait and librarian does extra work. We need simple software to keep all this.

## 3. Objectives

- Make easy login and register
- Let admin add and manage books
- Let students search, issue and return books
- Show simple reports like stock and overdue

## 4. Scope

### In Scope

- Register and login
- Add, view, edit and delete books
- Search books
- Issue and return with limit check
- My books list
- Reports for admin
- CSV file storage

### Out of Scope

- Payment
- Email or SMS
- Barcode
- Web or mobile app

## 5. Target Users

### Admin (Librarian)

- Needs to add books, see stock, see who has books
- Problem is manual entry takes time

### Member (Student)

- Needs to find books, take and return easily
- Problem is not knowing if book is there and forgetting due date

## 6. Functional Requirements

- FR1: Register new member
- FR2: Login and check role
- FR3: Admin can add member and librarian
- FR4: Admin can add book with id, title, author, copies
- FR5: Admin can edit and delete book
- FR6: Search books by title or author
- FR7: Issue book if copy is there and limit not crossed
- FR8: Return book and show fine if late
- FR9: Show my active books
- FR10: Show reports like total books, overdue, most borrowed

Input/Output:

| Input | Check | Output |
|---|---|---|
| username, password | check csv | login ok or error |
| book id, title, author, copies | save to books.csv | book added |
| username and book id for issue | check copy and limit | issued or error |
| username and book id for return | check active issue | returned or fine |

## 7. Non-Functional Requirements

- Performance: works fast for 100 books, file read each time
- Security: role check, admin pages only for admin
- Usability: simple menu, clear messages
- Reliability: if csv missing it is created, bad line skipped
- Maintainability: separate packages for model, repo, service
- Logging: simple console output

## 8. System Architecture

![System architecture](diagrams/01-architecture.png)

Modules: model, repo, service, util, Main. Each does one work.

Folder:

```
src/main/java/com/cse2006/library/
  model/User, Book, Issue, Role
  repo/UserStore, BookStore, IssueStore
  service/AuthService, BookService, IssueService, ReportService
  util/Validator, Ansi
  Main
```

## 9. Process Flow

![Process flow](diagrams/02-process-flow.png)

Steps: start -> login/register -> go to dashboard based on role -> do work -> logout.

## 10. UML Diagrams

### Use Case

![Use case](diagrams/03-use-case.png)

### Class Diagram

![Class diagram](diagrams/04-class-diagram.png)

### Sequence

![Sequence](diagrams/05-sequence.png)

## 11. Database Design

Files in data folder, not pushed to github.

### ER Diagram

![ER diagram](diagrams/06-er-diagram.png)

### Schema

- users: `data/users/<username>.csv` -> `username,password,role,id,createdAt` e.g. `admin,admin123,ADMIN,a1,2026-09-18`
- books: `data/books.csv` -> `id,title,author,total,available` e.g. `B001,Intro to Java,Herbert Schildt,5,5`
- issues: `data/issues.csv` -> `id,bookId,username,issued,due,returned` e.g. `abc123,B001,alice,2026-09-18,2026-10-02,`

Rules: username is file name, book id is unique, issue is active if returned is empty.

## 12. Implementation

- Language: Java 17
- Build: Maven
- Storage: CSV files
- CLI: Scanner and simple menu, colors with ANSI

Key code is simple if-else and file read/write, no framework.

To run:

```sh
git clone https://github.com/SykikXO/CSE2006.git
cd CSE2006
mvn compile
mvn exec:java -Dexec.mainClass="com.cse2006.library.Main"
# demo logins: admin/admin123 , alice/pass123
```

To test:

```sh
mvn test
```

## 13. Testing

Tests in `src/test/java/com/cse2006/library/BasicTest.java`:

- username and password check
- book add and find
- user register and login
- book service add/search/delete
- issue service issue and return
- report counts

Result: 7 tests, all pass.

## 14. Screenshots

Place in `docs/screenshots/`:

- Login screen
- Admin dashboard
- Manage books
- Member issue and return
- Reports

To make them, run the app and take terminal screenshot.

## 15. Conclusion

The app covers all asked parts: user management, catalog and circulation, reporting, with simple CSV storage and clear menu flow. It is easy to run and check. Future work can add PDF export or fine payment.

## 16. References

- Java docs
- Maven docs
- College lab notes for library system
