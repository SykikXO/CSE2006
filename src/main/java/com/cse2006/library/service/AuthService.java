package com.cse2006.library.service;
import com.cse2006.library.model.*;
import com.cse2006.library.repo.InMemoryStore;
import com.cse2006.library.util.Validator;
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
    User user = new User(UUID.randomUUID().toString().substring(0, 8), u, password, role);
    store.save(user);
    return user;
  }
  public User login(String username, String password) {
    String u = username.trim();
    Optional<User> opt = store.findByUsername(u);
    if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
    User user = opt.get();
    if (!user.password().equals(password)) throw new IllegalArgumentException("Wrong password");
    return user;
  }
  public InMemoryStore store() { return store; }
}
