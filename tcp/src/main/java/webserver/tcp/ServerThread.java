package webserver.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Yoshimasa Tanabe
 */
public class ServerThread implements Runnable {

  private static final Logger LOGGER = Loggers.from(ServerThread.class);

  private Socket socket;

  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (InputStream input = socket.getInputStream();
          OutputStream output = socket.getOutputStream();) {

      HttpUtils.Request request = HttpUtils.parseRequest(input);

      HttpUtils.createResponseHeader(output, request);
      HttpUtils.createResponseBody(output, request.getPath());

      LOGGER.info(() -> Thread.currentThread().getName() + " " + request.getPath());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
