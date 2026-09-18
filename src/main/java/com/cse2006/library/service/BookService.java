package com.cse2006.library.service;
import com.cse2006.library.model.Book;
import com.cse2006.library.repo.BookStore;
import java.util.List;
import java.util.Optional;
public class BookService {
  private BookStore store = new BookStore();
  public void addBook(String id, String title, String author, int total) {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("id needed");
    if (title == null || title.isEmpty()) throw new IllegalArgumentException("title needed");
    if (author == null || author.isEmpty()) throw new IllegalArgumentException("author needed");
    if (total < 1) throw new IllegalArgumentException("copies must be 1 or more");
    if (store.findById(id).isPresent()) throw new IllegalArgumentException("id already there");
    store.add(new Book(id, title, author, total, total));
  }
  public List<Book> getAll() { return store.all(); }
  public List<Book> search(String q) { return store.search(q); }
  public Optional<Book> getById(String id) { return store.findById(id); }
  public void updateBook(Book b) { store.update(b); }
  public boolean deleteBook(String id) { return store.delete(id); }
}
