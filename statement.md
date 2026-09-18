# Statement

## Problem statement

College library still uses registers and Excel. It is hard to know which books are available, who took what, and when to return. Students wait, librarian does manual work, and records get lost. We need a simple CLI app to manage this.

## Scope of the project

### In scope

- User login and register
- Add, view, edit and delete books
- Issue and return books
- Show my books and due dates
- Simple reports for admin

### Out of scope

- Online payment
- Email or SMS
- Barcode scanning
- Web or mobile app

## Target users

### Admin (librarian)

- Goals: add books, see stock, see who has books, manage users
- Pain points: lot of manual entries, no quick search

### Member (student)

- Goals: search books, issue and return easily, see my list
- Pain points: not knowing if book is available, forgetting due date

## High-level features

| ID | Feature | Module | Priority |
|---|---|---|---|
| HL1 | Register and login | User management | must |
| HL2 | Add and manage books | Catalog | must |
| HL3 | Issue and return | Circulation | must |
| HL4 | Reports and list | Reporting | must |
| HL5 | Save in CSV files | Storage | must |

See docs/DESIGN.md for full design.
