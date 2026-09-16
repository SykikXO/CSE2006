package com.cse2006.library.repo;
import com.cse2006.library.model.Book;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class BookStore {
  private final Path file = Paths.get("data", "books.csv");
  public BookStore() { try { Files.createDirectories(file.getParent()); if (!Files.exists(file)) Files.createFile(file); } catch (IOException ignored) {} }
  public void add(Book b) {
    try {
      String line = String.join(",", b.id(), escape(b.title()), escape(b.author()), String.valueOf(b.total()), String.valueOf(b.available()));
      Files.writeString(file, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) { throw new RuntimeException(e); }
  }
  public Optional<Book> findById(String id) { return all().stream().filter(b -> b.id().equalsIgnoreCase(id)).findFirst(); }
  public List<Book> all() {
    List<Book> list = new ArrayList<>();
    try {
      if (!Files.exists(file)) return list;
      for (String line : Files.readAllLines(file)) {
        if (line.trim().isEmpty()) continue;
        try { list.add(parse(line)); } catch (Exception ignored) {}
      }
    } catch (IOException ignored) {}
    return list;
  }
  public List<Book> search(String q) {
    String s = q.toLowerCase();
    List<Book> res = new ArrayList<>();
    for (Book b : all()) if (b.title().toLowerCase().contains(s) || b.author().toLowerCase().contains(s) || b.id().toLowerCase().contains(s)) res.add(b);
    return res;
  }
  public void update(Book b) {
    List<Book> all = all();
    for (int i=0;i<all.size();i++) if (all.get(i).id().equalsIgnoreCase(b.id())) { all.set(i,b); break; }
    saveAll(all);
  }
  public boolean delete(String id) {
    List<Book> all = all();
    boolean removed = all.removeIf(b -> b.id().equalsIgnoreCase(id));
    if (removed) saveAll(all);
    return removed;
  }
  public int count() { return all().size(); }
  private void saveAll(List<Book> list) {
    try {
      List<String> lines = new ArrayList<>();
      for (Book b : list) lines.add(String.join(",", b.id(), escape(b.title()), escape(b.author()), String.valueOf(b.total()), String.valueOf(b.available())));
      Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) { throw new RuntimeException(e); }
  }
  private Book parse(String line) {
    String[] p = line.split(",", -1);
    return new Book(p[0], unescape(p[1]), unescape(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]));
  }
  private String escape(String s) { return s.replace(",", ";"); }
  private String unescape(String s) { return s.replace(";", ","); }
}
