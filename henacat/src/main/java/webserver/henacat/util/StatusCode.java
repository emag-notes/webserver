package webserver.henacat.util;

/**
 * @author Yoshimasa Tanabe
 */
public enum  StatusCode {

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
