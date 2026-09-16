package com.cse2006.library.repo;
import com.cse2006.library.model.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
public class UserStore {
  private final Path dir = Paths.get("data", "users");
  public UserStore() { try { Files.createDirectories(dir); } catch (IOException ignored) {} }
  private Path fileFor(String username) { return dir.resolve(username.toLowerCase() + ".csv"); }
  public boolean exists(String username) { return Files.exists(fileFor(username)); }
  public void save(User u) {
    try {
      Files.createDirectories(dir);
      String line = String.join(",", u.username(), u.password(), u.role().name(), u.id(), u.createdAt().toString());
      Files.writeString(fileFor(u.username()), line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) { throw new RuntimeException("Failed to save user " + u.username(), e); }
  }
  public Optional<User> findByUsername(String username) {
    Path p = fileFor(username);
    if (!Files.exists(p)) return Optional.empty();
    try {
      String line = Files.readString(p).trim();
      if (line.isEmpty()) return Optional.empty();
      return Optional.of(parse(line));
    } catch (IOException e) { return Optional.empty(); }
  }
  public Collection<User> allUsers() {
    List<User> list = new ArrayList<>();
    try {
      if (!Files.exists(dir)) return list;
      try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.csv")) {
        for (Path p : ds) {
          try {
            String line = Files.readString(p).trim();
            if (!line.isEmpty()) list.add(parse(line));
          } catch (Exception ignored) {}
        }
      }
    } catch (IOException ignored) {}
    return list;
  }
  public int size() { return allUsers().size(); }
  private User parse(String line) {
    String[] parts = line.split(",", -1);
    if (parts.length < 3) throw new IllegalArgumentException("Bad csv: " + line);
    String username = parts[0].trim();
    String password = parts[1].trim();
    Role role = Role.valueOf(parts[2].trim().toUpperCase());
    String id = parts.length > 3 ? parts[3].trim() : username;
    if (id.isEmpty()) id = username;
    if (parts.length > 4 && !parts[4].trim().isEmpty()) {
      try {
        LocalDateTime t = LocalDateTime.parse(parts[4].trim());
        return new User(id, username, password, role, t);
      } catch (Exception ignored) {}
    }
    return new User(id, username, password, role);
  }
}
