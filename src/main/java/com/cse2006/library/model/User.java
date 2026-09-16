package com.cse2006.library.model;
import java.time.LocalDateTime;
public class User {
  private final String id;
  private final String username;
  private final String passwordHash;
  private final Role role;
  private final LocalDateTime createdAt;
  public User(String id, String username, String passwordHash, Role role) {
    this.id = id; this.username = username; this.passwordHash = passwordHash; this.role = role; this.createdAt = LocalDateTime.now();
  }
  public String id() { return id; }
  public String username() { return username; }
  public String passwordHash() { return passwordHash; }
  public Role role() { return role; }
  public LocalDateTime createdAt() { return createdAt; }
}
