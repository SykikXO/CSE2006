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
}
