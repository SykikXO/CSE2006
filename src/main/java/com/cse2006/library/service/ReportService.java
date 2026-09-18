package com.cse2006.library.service;
import com.cse2006.library.model.Book;
import com.cse2006.library.model.Issue;
import com.cse2006.library.repo.BookStore;
import com.cse2006.library.repo.IssueStore;
import java.util.*;
public class ReportService {
  private BookStore books = new BookStore();
  private IssueStore issues = new IssueStore();
  public int totalBooks() { return books.all().size(); }
  public int totalCopies() { int t=0; for(Book b: books.all()) t+=b.total(); return t; }
  public int availableCopies() { int a=0; for(Book b: books.all()) a+=b.available(); return a; }
  public int totalIssues() { return issues.all().size(); }
  public int activeIssues() { int c=0; for(Issue i: issues.all()) if(i.returned()==null) c++; return c; }
  public List<Issue> overdueList() { return issues.overdue(); }
  public String mostBorrowed() {
    Map<String,Integer> map = new HashMap<>();
    for(Issue i: issues.all()){
      String k = i.bookId();
      if(!map.containsKey(k)) map.put(k,1); else map.put(k, map.get(k)+1);
    }
    String best = null; int max = 0;
    for(String k: map.keySet()) if(map.get(k) > max){ max=map.get(k); best=k; }
    if(best==null) return "none";
    Optional<Book> b = books.findById(best);
    if(b.isPresent()) return b.get().title() + " ("+best+") borrowed " + max + " times";
    return best + " borrowed " + max + " times";
  }
}
