package com.cse2006.library.repo;
import com.cse2006.library.model.Issue;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
public class IssueStore {
  private final Path file = Paths.get("data", "issues.csv");
  public IssueStore() { try { Files.createDirectories(file.getParent()); if (!Files.exists(file)) Files.createFile(file); } catch (IOException ignored) {} }
  public void add(Issue is) {
    try {
      String line = String.join(",", is.id(), is.bookId(), is.username(), is.issued().toString(), is.due().toString(), is.returned()==null?"":is.returned().toString());
      Files.writeString(file, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) { throw new RuntimeException(e); }
  }
  public List<Issue> all() {
    List<Issue> list = new ArrayList<>();
    try {
      if (!Files.exists(file)) return list;
      for (String line : Files.readAllLines(file)) {
        if (line.trim().isEmpty()) continue;
        try { list.add(parse(line)); } catch (Exception ignored) {}
      }
    } catch (IOException ignored) {}
    return list;
  }
  public List<Issue> byUser(String username) {
    List<Issue> res = new ArrayList<>();
    for (Issue i : all()) if (i.username().equalsIgnoreCase(username)) res.add(i);
    return res;
  }
  public List<Issue> activeByUser(String username) {
    List<Issue> res = new ArrayList<>();
    for (Issue i : byUser(username)) if (i.returned()==null) res.add(i);
    return res;
  }
  public Optional<Issue> findActive(String username, String bookId) {
    return all().stream().filter(i -> i.username().equalsIgnoreCase(username) && i.bookId().equalsIgnoreCase(bookId) && i.returned()==null).findFirst();
  }
  public void update(Issue updated) {
    List<Issue> all = all();
    for (int i=0;i<all.size();i++) if (all.get(i).id().equals(updated.id())) { all.set(i, updated); break; }
    saveAll(all);
  }
  public List<Issue> overdue() {
    List<Issue> res = new ArrayList<>();
    for (Issue i : all()) if (i.isOverdue()) res.add(i);
    return res;
  }
  private void saveAll(List<Issue> list) {
    try {
      List<String> lines = new ArrayList<>();
      for (Issue i : list) lines.add(String.join(",", i.id(), i.bookId(), i.username(), i.issued().toString(), i.due().toString(), i.returned()==null?"":i.returned().toString()));
      Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) { throw new RuntimeException(e); }
  }
  private Issue parse(String line) {
    String[] p = line.split(",", -1);
    LocalDate ret = p[5]==null||p[5].isEmpty()?null:LocalDate.parse(p[5]);
    return new Issue(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]), ret);
  }
}
