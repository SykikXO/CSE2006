package com.cse2006.library.model;
import java.time.LocalDate;
public class Issue {
  private final String id;
  private final String bookId;
  private final String username;
  private final LocalDate issued;
  private final LocalDate due;
  private LocalDate returned;
  public Issue(String id, String bookId, String username, LocalDate issued, LocalDate due, LocalDate returned) {
    this.id = id; this.bookId = bookId; this.username = username; this.issued = issued; this.due = due; this.returned = returned;
  }
  public String id() { return id; }
  public String bookId() { return bookId; }
  public String username() { return username; }
  public LocalDate issued() { return issued; }
  public LocalDate due() { return due; }
  public LocalDate returned() { return returned; }
  public void setReturned(LocalDate d) { this.returned = d; }
  public boolean isOverdue() { return returned == null && LocalDate.now().isAfter(due); }
}
