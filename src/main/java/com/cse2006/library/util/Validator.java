package com.cse2006.library.util;
public final class Validator {
  private Validator() {}
  public static void username(String s) {
    if (s == null || s.isBlank() || s.length() < 3 || s.length() > 20) throw new IllegalArgumentException("Username 3 to 20 chars, no spaces");
    if (!s.matches("[a-zA-Z0-9_]+")) throw new IllegalArgumentException("Username only letters, digits and underscore");
  }
  public static void password(String s) {
    if (s == null || s.length() < 4) throw new IllegalArgumentException("Password at least 4 chars");
  }
}
