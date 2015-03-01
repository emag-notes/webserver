package webserver.tcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Yoshimasa Tanabe
 */
public class Server02 {

  private static final String DOCUMENT_ROOT = "assets";

  public static void main(String... args) {
    System.out.println("Running Server02");
    try(ServerSocket server = new ServerSocket(8001)) {
      Socket socket = server.accept();
      System.out.println("クライアント接続");

      InputStream input = socket.getInputStream();
      String path = readRequestedPath(input);

      OutputStream output = socket.getOutputStream();
      createResponseHeader(output);
      createResponseBody(output, path);

      socket.close();
      System.out.println("クライアント接続終了");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String readRequestedPath(InputStream input) throws IOException {
    String line;
    String path = null;
    while ((line = readLine(input)) != null) {
      if (line == "") break;
      if (line.startsWith("GET")) {
        path = line.split(" ")[1];

      }
    }
    return path;
  }

  private static String readLine(InputStream input) throws IOException {
    int ch;
    String line = "";
    while ((ch = input.read()) != -1) {
      if (ch == '\r') {
        // do nothing
      } else if (ch == '\n') {
        break;
      } else {
        line += (char) ch;
      }
    }

    if (ch == -1) {
      return null;
    } else {
      return line;
    }
  }

  private static void createResponseHeader(OutputStream output) throws IOException {
    writeLine(output, "HTTP/1.1 200 OK");
    writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    writeLine(output, "Server: Sever02");
    writeLine(output, "Connection: close");
    writeLine(output, "Content-type: text/html");
    writeLine(output, "");
  }

  private static void writeLine(OutputStream output, String s) throws IOException {
    for (char ch : s.toCharArray()) {
      output.write(ch);
    }
    output.write('\r');
    output.write('\n');
  }

  private static void createResponseBody(OutputStream output, String path) throws IOException {
    try (FileInputStream fis = new FileInputStream(DOCUMENT_ROOT + path)) {
      int ch;
      while ((ch = fis.read()) != -1) {
        output.write(ch);
      }
    }
  }
}
