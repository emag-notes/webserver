package webserver.henacat.servlet;

import webserver.henacat.core.Request;
import webserver.henacat.util.HttpMethod;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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

    HttpServletRequest httpServletRequest;
    switch (request.getMethod()) {
      case GET: {
        Map<String, String> parameters = stringToMap(request.getQuery());
        httpServletRequest = new HttpServletRequestImpl(HttpMethod.GET, parameters);
        break;
      }
      case POST: {
        int contentLength = Integer.parseInt(request.getHeader().get("CONTENT-LENGTH"));
        String line = readToSize(input, contentLength);
        Map<String, String> parameters = stringToMap(line);
        httpServletRequest = new HttpServletRequestImpl(HttpMethod.POST, parameters);
        break;
      }
      default: {
        throw new AssertionError("BAD METHOD:" + request.getMethod());
      }
    }

    HttpServletResponseImpl httpServletResponse = new HttpServletResponseImpl(request, output);
    info.servlet.service(httpServletRequest, httpServletResponse);

    httpServletResponse.printWriter.flush();
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
