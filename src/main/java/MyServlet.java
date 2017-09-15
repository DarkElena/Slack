import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



//@WebServlet("/")
public class MyServlet extends HttpServlet {

    @Override   
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	  response.setContentType("text/html");
    	  SlackMessenger messenger = new SlackMessenger();
          String message = request.getParameter("message").toString();
          String sender = request.getParameter("sender").toString();
         // System.out.println(message + " " + sender);
          String channel = request.getParameter("recipient").toString();
         // System.out.println(channel);
          String output = messenger.sendMessenge(sender, message, channel);
          request.setAttribute("output", output);
          RequestDispatcher dispatcher = request.getRequestDispatcher("Output.jsp");
          dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
    }
}