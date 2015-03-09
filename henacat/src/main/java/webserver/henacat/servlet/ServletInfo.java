package webserver.henacat.servlet;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yoshimasa Tanabe
 */
public class ServletInfo {

  private static Map<String, ServletInfo> servletCollection = new HashMap<>();

  private String urlPattern;
  String servletDirectory;
  String servletClassName;
  HttpServlet servlet;

  public ServletInfo(String urlPattern, String servletDirectory, String servletClassName) {
    this.urlPattern = urlPattern;
    this.servletDirectory = servletDirectory;
    this.servletClassName = servletClassName;
  }

  public static void addServlet(String urlPattern, String servletDirectory, String servletClassName) {
    servletCollection.put(
      urlPattern,
      new ServletInfo(urlPattern, servletDirectory, servletClassName));
  }

  public static ServletInfo searchServlet(String urlPattern) {
    return servletCollection.get(urlPattern);
  }
}
