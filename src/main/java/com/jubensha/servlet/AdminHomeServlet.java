package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.dao.SessionDao;
import com.jubensha.dao.StatsDao;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/** 后台看板。 */
@WebServlet("/admin")
public class AdminHomeServlet extends HttpServlet {

    private final StatsDao statsDao = new StatsDao();
    private final SessionDao sessionDao = new SessionDao();
    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("stats", statsDao.dashboard());
            req.setAttribute("todaySessions",
                    sessionDao.findUpcoming(LocalDate.now().toString(), null, 10));
            req.setAttribute("pendingBookings", bookingDao.findAll("PENDING"));
            WebUtil.forward(req, resp, "admin/dashboard");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
