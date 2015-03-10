package webserver.henacat.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yoshimasa Tanabe
 */
class HttpSessionImpl implements HttpSession {

  private String id;
  private Map<String, Object> attributs = new ConcurrentHashMap<>();
  private volatile long lastAccessedTime;

  public HttpSessionImpl(String id) {
    this.id = id;
    this.access();
  }


  synchronized void access() {
    lastAccessedTime = System.currentTimeMillis();
  }

  @Override
  public long getCreationTime() {
    return 0;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  @Override
  public ServletContext getServletContext() {
    return null;
  }

  @Override
  public void setMaxInactiveInterval(int interval) {

  }

  @Override
  public int getMaxInactiveInterval() {
    return 0;
  }

  @Override
  public HttpSessionContext getSessionContext() {
    return null;
  }

  @Override
  public Object getAttribute(String name) {
    return attributs.get(name);
  }

  @Override
  public Object getValue(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    Set<String> names = new HashSet<>();
    names.addAll(attributs.keySet());
    return Collections.enumeration(names);
  }

  @Override
  public String[] getValueNames() {
    return new String[0];
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) {
      removeAttribute(name);
      return;
    }
    attributs.put(name, value);
  }

  @Override
  public void putValue(String name, Object value) {

  }

  @Override
  public void removeAttribute(String name) {
    attributs.remove(name);
  }

  @Override
  public void removeValue(String name) {

  }

  @Override
  public void invalidate() {

  }

  @Override
  public boolean isNew() {
    return false;
  }

}
