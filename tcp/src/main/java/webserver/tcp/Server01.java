package webserver.tcp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yoshimasa Tanabe
 */
public class Server01 {

  public static void main(String... args) {
    try(ServerSocket server = new ServerSocket(8001);
        FileOutputStream recv = new FileOutputStream("output/server_recv.txt");
        FileInputStream send = new FileInputStream("input/server_send.txt")) {

      System.out.println("クライアントからの接続を待ちます。");
      Socket socket = server.accept();
      System.out.println("クライアント接続");

      int ch;

      InputStream input = socket.getInputStream();
      while ((ch = input.read()) != Globals.EOF.getValue()) {
        recv.write(ch);
      }

      OutputStream output = socket.getOutputStream();
      while ((ch = send.read()) != -1) {
        output.write(ch);
      }

      socket.close();
      System.out.println("クライアント接続終了");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
