package com.cse2006.library.util;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import java.io.IOException;
import java.util.Scanner;
public class InputHelper {
  private final Scanner scanner;
  private Terminal terminal;
  private LineReader lineReader;
  private boolean interactive;
  public InputHelper() {
    this.scanner = new Scanner(System.in);
    try {
      Terminal t = TerminalBuilder.builder().system(true).dumb(false).build();
      // ponytail: if dumb terminal (piped input, tests) fall back to scanner, no jline peek
      if (t != null && !"dumb".equalsIgnoreCase(t.getType()) && System.console() != null) {
        this.terminal = t;
        this.lineReader = LineReaderBuilder.builder().terminal(t).build();
        this.interactive = true;
      } else {
        if (t != null) try { t.close(); } catch (IOException ignored) {}
        this.terminal = null;
        this.interactive = false;
      }
    } catch (Exception e) {
      this.terminal = null;
      this.interactive = false;
    }
  }
  public String readLine(String prompt) {
    if (interactive && lineReader != null) {
      try { return lineReader.readLine(prompt); } catch (Exception e) { return scanner.nextLine(); }
    }
    System.out.print(prompt);
    System.out.flush();
    if (scanner.hasNextLine()) return scanner.nextLine();
    return "";
  }
  // ponytail: 1 sec peek then mask to *, blocking per char is simplest and correct for duty cycle
  public String readPassword(String prompt) {
    if (!interactive || terminal == null) {
      System.out.print(prompt);
      System.out.flush();
      if (scanner.hasNextLine()) return scanner.nextLine();
      return "";
    }
    terminal.writer().print(prompt);
    terminal.writer().flush();
    StringBuilder sb = new StringBuilder();
    Attributes prev = null;
    try {
      prev = terminal.enterRawMode();
      var reader = terminal.reader();
      while (true) {
        int c = reader.read();
        if (c == -1 || c == 10 || c == 13) {
          terminal.writer().println();
          terminal.writer().flush();
          break;
        }
        if (c == 127 || c == 8) {
          if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            terminal.writer().print("\b \b");
            terminal.writer().flush();
          }
          continue;
        }
        if (c < 32) continue;
        char ch = (char) c;
        sb.append(ch);
        terminal.writer().print(ch);
        terminal.writer().flush();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        terminal.writer().print("\b*");
        terminal.writer().flush();
      }
    } catch (IOException e) {
      return scanner.hasNextLine() ? scanner.nextLine() : sb.toString();
    } finally {
      if (prev != null) try { terminal.setAttributes(prev); } catch (Exception ignored) {}
      try { terminal.writer().flush(); } catch (Exception ignored) {}
    }
    return sb.toString();
  }
}
