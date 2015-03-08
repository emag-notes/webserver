package webserver.tcp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private static final String SERVER_NAME = "localhost:8001";

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
    OK(200, "OK"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),

    NOT_FOUND(404, "Not Found");

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
    private String host;

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

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }
  }

  public static Request parseRequest(InputStream input) throws IOException {
    String line;
    Request request = new Request();
    while ((line = IOUtils.readLine(input)) != null) {
      if (line == "") break;
      if (line.startsWith("GET")) {
        String path = URLDecoder.decode(line.split(" ")[1], StandardCharsets.UTF_8.name()) ;
        request.setPath(path);
        String[] tmp = path.split("\\.");
        request.setExt(tmp[tmp.length - 1]);
      } else if (line.startsWith("HOST:")) {
        request.setHost(line.substring("HOST: ".length()));
      }
    }

    if (request.getPath().endsWith("/")) {
      request.setPath(request.getPath() + "index.html");
      request.setExt("html");
    }

    return request;
  }

  public static StatusCode calcStatusCode(Request request) throws IOException {

    StatusCode responseStatusCode = null;
    String targetPath = DOCUMENT_ROOT + request.getPath();
    try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath))) {
      responseStatusCode = StatusCode.OK;
    } catch (FileNotFoundException e) {
      FileSystem fs = FileSystems.getDefault();
      Path targetPathObj = fs.getPath(targetPath);
      if (Files.isDirectory(targetPathObj)) {
        responseStatusCode = StatusCode.MOVED_PERMANENTLY;
      } else {
        responseStatusCode = StatusCode.NOT_FOUND;
      }
    }
    return responseStatusCode;
  }

  public static void createResponse(OutputStream output, Request request, StatusCode statusCode) throws IOException {
    createResponseHeader(output, request, statusCode);
    if (statusCode != StatusCode.MOVED_PERMANENTLY) {
      createResponseBody(output, request.path, statusCode);
    }
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
      case MOVED_PERMANENTLY:
      case NOT_FOUND:
        IOUtils.writeLine(output, "Content-type: text/html");
        break;
      default:
        throw new RuntimeException("invalid status"); // never
    }
    if (status == StatusCode.MOVED_PERMANENTLY) {
      IOUtils.writeLine(output, "Location: " + resolveLocation(request));
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

  private static String resolveLocation(Request request) {
    return "http://" + (request.getHost() != null ? request.getHost() :  SERVER_NAME) + request.getPath() + "/";
  }

  private static void createResponseBody(OutputStream output, String path, StatusCode statusCode) throws IOException {
    String actualPath;
    switch (statusCode) {
      case OK:
        actualPath = DOCUMENT_ROOT + path;
        break;
      case MOVED_PERMANENTLY:
        return;
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
