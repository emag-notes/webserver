package webserver.henacat.core;

import webserver.henacat.servlet.ServletInfo;
import webserver.henacat.util.Loggers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
public class Hencacat {

  private static final Logger LOGGER = Loggers.from(Hencacat.class);

  public static void main(String[] args) {
    new Hencacat().run(args);
  }

  public void run(String... args) {
    if (args.length != 3) {
      System.exit(1);
    }
    ServletInfo.addServlet(args[0], args[1], args[2]);

    try (ServerSocket server = new ServerSocket(8001)) {
      LOGGER.info(() -> "Running Henacat");
      while (true) {
        Socket socket = server.accept();
        new Thread(new ServerThread(socket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
