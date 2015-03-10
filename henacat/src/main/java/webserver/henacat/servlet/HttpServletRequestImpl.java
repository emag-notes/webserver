package webserver.henacat.servlet;

import webserver.henacat.util.HttpMethod;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author Yoshimasa Tanabe
 */
class HttpServletRequestImpl implements HttpServletRequest {

  private final String SESSION_COOKIE_ID = "JSESSIONID";

  private HttpMethod method;
  private String characterEncoding;
  private Map<String, String> headers;
  private Map<String, String> parameters;
  private Cookie[] cookies;
  private HttpSessionImpl session;
  private HttpServletResponseImpl response;


  public HttpServletRequestImpl(HttpMethod method, Map<String, String> headers, Map<String, String> parameters,
                                HttpServletResponseImpl response) {
    this.method = method;
    this.headers = headers;
    this.parameters = parameters;
    this.cookies = parseCookies(headers.get("COOKIE"));
    this.response = response;
    this.session = getSessionInternal();
    if (this.session != null) {
      addSessionCookie();
    }
  }


  private HttpSessionImpl getSessionInternal() {
    if (cookies == null) {
      return null;
    }
    Cookie cookie = null;
    for (Cookie tmp : cookies) {
      if (tmp.getName().equals(SESSION_COOKIE_ID)) {
        cookie = tmp;
      }
    }
    SessionManager manager = SessionManager.getInstance();
    HttpSessionImpl session = null;
    if (cookie != null) {
      session = manager.getSession(cookie.getValue());
    }
    return session;
  }
  private Cookie[] parseCookies(String cookie) {
    if (cookie == null) {
      return null;
    }
    String[] cookieArray = cookie.split(";");
    Cookie[] cookies = new Cookie[cookieArray.length];

    int count = 0;
    for (String cookiePair : cookieArray) {
      String[] keyValue = cookiePair.split("=", 2);
      cookies[count] = new Cookie(keyValue[0], keyValue[1]);
      count++;
    }
    return cookies;
  }

  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    return cookies;
  }

  @Override
  public long getDateHeader(String name) {
    return 0;
  }

  @Override
  public String getHeader(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return null;
  }

  @Override
  public int getIntHeader(String name) {
    return 0;
  }

  @Override
  public String getMethod() {
    return this.method.name();
  }

  @Override
  public String getPathInfo() {
    return null;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getContextPath() {
    return null;
  }

  @Override
  public String getQueryString() {
    return null;
  }

  @Override
  public String getRemoteUser() {
    return null;
  }

  @Override
  public boolean isUserInRole(String role) {
    return false;
  }

  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    return null;
  }

  @Override
  public String getRequestURI() {
    return null;
  }

  @Override
  public StringBuffer getRequestURL() {
    return null;
  }

  @Override
  public String getServletPath() {
    return null;
  }

  @Override
  public HttpSession getSession(boolean create) {
    if (!create) {
      return session;
    }
    if (session == null) {
      SessionManager manager = SessionManager.getInstance();
      session = manager.createSession();
      addSessionCookie();
    }
    return this.session;
  }

  private void addSessionCookie() {
    response.addCookie(new Cookie(SESSION_COOKIE_ID, session.getId() + "; HttpOnly"));
  }

  @Override
  public HttpSession getSession() {
    return getSession(true);
  }

  @Override
  public String changeSessionId() {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    return false;
  }

  @Override
  public void login(String username, String password) throws ServletException {

  }

  @Override
  public void logout() throws ServletException {

  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return null;
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    return null;
  }

  @Override
  public Object getAttribute(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
    if (!Charset.isSupported(env)) {
      throw new UnsupportedEncodingException("encoding.." + env);
    }
    this.characterEncoding = env;
  }

  @Override
  public int getContentLength() {
    return 0;
  }

  @Override
  public long getContentLengthLong() {
    return 0;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  @Override
  public String getParameter(String name) {
    String value = this.parameters.get(name);
    String decoded = null;
    try {
      decoded = URLDecoder.decode(value, this.characterEncoding);
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError(e);
    }
    return decoded;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return null;
  }

  @Override
  public String[] getParameterValues(String name) {
    return new String[0];
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return null;
  }

  @Override
  public String getProtocol() {
    return null;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public String getServerName() {
    return null;
  }

  @Override
  public int getServerPort() {
    return 0;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return null;
  }

  @Override
  public String getRemoteAddr() {
    return null;
  }

  @Override
  public String getRemoteHost() {
    return null;
  }

  @Override
  public void setAttribute(String name, Object o) {

  }

  @Override
  public void removeAttribute(String name) {

  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public Enumeration<Locale> getLocales() {
    return null;
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  @Override
  public String getRealPath(String path) {
    return null;
  }

  @Override
  public int getRemotePort() {
    return 0;
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public String getLocalAddr() {
    return null;
  }

  @Override
  public int getLocalPort() {
    return 0;
  }

  @Override
  public ServletContext getServletContext() {
    return null;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    return null;
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    return null;
  }

  @Override
  public boolean isAsyncStarted() {
    return false;
  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    return null;
  }

  @Override
  public DispatcherType getDispatcherType() {
    return null;
  }

}
