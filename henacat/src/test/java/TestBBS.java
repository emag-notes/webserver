import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yoshimasa Tanabe
 */
public class TestBBS extends HttpServlet {

  // Thread Unsafe
  private List<Message> messageList = new ArrayList<>();

  private String htmlEscape(String src) {
    return src
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    response.setContentType("text/html;charset=UTF-8");

    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head>");
    out.println("<title>テスト掲示板</title>");
    out.println("<head>");
    out.println("<body>");
    out.println("<h1>テスト掲示板</h1>");
    out.println("<form action='/testbbs/TestBBS' method='post'>");
    out.println("ハンドル名:<input type='text' name='handle'><br/>");
    out.println("<textarea name='message' rows='4' cols='60'></textarea><br/>");
    out.println("<input type='submit'/>");
    out.println("</form>");
    out.println("<hr/>");
    messageList.forEach(message -> {
      out.println("<p>" + htmlEscape(message.handle) + " さん</p>");
      out.println("<p>");
      out.println(htmlEscape(message.message).replace("\r\n", "<br/>"));
      out.println("</p><hr/>");
    });
    out.println("</body>");
    out.println("</html>");

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    request.setCharacterEncoding("UTF-8");
    Message newMessage = new Message(request.getParameter("handle"), request.getParameter("message"));
    messageList.add(0, newMessage);
    doGet(request, response);

  }
}
