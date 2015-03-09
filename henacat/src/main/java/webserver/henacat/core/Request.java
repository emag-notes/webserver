package webserver.henacat.core;

import webserver.henacat.util.HttpMethod;

import java.util.Map;

/**
 * @author Yoshimasa Tanabe
 */
public class Request {

  private String path;
  private String ext;
  private Map<String, String> header;
  private HttpMethod method;
  private String query;

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

  public Map<String, String> getHeader() {
    return header;
  }

  public void setHeader(Map<String, String> header) {
    this.header = header;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
