package servlet;

import java.io.IOException;
import java.net.Authenticator.RequestorType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServicesPageServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getPathInfo();
		if(path == null) {
			res.sendError(404);
			return;
		}
		
		switch(path) {
			case "/account": req.getRequestDispatcher("/jsp/pages/accountServices.jsp").forward(req, res); 
							 break;
			default:
					res.sendError(404);
		}
	}
}
