package webserver.henacat.core;

import webserver.henacat.servlet.ServletInfo;
import webserver.henacat.servlet.ServletService;
import webserver.henacat.util.HttpUtils;
import webserver.henacat.util.Loggers;
import webserver.henacat.util.StatusCode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    StatusCode responseStatusCode = null;
    Request request = null;

    try (InputStream input = new BufferedInputStream(socket.getInputStream());
          OutputStream output = new BufferedOutputStream(socket.getOutputStream())) {

      request = HttpUtils.parseRequest(input);

      ServletInfo servletInfo = ServletInfo.searchServlet(request.getPath());
      if (servletInfo != null) {
        ServletService.doService(request, servletInfo, input, output);
        return;
      }

      responseStatusCode = HttpUtils.calcStatusCode(request);
      HttpUtils.sendResponse(output, request, responseStatusCode);

    } catch (Exception e) {
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
