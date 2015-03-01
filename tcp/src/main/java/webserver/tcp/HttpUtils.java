package webserver.tcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author Yoshimasa Tanabe
 */
public class HttpUtils {

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

  public static class Request {
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

  public static Request parseRequest(InputStream input) throws IOException {
    String line;
    Request request = new Request();
    while ((line = IOUtils.readLine(input)) != null) {
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

  public static void createResponseHeader(OutputStream output, Request request) throws IOException {
    IOUtils.writeLine(output, "HTTP/1.1 200 OK");
    IOUtils.writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    IOUtils.writeLine(output, "Server: Sever03");
    IOUtils.writeLine(output, "Connection: close");
    IOUtils.writeLine(output, "Content-type: " + getContentType(request.getExt()));
    IOUtils.writeLine(output, "");
  }

  private static String getContentType(String ext) {
    String contentType = contentTypeMap.get(ext.toLowerCase());
    if (contentType == null) {
      return "application/octet-stream";
    }
    return contentType;
  }

  public static void createResponseBody(OutputStream output, String path) throws IOException {
    try (FileInputStream fis = new FileInputStream(DOCUMENT_ROOT + path)) {
      int ch;
      while ((ch = fis.read()) != -1) {
        output.write(ch);
      }
    }
  }

}
