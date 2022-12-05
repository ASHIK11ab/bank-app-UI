package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import model.Bank;


public class ContactPageServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Bank bank = AppCache.getBank();
		req.setAttribute("supportEmail", bank.supportEmail);
		req.setAttribute("supportPhone", bank.supportPhone);
		req.getRequestDispatcher("/jsp/pages/contactPage.jsp").forward(req, res);
	}
}
