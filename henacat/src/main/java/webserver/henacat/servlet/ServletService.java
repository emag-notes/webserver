package webserver.henacat.servlet;

import webserver.henacat.core.Request;
import webserver.henacat.util.Constants;
import webserver.henacat.util.HttpMethod;
import webserver.henacat.util.HttpUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yoshimasa Tanabe
 */
public class ServletService {

  public static void doService(Request request, ServletInfo info,
                                    InputStream input, OutputStream output) throws Exception {

    if (info.servlet == null) {
      info.servlet = createServlet(info);
    }

    ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    HttpServletResponseImpl httpServletResponse = new HttpServletResponseImpl(outputBuffer);

    HttpServletRequest httpServletRequest;
    switch (request.getMethod()) {
      case GET: {
        Map<String, String> parameters = stringToMap(request.getQuery());
        httpServletRequest = new HttpServletRequestImpl(HttpMethod.GET, request.getHeader(), parameters, httpServletResponse);
        break;
      }
      case POST: {
        int contentLength = Integer.parseInt(request.getHeader().get("CONTENT-LENGTH"));
        String line = readToSize(input, contentLength);
        Map<String, String> parameters = stringToMap(line);
        httpServletRequest = new HttpServletRequestImpl(HttpMethod.POST, request.getHeader(), parameters, httpServletResponse);
        break;
      }
      default: {
        throw new AssertionError("BAD METHOD:" + request.getMethod());
      }
    }

    info.servlet.service(httpServletRequest, httpServletResponse);

    switch (httpServletResponse.getStatus()) {
      case HttpServletResponse.SC_OK:
        HttpUtils.sendOkResponseHeader(output, httpServletResponse.getContentType(), new ResponseHeaderGeneratorImpl(httpServletResponse.cookies));
        httpServletResponse.printWriter.flush();
        for (byte b : outputBuffer.toByteArray()) {
          output.write((int)b);
        }
        break;
      case HttpServletResponse.SC_FOUND:
        String redirectLocation;
        if (httpServletResponse.redirectLocation.startsWith("/")) {
          redirectLocation = "http://" + request.getHeader().getOrDefault("HOST", Constants.SERVER_NAME) + httpServletResponse.redirectLocation;
        } else {
          redirectLocation = httpServletResponse.redirectLocation;
        }
        HttpUtils.sendFoundResponse(output, redirectLocation);
    }

  }

  private static HttpServlet createServlet(ServletInfo info) throws Exception, InstantiationException {
    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath(info.servletDirectory);
    URLClassLoader loader = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()});
    Class<?> servlet = loader.loadClass(info.servletClassName);
    return (HttpServlet) servlet.newInstance();
  }

  private static Map<String, String> stringToMap(String query) {
    Map<String, String> parameters = new HashMap<>();
    if (query != null) {
      String[] queryArray = query.split("&");
      for (String param : queryArray) {
        String[] keyValue = param.split("=");
        parameters.put(keyValue[0], keyValue[1]);
      }
    }
    return parameters;
  }

  private static String readToSize(InputStream input, int size) throws IOException {
    int ch;
    StringBuilder sb = new StringBuilder();
    int readSize = 0;

    while (readSize < size && (ch = input.read()) != -1) {
      sb.append((char) ch);
      readSize++;
    }

    return sb.toString();
  }
}
