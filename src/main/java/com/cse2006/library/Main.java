package com.cse2006.library;
import com.cse2006.library.model.*;
import com.cse2006.library.repo.*;
import com.cse2006.library.service.AuthService;
import com.cse2006.library.util.Ansi;
import java.time.LocalDate;
import java.util.*;
public class Main {
  private final Scanner sc = new Scanner(System.in);
  private final AuthService auth;
  private final BookStore books = new BookStore();
  private final IssueStore issues = new IssueStore();
  private User current;
  public Main(AuthService auth) { this.auth = auth; }
  public static void main(String[] args) {
    UserStore store = new UserStore();
    AuthService auth = new AuthService(store);
    try { auth.register("admin", "admin123", Role.ADMIN); auth.register("alice", "pass123", Role.MEMBER); } catch (Exception ignored) {}
    seedBooksIfEmpty();
    new Main(auth).loop();
  }
  private static void seedBooksIfEmpty() {
    BookStore b = new BookStore();
    if (b.count() == 0) {
      b.add(new Book("B001", "Intro to Java", "Herbert Schildt", 5, 5));
      b.add(new Book("B002", "Clean Code", "Robert Martin", 3, 3));
      b.add(new Book("B003", "Design Patterns", "GoF", 2, 2));
    }
  }
  private void loop() {
    while (true) {
      if (current == null) authMenu();
      else dashboard();
    }
  }
  private void authMenu() {
    header("Library Management System", "CLI Auth");
    System.out.println(Ansi.c(Ansi.DIM, "  Demo: admin / admin123  |  alice / pass123"));
    System.out.println();
    System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " Login");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Register as member");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " About");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "4.") + " Exit");
    System.out.println(Ansi.c(Ansi.DIM, "    Member registration here. Librarians are created by managers."));
    System.out.println();
    System.out.print(Ansi.c(Ansi.YELLOW, "  Select [1-4]: "));
    String ch = sc.nextLine().trim();
    switch (ch) {
      case "1" -> doLogin();
      case "2" -> doRegister();
      case "3" -> about();
      case "4" -> { System.out.println(Ansi.c(Ansi.GREEN, "\n  Goodbye!\n")); System.exit(0); }
      default -> { System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice, try 1 to 4.")); pause(); }
    }
  }
  private void doLogin() {
    header("Login", null);
    System.out.print("  Username: ");
    String u = sc.nextLine().trim();
    if (u.isEmpty()) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Username required")); pause(); return; }
    System.out.print("  Password: ");
    String p = sc.nextLine();
    try {
      current = auth.login(u, p);
      System.out.println(Ansi.c(Ansi.GREEN, "\n  Welcome, " + current.username() + " (" + current.role() + ")"));
      pause();
    } catch (IllegalArgumentException e) {
      System.out.println(Ansi.c(Ansi.RED, "\n  ! " + e.getMessage()));
      pause();
    }
  }
  private void doRegister() {
    header("Member Registration", null);
    System.out.println(Ansi.c(Ansi.DIM, "  Managers create librarian accounts via Admin -> Manage members"));
    System.out.println();
    System.out.print("  Choose username (3-20, letters/digits/_): ");
    String u = sc.nextLine().trim();
    if (u.isEmpty()) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Username cannot be empty")); pause(); return; }
    System.out.print("  Choose password (min 4 chars): ");
    String p = sc.nextLine();
    System.out.print("  Confirm password: ");
    String c = sc.nextLine();
    if (!p.equals(c)) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Passwords do not match")); pause(); return; }
    try {
      User created = auth.register(u, p, Role.MEMBER);
      System.out.println(Ansi.c(Ansi.GREEN, "\n  Registered " + created.username() + " as " + created.role() + ". Please login."));
      pause();
    } catch (IllegalArgumentException e) {
      System.out.println(Ansi.c(Ansi.RED, "\n  ! " + e.getMessage()));
      pause();
    }
  }
  private void about() {
    header("About", null);
    System.out.println("  CSE2006 Library Management System");
    System.out.println("  Modules: User auth, Catalog, Reporting");
    System.out.println("  Stack: Java 17, Maven");
    System.out.println("  Storage: CSV files in data/");
    System.out.println();
    System.out.println("  Auth: public register creates MEMBER only");
    System.out.println("  Librarian accounts: Admin -> Manage members -> Add librarian");
    System.out.println();
    System.out.println(Ansi.c(Ansi.DIM, "  Press enter to go back"));
    sc.nextLine();
  }
  private void dashboard() {
    if (current.role() == Role.ADMIN) adminDash();
    else memberDash();
  }
  private void adminDash() {
    header("Admin Dashboard", current.username());
    System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " Manage books");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Manage members");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " View reports");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "4.") + " List users (" + auth.store().size() + " total)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Logout");
    System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
    String ch = sc.nextLine().trim();
    switch (ch) {
      case "1" -> manageBooks();
      case "2" -> manageMembers();
      case "3" -> reports();
      case "4" -> {
        System.out.println();
        auth.store().allUsers().stream().sorted((a,b)->a.role().toString().compareTo(b.role().toString()))
          .forEach(u -> System.out.println("    - " + u.username() + " [" + Ansi.c(u.role()==Role.ADMIN?Ansi.BLUE:Ansi.GREEN, u.role().toString()) + "] id:" + u.id()));
        System.out.println();
        pause();
      }
      case "0" -> { System.out.println(Ansi.c(Ansi.YELLOW, "\n  Logged out.\n")); current = null; }
      default -> { System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice")); pause(); }
    }
  }
  private void manageBooks() {
    while (true) {
      header("Manage Books", current.username());
      System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " List all books");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Add book");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " Search books");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "4.") + " Edit book");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "5.") + " Delete book");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Back");
      System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
      String ch = sc.nextLine().trim();
      if (ch.equals("0")) return;
      if (ch.equals("1")) { listBooks(books.all()); continue; }
      if (ch.equals("2")) { addBook(); continue; }
      if (ch.equals("3")) { System.out.print("  Search: "); String q=sc.nextLine().trim(); listBooks(books.search(q)); continue; }
      if (ch.equals("4")) { editBook(); continue; }
      if (ch.equals("5")) { deleteBook(); continue; }
      System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice")); pause();
    }
  }
  private void listBooks(List<Book> list) {
    System.out.println();
    if (list.isEmpty()) System.out.println("  No books found.");
    else {
      System.out.println(String.format("  %-6s %-20s %-15s %s", "ID", "Title", "Author", "Avail/Total"));
      System.out.println("  ------------------------------------------------------");
      for (Book b : list) System.out.println(String.format("  %-6s %-20s %-15s %d/%d", b.id(), trim(b.title(),20), trim(b.author(),15), b.available(), b.total()));
    }
    System.out.println();
    pause();
  }
  private void addBook() {
    System.out.print("  ID (e.g. B004): "); String id=sc.nextLine().trim();
    if (id.isEmpty()) { System.out.println(Ansi.c(Ansi.RED, "  ! ID required")); pause(); return; }
    if (books.findById(id).isPresent()) { System.out.println(Ansi.c(Ansi.RED, "  ! ID already exists")); pause(); return; }
    System.out.print("  Title: "); String title=sc.nextLine().trim();
    System.out.print("  Author: "); String author=sc.nextLine().trim();
    System.out.print("  Copies: "); String c=sc.nextLine().trim();
    int total; try { total=Integer.parseInt(c); if (total<1) throw new Exception(); } catch (Exception e){ System.out.println(Ansi.c(Ansi.RED,"  ! Copies must be number >=1")); pause(); return; }
    if (title.isEmpty()||author.isEmpty()){ System.out.println(Ansi.c(Ansi.RED,"  ! Title and author required")); pause(); return; }
    books.add(new Book(id, title, author, total, total));
    System.out.println(Ansi.c(Ansi.GREEN,"  Added "+id)); pause();
  }
  private void editBook() {
    System.out.print("  Book ID to edit: "); String id=sc.nextLine().trim();
    Optional<Book> opt=books.findById(id);
    if (opt.isEmpty()){ System.out.println(Ansi.c(Ansi.RED,"  ! Not found")); pause(); return; }
    Book b=opt.get();
    System.out.print("  New title ["+b.title()+"]: "); String t=sc.nextLine().trim(); if (!t.isEmpty()) b.setTitle(t);
    System.out.print("  New author ["+b.author()+"]: "); String a=sc.nextLine().trim(); if (!a.isEmpty()) b.setAuthor(a);
    System.out.print("  New total ["+b.total()+"]: "); String ts=sc.nextLine().trim();
    if (!ts.isEmpty()){ try{int nt=Integer.parseInt(ts); int diff=nt-b.total(); b.setTotal(nt); b.setAvailable(b.available()+diff); if(b.available()<0) b.setAvailable(0);}catch(Exception e){System.out.println(Ansi.c(Ansi.RED,"  ! Invalid number, skipped"));}}
    books.update(b);
    System.out.println(Ansi.c(Ansi.GREEN,"  Updated")); pause();
  }
  private void deleteBook() {
    System.out.print("  Book ID to delete: "); String id=sc.nextLine().trim();
    if (books.delete(id)) System.out.println(Ansi.c(Ansi.GREEN,"  Deleted")); else System.out.println(Ansi.c(Ansi.RED,"  ! Not found"));
    pause();
  }
  private void manageMembers() {
    while (true) {
      header("Manage Members", current.username());
      System.out.println(Ansi.c(Ansi.DIM, "  Only managers can create librarian accounts"));
      System.out.println();
      System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " List all users");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Add member (student)");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " Add librarian (admin)");
      System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Back");
      System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
      String ch = sc.nextLine().trim();
      if (ch.equals("0")) return;
      if (ch.equals("1")) {
        System.out.println();
        auth.store().allUsers().stream().sorted((a,b)->a.username().compareToIgnoreCase(b.username()))
          .forEach(u -> System.out.println("    - " + u.username() + " [" + u.role() + "] id:" + u.id() + " since " + u.createdAt().toLocalDate()));
        System.out.println();
        pause(); continue;
      }
      if (ch.equals("2") || ch.equals("3")) {
        Role role = ch.equals("3") ? Role.ADMIN : Role.MEMBER;
        System.out.print("  Username: "); String u = sc.nextLine().trim();
        if (u.isEmpty()) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Username cannot be empty")); pause(); continue; }
        System.out.print("  Password: "); String p = sc.nextLine();
        System.out.print("  Confirm: "); String c = sc.nextLine();
        if (!p.equals(c)) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Passwords do not match")); pause(); continue; }
        try {
          User created = auth.register(u, p, role);
          System.out.println(Ansi.c(Ansi.GREEN, "\n  Created " + created.username() + " as " + created.role()));
        } catch (IllegalArgumentException e) { System.out.println(Ansi.c(Ansi.RED, "\n  ! " + e.getMessage())); }
        pause(); continue;
      }
      System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice")); pause();
    }
  }
  private void reports() {
    header("Reports", current.username());
    System.out.println("  Books total: " + books.count());
    int avail = books.all().stream().mapToInt(Book::available).sum();
    int total = books.all().stream().mapToInt(Book::total).sum();
    System.out.println("  Copies: " + avail + " available / " + total + " total");
    System.out.println("  Issues total: " + issues.all().size());
    System.out.println("  Active issues: " + issues.all().stream().filter(i->i.returned()==null).count());
    System.out.println("  Overdue: " + issues.overdue().size());
    System.out.println();
    if (!issues.overdue().isEmpty()){
      System.out.println("  Overdue list:");
      for (Issue i: issues.overdue()) System.out.println("    - " + i.username() + " has " + i.bookId() + " due " + i.due());
      System.out.println();
    }
    System.out.println("  Most borrowed book:");
    Map<String, Long> counts = new HashMap<>();
    for (Issue i: issues.all()) counts.put(i.bookId(), counts.getOrDefault(i.bookId(),0L)+1);
    counts.entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(e->{
      books.findById(e.getKey()).ifPresent(b-> System.out.println("    - " + b.title() + " ("+b.id()+") borrowed " + e.getValue() + " times"));
    });
    if (counts.isEmpty()) System.out.println("    - none yet");
    System.out.println();
    pause();
  }
  private void memberDash() {
    header("Member Dashboard", current.username());
    System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " Browse catalog");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Search books");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " Issue book");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "4.") + " Return book");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "5.") + " My books");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Logout");
    System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
    String ch = sc.nextLine().trim();
    switch(ch){
      case "1" -> listBooks(books.all());
      case "2" -> { System.out.print("  Search: "); String q=sc.nextLine().trim(); listBooks(books.search(q)); }
      case "3" -> issueBook();
      case "4" -> returnBook();
      case "5" -> myBooks();
      case "0" -> { System.out.println(Ansi.c(Ansi.YELLOW, "\n  Logged out.\n")); current = null; }
      default -> { System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice")); pause(); }
    }
  }
  private void issueBook() {
    System.out.print("  Book ID to issue: "); String id=sc.nextLine().trim();
    Optional<Book> opt=books.findById(id);
    if(opt.isEmpty()){ System.out.println(Ansi.c(Ansi.RED,"  ! Book not found")); pause(); return; }
    Book b=opt.get();
    if(b.available()<=0){ System.out.println(Ansi.c(Ansi.RED,"  ! No copies available")); pause(); return; }
    if(issues.findActive(current.username(), id).isPresent()){ System.out.println(Ansi.c(Ansi.RED,"  ! You already have this book")); pause(); return; }
    if(issues.activeByUser(current.username()).size()>=3){ System.out.println(Ansi.c(Ansi.RED,"  ! Limit 3 books at a time")); pause(); return; }
    String iid = UUID.randomUUID().toString().substring(0,6);
    LocalDate now=LocalDate.now(); LocalDate due=now.plusDays(14);
    issues.add(new Issue(iid, id, current.username(), now, due, null));
    b.setAvailable(b.available()-1); books.update(b);
    System.out.println(Ansi.c(Ansi.GREEN,"  Issued "+b.title()+" due "+due)); pause();
  }
  private void returnBook(){
    List<Issue> act=issues.activeByUser(current.username());
    if(act.isEmpty()){ System.out.println(Ansi.c(Ansi.RED,"  ! No active books")); pause(); return; }
    System.out.println("  Your books:");
    for(Issue i: act){ books.findById(i.bookId()).ifPresent(b-> System.out.println("    - "+b.id()+" "+b.title()+" due "+i.due())); }
    System.out.print("  Book ID to return: "); String id=sc.nextLine().trim();
    Optional<Issue> opt=issues.findActive(current.username(), id);
    if(opt.isEmpty()){ System.out.println(Ansi.c(Ansi.RED,"  ! Not found in your active list")); pause(); return; }
    Issue is=opt.get(); is.setReturned(LocalDate.now()); issues.update(is);
    books.findById(id).ifPresent(b->{ b.setAvailable(b.available()+1); books.update(b); });
    long overdue = 0;
    if(is.returned().isAfter(is.due())) overdue = java.time.temporal.ChronoUnit.DAYS.between(is.due(), is.returned());
    if(overdue>0) System.out.println(Ansi.c(Ansi.YELLOW,"  Returned late by "+overdue+" days, fine $"+(overdue*5)));
    else System.out.println(Ansi.c(Ansi.GREEN,"  Returned on time"));
    pause();
  }
  private void myBooks(){
    List<Issue> act=issues.activeByUser(current.username());
    System.out.println();
    if(act.isEmpty()) System.out.println("  No active books");
    else for(Issue i: act){
      String title=books.findById(i.bookId()).map(Book::title).orElse(i.bookId());
      System.out.println("  - "+title+" ("+i.bookId()+") issued "+i.issued()+" due "+i.due() + (i.isOverdue()?Ansi.c(Ansi.RED," OVERDUE"):""));
    }
    System.out.println();
    pause();
  }
  private String trim(String s,int n){ return s.length()<=n?s:s.substring(0,n-3)+"..."; }
  private void header(String title, String subtitle) {
    clear();
    System.out.println();
    System.out.println(Ansi.c(Ansi.BLUE, "  +--------------------------------------+"));
    System.out.println(Ansi.c(Ansi.BLUE, "  |  ") + Ansi.c(Ansi.BOLD, title) + (subtitle != null ? Ansi.c(Ansi.DIM, " - " + subtitle) : "") + pad(title, subtitle));
    System.out.println(Ansi.c(Ansi.BLUE, "  +--------------------------------------+"));
    System.out.println();
  }
  private String pad(String title, String subtitle) {
    int len = title.length() + (subtitle != null ? subtitle.length() + 3 : 0);
    int total = 38 - len;
    if (total < 2) total = 2;
    return " ".repeat(total) + Ansi.c(Ansi.BLUE, "|");
  }
  private void clear() { System.out.print("\u001B[2J\u001B[H"); System.out.flush(); }
  private void pause() { System.out.print(Ansi.c(Ansi.DIM, "\n  Press enter to continue...")); sc.nextLine(); }
}
