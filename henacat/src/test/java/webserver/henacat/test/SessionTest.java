package webserver.henacat.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Yoshimasa Tanabe
 */
public class SessionTest extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("text/plain");
    PrintWriter out = response.getWriter();

    HttpSession session = request.getSession(true);
    Integer counter = (Integer) session.getAttribute("counter");
    if (counter == null) {
      out.println("No session");
      session.setAttribute("counter", 1);
    } else {
      out.println("Counter.." + counter);
      session.setAttribute("counter", counter++);
    }
  }

}
