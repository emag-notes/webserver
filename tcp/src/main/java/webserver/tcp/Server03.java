package webserver.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
public class Server03 {

  private static final Logger LOGGER = Loggers.from(Server03.class);

  public static void main(String[] args) {
    try (ServerSocket server = new ServerSocket(8001)) {
      LOGGER.info(() -> "Running Server03");
      while (true) {
        Socket socket = server.accept();
        new Thread(new ServerThread(socket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
