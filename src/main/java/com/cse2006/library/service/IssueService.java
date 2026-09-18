package com.cse2006.library.service;
import com.cse2006.library.model.Book;
import com.cse2006.library.model.Issue;
import com.cse2006.library.repo.BookStore;
import com.cse2006.library.repo.IssueStore;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public class IssueService {
  private BookStore books = new BookStore();
  private IssueStore issues = new IssueStore();
  public String issueBook(String username, String bookId) {
    Book b = books.findById(bookId).orElse(null);
    if (b == null) return "Book not found";
    if (b.available() <= 0) return "No copies left";
    if (issues.findActive(username, bookId).isPresent()) return "You already have this book";
    if (issues.activeByUser(username).size() >= 3) return "You can take only 3 books";
    String id = UUID.randomUUID().toString().substring(0,6);
    LocalDate now = LocalDate.now();
    LocalDate due = now.plusDays(14);
    issues.add(new Issue(id, bookId, username, now, due, null));
    b.setAvailable(b.available() - 1);
    books.update(b);
    return null;
  }
  public String returnBook(String username, String bookId) {
    Issue is = issues.findActive(username, bookId).orElse(null);
    if (is == null) return "Not found in your list";
    is.setReturned(LocalDate.now());
    issues.update(is);
    Book b = books.findById(bookId).orElse(null);
    if (b != null) { b.setAvailable(b.available() + 1); books.update(b); }
    if (is.returned().isAfter(is.due())) {
      long days = java.time.temporal.ChronoUnit.DAYS.between(is.due(), is.returned());
      return "Late by " + days + " days, fine $" + (days * 5);
    }
    return "Returned";
  }
  public List<Issue> myBooks(String username) { return issues.activeByUser(username); }
  public List<Issue> allIssues() { return issues.all(); }
}
