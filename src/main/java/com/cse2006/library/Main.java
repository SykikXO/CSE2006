package com.cse2006.library;
import com.cse2006.library.model.*;
import com.cse2006.library.repo.InMemoryStore;
import com.cse2006.library.service.AuthService;
import com.cse2006.library.util.Ansi;
import java.util.Scanner;
public class Main {
  private final Scanner sc = new Scanner(System.in);
  private final AuthService auth;
  private User current;
  public Main(AuthService auth) { this.auth = auth; }
  public static void main(String[] args) {
    InMemoryStore store = new InMemoryStore();
    AuthService auth = new AuthService(store);
    // seed demo users
    try { auth.register("admin", "admin123", Role.ADMIN); auth.register("alice", "pass123", Role.MEMBER); } catch (Exception ignored) {}
    new Main(auth).loop();
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
    System.out.println("  Stack: Java 17, Maven, stdlib only");
    System.out.println("  Storage: in-memory now, file csv next");
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
    System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " Manage books (coming soon)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " Manage members");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " View reports (coming soon)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "4.") + " List users (" + auth.store().size() + " total)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Logout");
    System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
    String ch = sc.nextLine().trim();
    switch (ch) {
      case "2" -> manageMembers();
      case "4" -> {
        System.out.println();
        auth.store().allUsers().stream()
          .sorted((a,b) -> a.role().toString().compareTo(b.role().toString()))
          .forEach(u -> System.out.println("    - " + u.username() + " [" + Ansi.c(u.role()==Role.ADMIN?Ansi.BLUE:Ansi.GREEN, u.role().toString()) + "] id:" + u.id()));
        System.out.println();
        pause();
      }
      case "0" -> { System.out.println(Ansi.c(Ansi.YELLOW, "\n  Logged out.\n")); current = null; }
      default -> {
        if (!ch.equals("1") && !ch.equals("3") && !ch.equals("0"))
          System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice"));
        else System.out.println(Ansi.c(Ansi.YELLOW, "\n  > Feature under construction. Catalog and reporting next phase."));
        pause();
      }
    }
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
        auth.store().allUsers().stream()
          .sorted((a,b) -> a.username().compareToIgnoreCase(b.username()))
          .forEach(u -> System.out.println("    - " + u.username() + " [" + u.role() + "] id:" + u.id() + " since " + u.createdAt().toLocalDate()));
        System.out.println();
        pause();
        continue;
      }
      if (ch.equals("2") || ch.equals("3")) {
        Role role = ch.equals("3") ? Role.ADMIN : Role.MEMBER;
        System.out.print("  Username: ");
        String u = sc.nextLine().trim();
        if (u.isEmpty()) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Username cannot be empty")); pause(); continue; }
        System.out.print("  Password: ");
        String p = sc.nextLine();
        System.out.print("  Confirm: ");
        String c = sc.nextLine();
        if (!p.equals(c)) { System.out.println(Ansi.c(Ansi.RED, "\n  ! Passwords do not match")); pause(); continue; }
        try {
          User created = auth.register(u, p, role);
          System.out.println(Ansi.c(Ansi.GREEN, "\n  Created " + created.username() + " as " + created.role()));
        } catch (IllegalArgumentException e) {
          System.out.println(Ansi.c(Ansi.RED, "\n  ! " + e.getMessage()));
        }
        pause();
        continue;
      }
      System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice")); pause();
    }
  }
  private void memberDash() {
    header("Member Dashboard", current.username());
    System.out.println("  " + Ansi.c(Ansi.CYAN, "1.") + " Browse catalog (coming soon)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "2.") + " My books (coming soon)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "3.") + " Search (coming soon)");
    System.out.println("  " + Ansi.c(Ansi.CYAN, "0.") + " Logout");
    System.out.print(Ansi.c(Ansi.YELLOW, "\n  Select: "));
    String ch = sc.nextLine().trim();
    if (ch.equals("0")) { System.out.println(Ansi.c(Ansi.YELLOW, "\n  Logged out.\n")); current = null; }
    else {
      if (!ch.equals("1") && !ch.equals("2") && !ch.equals("3"))
        System.out.println(Ansi.c(Ansi.RED, "\n  ! Invalid choice"));
      else System.out.println(Ansi.c(Ansi.YELLOW, "\n  > Feature under construction. Borrow and return next phase."));
      pause();
    }
  }
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
