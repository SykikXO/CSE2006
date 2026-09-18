# Design

## Problem statement

See statement.md. In short, library needs simple software to keep books and issue records without manual register.

## Objectives

- Make login and register easy
- Let admin add and manage books
- Let students issue and return books
- Show simple reports

## Functional requirements

### User management

- FR1: Register new member with username and password
- FR2: Login with username and password, check role
- FR3: Admin can create member and librarian accounts
- FR4: Show list of users

### Catalog and circulation

- FR5: Admin can add book with id, title, author, copies
- FR6: Admin can edit and delete book
- FR7: Anyone can search books by title or author
- FR8: Member can issue book if copy is available and limit not crossed
- FR9: Member can return book and get fine if late

### Reporting and analytics

- FR10: Show total books and copies
- FR11: Show overdue list
- FR12: Show most borrowed book

Input/output structure:

| Input | Processing | Output |
|---|---|---|
| username, password | check in UserStore | success or error |
| book id, title, author, copies | save in books.csv | list shows new book |
| member and book id for issue | check copies and limit | issued or error |
| member and book id for return | check active issue | returned or late fine |

## Non-functional requirements

- Performance: search and login use files, works fast for small data (100 books)
- Security: plain text password in csv, role check before admin work
- Usability: simple menu, clear messages, re-prompt on wrong input
- Reliability: if csv missing it is created, bad line is skipped
- Maintainability: separate packages for model, repo, service
- Logging: simple console messages

## System architecture diagram

```mermaid
graph TD
  A[Main CLI] --> B[AuthService]
  A --> C[BookService]
  A --> D[IssueService]
  A --> E[ReportService]
  B --> F[UserStore]
  C --> G[BookStore]
  D --> G
  D --> H[IssueStore]
  E --> G
  E --> H
  F --> I[data/users/*.csv]
  G --> J[data/books.csv]
  H --> K[data/issues.csv]
```

## Process flow and workflow diagram

```mermaid
flowchart TD
  Start --> Login
  Login --> Check{Login ok?}
  Check -- No --> Login
  Check -- Yes --> Role{Role?}
  Role -- Admin --> A1[Manage Books]
  Role -- Admin --> A2[Manage Members]
  Role -- Admin --> A3[Reports]
  Role -- Member --> M1[Browse]
  Role -- Member --> M2[Issue]
  Role -- Member --> M3[Return]
  Role -- Member --> M4[My Books]
```

## UML diagrams

### Use case diagram

```mermaid
graph LR
  Admin --> UC1[Login]
  Member --> UC1
  Admin --> UC2[Manage Books]
  Admin --> UC3[Manage Members]
  Admin --> UC4[View Reports]
  Member --> UC5[Search Books]
  Member --> UC6[Issue Book]
  Member --> UC7[Return Book]
```

### Class diagram

```mermaid
classDiagram
  class User { String id; String username; String password; Role role; }
  class Book { String id; String title; String author; int total; int available; }
  class Issue { String id; String bookId; String username; LocalDate issued; LocalDate due; LocalDate returned; }
  class UserStore { save(); findByUsername(); allUsers(); }
  class BookStore { add(); findById(); search(); all(); }
  class IssueStore { add(); findActive(); all(); overdue(); }
  class AuthService { register(); login(); }
  class BookService { addBook(); search(); deleteBook(); }
  class IssueService { issueBook(); returnBook(); }
  class ReportService { totalBooks(); overdueList(); mostBorrowed(); }
  AuthService --> UserStore
  BookService --> BookStore
  IssueService --> BookStore
  IssueService --> IssueStore
  ReportService --> BookStore
  ReportService --> IssueStore
```

### Sequence diagram

```mermaid
sequenceDiagram
  participant M as Main
  participant A as AuthService
  participant U as UserStore
  M->>A: login(username, password)
  A->>U: findByUsername()
  U-->>A: User
  A-->>M: success or error

  participant S as IssueService
  participant B as BookStore
  participant I as IssueStore
  M->>S: issueBook(user, bookId)
  S->>B: findById()
  S->>I: findActive()
  S->>I: add()
  S->>B: update()
  S-->>M: done or error
```

## Database and storage design

Files in data folder, gitignored.

### ER diagram

```mermaid
erDiagram
  USER ||--o{ ISSUE : has
  BOOK ||--o{ ISSUE : issued_as
  USER { string id; string username; string password; string role; }
  BOOK { string id; string title; string author; int total; int available; }
  ISSUE { string id; string bookId; string username; date issued; date due; date returned; }
```

### Schema design

- users: `data/users/<username>.csv` -> `username,password,role,id,createdAt`
- books: `data/books.csv` -> `id,title,author,total,available`
- issues: `data/issues.csv` -> `id,bookId,username,issued,due,returned`

Example:

- `admin,admin123,ADMIN,a1,2026-09-18`
- `B001,Intro to Java,Herbert Schildt,5,5`
- `abc123,B001,alice,2026-09-18,2026-10-02,`

Constraints:

- username is file name, must be unique
- book id must be unique
- issue is active if returned is empty
