package webserver.henacat.servlet;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Yoshimasa Tanabe
 */
class SessionIdGenerator {

  private SecureRandom random;

  SessionIdGenerator() {
    try {
      random = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      System.out.println(e);
      e.printStackTrace();
      System.exit(1);
    }
  }

  public String generateSessionId() {
    byte[] bytes = new byte[16];
    random.nextBytes(bytes);
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < bytes.length; i++) {
      buffer.append(Integer.toHexString(bytes[i] & 0xff).toUpperCase());
    }
    return buffer.toString();
  }

}
