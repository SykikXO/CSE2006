package com.cse2006.library.model;
public class Book {
  private final String id;
  private String title;
  private String author;
  private int total;
  private int available;
  public Book(String id, String title, String author, int total, int available) {
    this.id = id; this.title = title; this.author = author; this.total = total; this.available = available;
  }
  public String id() { return id; }
  public String title() { return title; }
  public String author() { return author; }
  public int total() { return total; }
  public int available() { return available; }
  public void setTitle(String t) { this.title = t; }
  public void setAuthor(String a) { this.author = a; }
  public void setTotal(int t) { this.total = t; }
  public void setAvailable(int a) { this.available = a; }
}
