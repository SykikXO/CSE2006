package com.cse2006.library.model;
import java.time.LocalDateTime;
public class User {
  private final String id;
  private final String username;
  private final String password;
  private final Role role;
  private final LocalDateTime createdAt;
  public User(String id, String username, String password, Role role) {
    this.id = id; this.username = username; this.password = password; this.role = role; this.createdAt = LocalDateTime.now();
  }
  public User(String id, String username, String password, Role role, LocalDateTime createdAt) {
    this.id = id; this.username = username; this.password = password; this.role = role; this.createdAt = createdAt;
  }
  public String id() { return id; }
  public String username() { return username; }
  public String password() { return password; }
  public Role role() { return role; }
  public LocalDateTime createdAt() { return createdAt; }
}
