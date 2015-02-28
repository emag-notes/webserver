package webserver.tcp;

/**
 * @author Yoshimasa Tanabe
 */
public enum Globals {

  EOF(0);

  private int value;

  private Globals(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
