package webserver.henacat.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
public class Loggers {

  protected static final String LOGGING_CONFIG =
    "handlers=java.util.logging.ConsoleHandler\n" +
      ".level=INFO\n" +
      "java.util.logging.ConsoleHandler.level=INFO\n" +
      "java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter\n" +
      "java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s [%3$s] %5$s (%2$s) %6$s%n";

  static {
    try {
      LogManager.getLogManager().readConfiguration(
        new ByteArrayInputStream(LOGGING_CONFIG.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Logger from(Class<?> clazz) {
    return Logger.getLogger(clazz.getName());
  }

}