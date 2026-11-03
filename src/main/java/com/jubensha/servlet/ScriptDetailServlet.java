package com.jubensha.servlet;

import com.jubensha.dao.ReviewDao;
import com.jubensha.dao.ScriptDao;
import com.jubensha.dao.SessionDao;
import com.jubensha.model.Script;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 剧本详情：简介、评价、可预约场次。 */
@WebServlet("/script")
public class ScriptDetailServlet extends HttpServlet {

    private final ScriptDao scriptDao = new ScriptDao();
    private final ReviewDao reviewDao = new ReviewDao();
    private final SessionDao sessionDao = new SessionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = WebUtil.intParam(req, "id", 0);
        try {
            Script script = scriptDao.findById(id);
            if (script == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "剧本不存在");
                return;
            }
            req.setAttribute("script", script);
            req.setAttribute("reviews", reviewDao.findByScript(id));
            req.setAttribute("sessions", sessionDao.findUpcoming(null, id, 8));
            WebUtil.forward(req, resp, "script_detail");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
