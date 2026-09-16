package com.cse2006.library.repo;
import com.cse2006.library.model.User;
import java.util.*;
public class InMemoryStore {
  private final Map<String, User> byUsername = new HashMap<>();
  private final Map<String, User> byId = new HashMap<>();
  public Optional<User> findByUsername(String u) { return Optional.ofNullable(byUsername.get(u.toLowerCase())); }
  public Optional<User> findById(String id) { return Optional.ofNullable(byId.get(id)); }
  public void save(User u) { byUsername.put(u.username().toLowerCase(), u); byId.put(u.id(), u); }
  public Collection<User> allUsers() { return Collections.unmodifiableCollection(byUsername.values()); }
  public boolean exists(String username) { return byUsername.containsKey(username.toLowerCase()); }
  public int size() { return byUsername.size(); }
}
