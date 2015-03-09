package webserver.henacat.servlet;

import webserver.henacat.core.Request;
import webserver.henacat.util.HttpUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Yoshimasa Tanabe
 */
public class HttpServletResponseImpl implements HttpServletResponse {

  private Request request;
  private String contentType = "application/octet-stream";
  private String characterEncoding = "ISO-8859-1";
  private OutputStream output;
  PrintWriter printWriter;

  public HttpServletResponseImpl(Request request, OutputStream output) {
    this.request = request;
    this.output = output;
  }

  @Override
  public void addCookie(Cookie cookie) {

  }

  @Override
  public boolean containsHeader(String name) {
    return false;
  }

  @Override
  public String encodeURL(String url) {
    return null;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return null;
  }

  @Override
  public String encodeUrl(String url) {
    return null;
  }

  @Override
  public String encodeRedirectUrl(String url) {
    return null;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {

  }

  @Override
  public void sendError(int sc) throws IOException {

  }

  @Override
  public void sendRedirect(String location) throws IOException {

  }

  @Override
  public void setDateHeader(String name, long date) {

  }

  @Override
  public void addDateHeader(String name, long date) {

  }

  @Override
  public void setHeader(String name, String value) {

  }

  @Override
  public void addHeader(String name, String value) {

  }

  @Override
  public void setIntHeader(String name, int value) {

  }

  @Override
  public void addIntHeader(String name, int value) {

  }

  @Override
  public void setStatus(int sc) {

  }

  @Override
  public void setStatus(int sc, String sm) {

  }

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public String getHeader(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    this.printWriter = new PrintWriter(new OutputStreamWriter(output, characterEncoding));
    HttpUtils.sendOkResponseHeader(printWriter, contentType);
    return this.printWriter;
  }

  @Override
  public void setCharacterEncoding(String charset) {
    this.characterEncoding = charset;
  }

  @Override
  public void setContentLength(int len) {

  }

  @Override
  public void setContentLengthLong(long len) {

  }

  @Override
  public void setContentType(String type) {
    this.contentType = type;
    String[] tmp = type.split(" *;");
    if (tmp.length > 1) {
      String[] keyValue = tmp[1].split("=");
      if (keyValue.length == 2 && keyValue[0].equals("charset")) {
        setCharacterEncoding(keyValue[1]);
      }
    }
  }

  @Override
  public void setBufferSize(int size) {

  }

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void flushBuffer() throws IOException {

  }

  @Override
  public void resetBuffer() {

  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {

  }

  @Override
  public void setLocale(Locale loc) {

  }

  @Override
  public Locale getLocale() {
    return null;
  }
}
