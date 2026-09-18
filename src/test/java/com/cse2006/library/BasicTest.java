package com.cse2006.library;
import com.cse2006.library.model.*;
import com.cse2006.library.repo.*;
import com.cse2006.library.util.Validator;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BasicTest {
  @Test
  void usernameCheck() {
    assertDoesNotThrow(() -> Validator.username("alice_123"));
    assertThrows(IllegalArgumentException.class, () -> Validator.username("ab"));
    assertThrows(IllegalArgumentException.class, () -> Validator.username("bad name"));
  }
  @Test
  void passwordCheck() {
    assertDoesNotThrow(() -> Validator.password("abcd"));
    assertThrows(IllegalArgumentException.class, () -> Validator.password("a"));
  }
  @Test
  void bookStoreAddAndFind() {
    BookStore b = new BookStore();
    String id = "T" + System.currentTimeMillis() % 10000;
    Book book = new Book(id, "Test Title", "Test Author", 2, 2);
    b.add(book);
    assertTrue(b.findById(id).isPresent());
    assertEquals(2, b.findById(id).get().available());
  }
  @Test
  void userRegisterAndLogin() {
    UserStore s = new UserStore();
    com.cse2006.library.service.AuthService a = new com.cse2006.library.service.AuthService(s);
    String u = "testu" + System.currentTimeMillis() % 10000;
    a.register(u, "pass123", Role.MEMBER);
    assertTrue(s.exists(u));
    assertNotNull(a.login(u, "pass123"));
    assertThrows(IllegalArgumentException.class, () -> a.login(u, "wrong"));
  }
  @Test
  void bookServiceTest() {
    com.cse2006.library.service.BookService bs = new com.cse2006.library.service.BookService();
    String id = "X" + System.currentTimeMillis() % 10000;
    bs.addBook(id, "My Book", "Me", 1);
    assertTrue(bs.getById(id).isPresent());
    assertTrue(bs.search("My Book").size() > 0);
    assertTrue(bs.deleteBook(id));
    assertFalse(bs.getById(id).isPresent());
  }
  @Test
  void issueServiceTest() {
    com.cse2006.library.service.BookService bs = new com.cse2006.library.service.BookService();
    com.cse2006.library.service.IssueService is = new com.cse2006.library.service.IssueService();
    com.cse2006.library.repo.UserStore us = new com.cse2006.library.repo.UserStore();
    com.cse2006.library.service.AuthService au = new com.cse2006.library.service.AuthService(us);
    String u = "istu" + System.currentTimeMillis() % 10000;
    String bid = "IB" + System.currentTimeMillis() % 10000;
    au.register(u, "pass123", Role.MEMBER);
    bs.addBook(bid, "Issue Book", "Auth", 2);
    assertNull(is.issueBook(u, bid));
    String again = is.issueBook(u, bid);
    assertTrue(again != null);
    assertEquals(1, is.myBooks(u).size());
    String ret = is.returnBook(u, bid);
    assertTrue(ret.equals("Returned") || ret.startsWith("Late"));
  }
  @Test
  void reportTest() {
    com.cse2006.library.service.ReportService r = new com.cse2006.library.service.ReportService();
    assertTrue(r.totalBooks() >= 0);
    assertTrue(r.totalCopies() >= 0);
  }
}
