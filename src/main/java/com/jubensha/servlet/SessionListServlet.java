package com.jubensha.servlet;

import com.jubensha.dao.SessionDao;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** 场次列表：默认展示未来场次，可按日期筛选。 */
@WebServlet("/sessions")
public class SessionListServlet extends HttpServlet {

    private final SessionDao sessionDao = new SessionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String date = WebUtil.param(req, "date");
        try {
            req.setAttribute("sessions", sessionDao.findUpcoming(date, null, 0));
            req.setAttribute("selectedDate", date);
            List<String> dates = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                dates.add(LocalDate.now().plusDays(i).toString());
            }
            req.setAttribute("dates", dates);
            WebUtil.forward(req, resp, "sessions");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
