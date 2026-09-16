package com.cse2006.library.service;
import com.cse2006.library.model.*;
import com.cse2006.library.repo.InMemoryStore;
import com.cse2006.library.util.Validator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;
public class AuthService {
  private final InMemoryStore store;
  public AuthService(InMemoryStore store) { this.store = store; }
  public User register(String username, String password, Role role) {
    String u = username.trim();
    Validator.username(u);
    Validator.password(password);
    if (store.exists(u)) throw new IllegalArgumentException("Username already taken");
    String hash = hash(password);
    User user = new User(UUID.randomUUID().toString().substring(0, 8), u, hash, role);
    store.save(user);
    return user;
  }
  public User login(String username, String password) {
    String u = username.trim();
    Optional<User> opt = store.findByUsername(u);
    if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
    User user = opt.get();
    if (!user.passwordHash().equals(hash(password))) throw new IllegalArgumentException("Wrong password");
    return user;
  }
  public static String hash(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] d = md.digest(password.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : d) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) { throw new RuntimeException(e); }
  }
  public InMemoryStore store() { return store; }
}
