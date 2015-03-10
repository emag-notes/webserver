package webserver.henacat.util;

import webserver.henacat.core.Request;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yoshimasa Tanabe
 */
public class HttpUtils {

  private static final String DOCUMENT_ROOT = System.getProperty("webserver.henacat.util.HttpUtils.DOCUMENT_ROOT");
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

  public static Request parseRequest(InputStream input) throws IOException {
    String line;
    String requestLine = null;
    Request request = new Request();
    Map<String, String> requestHeader = new HashMap<>();
    while ((line = IOUtils.readLine(input)) != null) {
      if (line == "") break;
      if (line.startsWith(HttpMethod.GET.name())) {
        request.setMethod(HttpMethod.GET);
        requestLine = line;
      } else if (line.startsWith(HttpMethod.POST.name())) {
        request.setMethod(HttpMethod.POST);
        requestLine = line;
      } else {
        addRequestHeader(requestHeader, line);
      }
    }

    request.setHeader(requestHeader);
    String requestUri = URLDecoder.decode(requestLine.split(" ")[1], StandardCharsets.UTF_8.name());
    String[] pathAndQuery = requestUri.split("\\?");

    String path = pathAndQuery[0];
    request.setPath(path);
    if (pathAndQuery.length > 1) {
      request.setQuery(pathAndQuery[1]);
    }
    String[] tmpExt = path.split("\\.");
    request.setExt(tmpExt[tmpExt.length - 1]);

    if (request.getPath().endsWith("/")) {
      request.setPath(request.getPath() + "index.html");
      request.setExt("html");
    }

    return request;
  }

  private static void addRequestHeader(Map<String, String> requestHeader, String line) {
    int colonPos = line.indexOf(':');
    if (colonPos == -1) return;

    String headerName = line.substring(0, colonPos).toUpperCase();
    String headerValue = line.substring(colonPos + 1).trim();
    requestHeader.put(headerName, headerValue);
  }

  public static StatusCode calcStatusCode(Request request) throws IOException {

    StatusCode responseStatusCode = null;
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(DOCUMENT_ROOT + request.getPath());

    Path realPath;
    try {
      realPath = path.toRealPath();
    } catch (NoSuchFileException e) {
      return StatusCode.NOT_FOUND;
    }

    if (!realPath.startsWith(DOCUMENT_ROOT)) {
      return StatusCode.NOT_FOUND;
    } else if (Files.isDirectory(realPath)) {
      return StatusCode.MOVED_PERMANENTLY;
    }

    return StatusCode.OK;
  }

  public static void sendOkResponseHeader(OutputStream output, String contentType, ResponseHeaderGenerator generator) throws IOException {
    IOUtils.writeLine(output, "HTTP/1.1 200 OK");
    IOUtils.writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    IOUtils.writeLine(output, "Server: Henacat");
    IOUtils.writeLine(output, "Connection: close");
    IOUtils.writeLine(output, "Content-type: " + contentType);
    generator.generate(output);
    IOUtils.writeLine(output, "");
  }


  public static void sendFoundResponse(OutputStream output, String redirectLocation) throws IOException {
    IOUtils.writeLine(output, "HTTP/1.1 302 Found");
    IOUtils.writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    IOUtils.writeLine(output, "Server: Henacat");
    IOUtils.writeLine(output, "Location: " + redirectLocation);
    IOUtils.writeLine(output, "Connection: close");
    IOUtils.writeLine(output, "");
  }

  public static void sendResponse(OutputStream output, Request request, StatusCode statusCode) throws IOException {
    sendResponseHeader(output, request, statusCode);
    if (statusCode != StatusCode.MOVED_PERMANENTLY) {
      sendResponseBody(output, request.getPath(), statusCode);
    }
  }

  private static void sendResponseHeader(OutputStream output, Request request, StatusCode status) throws IOException {
    IOUtils.writeLine(output, "HTTP/1.1 " + status.number() + " " + status.string());
    IOUtils.writeLine(output, "Date: " + ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC))).format(DateTimeFormatter.RFC_1123_DATE_TIME));
    IOUtils.writeLine(output, "Server: Henacat");
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
    return "http://" + request.getHeader().getOrDefault("HOST", Constants.SERVER_NAME) + request.getPath() + "/";
  }

  private static void sendResponseBody(OutputStream output, String path, StatusCode statusCode) throws IOException {
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
