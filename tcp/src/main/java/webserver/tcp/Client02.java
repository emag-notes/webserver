package webserver.tcp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Yoshimasa Tanabe
 */
public class Client02 {

  public static void main(String... args) {
    try (Socket socket = new Socket("localhost", 8888);
         FileInputStream send = new FileInputStream("input/client02_send.txt");
         FileOutputStream recv = new FileOutputStream("output/client02_recv.txt")) {

      int ch;

      System.out.println("サーバへリクエスト送信");
      OutputStream output = socket.getOutputStream();
      while ((ch = send.read()) != -1) {
        output.write(ch);
      }

      System.out.println("サーバからのレスポンス受信");
      InputStream input = socket.getInputStream();
      while ((ch = input.read()) != -1) {
        recv.write(ch);
      }

    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
