package webserver.henacat.servlet;

import webserver.henacat.util.IOUtils;
import webserver.henacat.util.ResponseHeaderGenerator;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author Yoshimasa Tanabe
 */
public class ResponseHeaderGeneratorImpl implements ResponseHeaderGenerator {

  private List<Cookie> cookies;

  ResponseHeaderGeneratorImpl(List<Cookie> cookies) {
    this.cookies = cookies;
  }

  public void generate(OutputStream output) throws IOException {
    for (Cookie cookie : cookies) {
      String header;
      header = "Set-Cookie: " + cookie.getName() + "=" + cookie.getValue();

      if (cookie.getDomain() != null) {
        header += "; Domain=" + cookie.getDomain();
      }
      if (cookie.getMaxAge() > 0) {
        header += "; Expires=" +
          ZonedDateTime.now(ZoneId.of(String.valueOf(ZoneOffset.UTC)))
            .format(DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy HH:mm:ss", Locale.US));
      } else if (cookie.getMaxAge() == 0) {
        header += "; Expires=" +
          ZonedDateTime.of(1970, 1, 1, 0, 0, 10, 0, ZoneId.of(String.valueOf(ZoneOffset.UTC)))
            .format(DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy HH:mm:ss", Locale.US));;
      }
      if (cookie.getPath() != null) {
        header += "; Path=" + cookie.getPath();
      }
      if (cookie.getSecure()) {
        header += "; Secure";
      }
      if (cookie.isHttpOnly()) {
        header += "; httpOnly";
      }
      IOUtils.writeLine(output, header);
    }
  }

}
