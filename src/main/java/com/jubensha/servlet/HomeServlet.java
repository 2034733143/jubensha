package com.jubensha.servlet;

import com.jubensha.dao.ScriptDao;
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

/** 首页：推荐剧本 + 今日场次 + 数据看板。 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final ScriptDao scriptDao = new ScriptDao();
    private final SessionDao sessionDao = new SessionDao();
    private final StatsDao statsDao = new StatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("featured", scriptDao.findTopRated(4));
            req.setAttribute("todaySessions",
                    sessionDao.findUpcoming(LocalDate.now().toString(), null, 6));
            req.setAttribute("tomorrowSessions",
                    sessionDao.findUpcoming(LocalDate.now().plusDays(1).toString(), null, 4));
            req.setAttribute("stats", statsDao.dashboard());
            WebUtil.forward(req, resp, "home");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
