package webserver.tcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
public class ServerThread implements Runnable {

  private static final Logger LOGGER = Loggers.from(ServerThread.class);
  private static final String DOCUMENT_ROOT = "assets";

  private static final HashMap<String, String> contentTypeMap =
    new HashMap<String, String>() {
      {
        put("html", "text/html");
        put("htm", "text/html");
        put("txt", "text/plain");
        put("css", "text/css");
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("gif", "image/gif");
      }
    };

  private Socket socket;

  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (InputStream input = socket.getInputStream();
          OutputStream output = socket.getOutputStream();) {

      Request request = parseRequest(input);

      createResponseHeader(output, request);
      createResponseBody(output, request.getPath());

      LOGGER.info(() -> Thread.currentThread().getName() + " " + request.getPath());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class Request {
    private String path;
    private String ext;

    public Request() {}

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getExt() {
      return ext;
    }

    public void setExt(String ext) {
      this.ext = ext;
    }
  }

  private static Request parseRequest(InputStream input) throws IOException {
    String line;
    Request request = new Request();
    while ((line = readLine(input)) != null) {
      if (line == "") break;
      if (line.startsWith("GET")) {
        String path = line.split(" ")[1];
        request.setPath(path);
        String[] tmp = path.split("\\.");
        request.setExt(tmp[tmp.length - 1]);
      }
    }
    return request;
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

  private static void createResponseHeader(OutputStream output, Request request) throws IOException {
    writeLine(output, "HTTP/1.1 200 OK");
    writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    writeLine(output, "Server: Sever03");
    writeLine(output, "Connection: close");
    writeLine(output, "Content-type: " + getContentType(request.getExt()));
    writeLine(output, "");
  }

  private static void writeLine(OutputStream output, String s) throws IOException {
    for (char ch : s.toCharArray()) {
      output.write(ch);
    }
    output.write('\r');
    output.write('\n');
  }

  private static String getContentType(String ext) {
    String contentType = contentTypeMap.get(ext.toLowerCase());
    if (contentType == null) {
      return "application/octet-stream";
    }
    return contentType;
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
