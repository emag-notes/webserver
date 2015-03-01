package webserver.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yoshimasa Tanabe
 */
public class IOUtils {

  public static String readLine(InputStream input) throws IOException {
    int ch;
    String line = "";
    while ((ch = input.read()) != -1) {
      if (ch == '\r') {
        // do nothing
      } else if (ch == '\n') {
        break;
      } else {
        line += (char) ch;
      }
    }

    if (ch == -1) {
      return null;
    } else {
      return line;
    }
  }

  public static void writeLine(OutputStream output, String s) throws IOException {
    for (char ch : s.toCharArray()) {
      output.write(ch);
    }
    output.write('\r');
    output.write('\n');
  }

}
