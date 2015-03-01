package webserver.tcp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
  private static final String ERROR_DOCUMENT_ROOT = DOCUMENT_ROOT + "/errors";

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

  public enum StatusCode {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    private int number;
    private String string;

    private StatusCode(int number, String string) {
      this.number = number;
      this.string = string;
    }

    public int number() {
      return number;
    }

    public String string() {
      return string;
    }
  }

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

  public static StatusCode calcStatusCode(Request request) throws IOException {
    StatusCode responseStatusCode = null;
    try (InputStream is = new BufferedInputStream(new FileInputStream(DOCUMENT_ROOT + request.getPath()))) {
      responseStatusCode = StatusCode.OK;
    } catch (FileNotFoundException e) {
      responseStatusCode = StatusCode.NOT_FOUND;
    }
    return responseStatusCode;
  }

  public static void createResponse(OutputStream output, Request request, StatusCode statusCode) throws IOException {
    createResponseHeader(output, request, statusCode);
    createResponseBody(output, request.path, statusCode);
  }

  private static void createResponseHeader(OutputStream output, Request request, StatusCode status) throws IOException {
    IOUtils.writeLine(output, "HTTP/1.1 " + status.number() + " " + status.string());
    IOUtils.writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    IOUtils.writeLine(output, "Server: Sever03");
    IOUtils.writeLine(output, "Connection: close");
    switch (status) {
      case OK:
        IOUtils.writeLine(output, "Content-type: " + getContentType(request.getExt()));
        break;
      case NOT_FOUND:
        IOUtils.writeLine(output, "Content-type: text/html");
        break;
      default:
        throw new RuntimeException("invalid status"); // never
    }
    IOUtils.writeLine(output, "");
  }

  private static String getContentType(String ext) {
    String contentType = contentTypeMap.get(ext.toLowerCase());
    if (contentType == null) {
      return "application/octet-stream";
    }
    return contentType;
  }

  private static void createResponseBody(OutputStream output, String path, StatusCode statusCode) throws IOException {
    String actualPath;
    switch (statusCode) {
      case OK:
        actualPath = DOCUMENT_ROOT + path;
        break;
      case NOT_FOUND:
        actualPath = ERROR_DOCUMENT_ROOT + "/404.html";
        break;
      default:
        throw new RuntimeException("invalid status"); // never
    }
    try (InputStream is = new BufferedInputStream(new FileInputStream(actualPath))) {
      int ch;
      while ((ch = is.read()) != -1) {
        output.write(ch);
      }
    }
  }

}
