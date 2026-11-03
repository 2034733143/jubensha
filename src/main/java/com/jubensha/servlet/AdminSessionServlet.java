package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.dao.ScriptDao;
import com.jubensha.dao.SessionDao;
import com.jubensha.model.GameSession;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** 后台场次管理：列表 / 新增 / 编辑 / 开始 / 结束 / 取消。 */
@WebServlet("/admin/sessions")
public class AdminSessionServlet extends HttpServlet {

    private final SessionDao sessionDao = new SessionDao();
    private final ScriptDao scriptDao = new ScriptDao();
    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if ("form".equals(WebUtil.param(req, "action"))) {
                int id = WebUtil.intParam(req, "id", 0);
                req.setAttribute("gameSession", id > 0 ? sessionDao.findById(id) : null);
                req.setAttribute("scripts", scriptDao.findAll(null, null, null, false, "new"));
                WebUtil.forward(req, resp, "admin/session_form");
                return;
            }
            String status = WebUtil.param(req, "status");
            String date = WebUtil.param(req, "date");
            req.setAttribute("sessions", sessionDao.findAll(status, date));
            req.setAttribute("status", status);
            req.setAttribute("date", date);
            WebUtil.forward(req, resp, "admin/sessions");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = WebUtil.param(req, "action");
        try {
            if ("status".equals(action)) {
                int id = WebUtil.intParam(req, "id", 0);
                String status = WebUtil.param(req, "status");
                sessionDao.updateStatus(id, status);
                if ("FINISHED".equals(status)) {
                    bookingDao.finishBySession(id);
                }
                WebUtil.flash(req, "success", "场次状态已更新为 " + status);
            } else if ("save".equals(action)) {
                GameSession session = new GameSession();
                session.setId(WebUtil.intParam(req, "id", 0));
                session.setScriptId(WebUtil.intParam(req, "scriptId", 0));
                session.setRoom(WebUtil.param(req, "room"));
                session.setDmName(WebUtil.param(req, "dmName"));
                session.setStartTime(parse(req.getParameter("startTime")));
                session.setEndTime(parse(req.getParameter("endTime")));
                session.setCapacity(WebUtil.intParam(req, "capacity", 6));
                session.setPrice(new BigDecimal(WebUtil.param(req, "price") == null ? "0" : WebUtil.param(req, "price")));
                session.setStatus("OPEN");

                if (session.getScriptId() <= 0) {
                    throw new IllegalArgumentException("请选择剧本");
                }
                if (session.getStartTime() == null || session.getEndTime() == null) {
                    throw new IllegalArgumentException("请填写开始与结束时间");
                }
                if (!session.getEndTime().after(session.getStartTime())) {
                    throw new IllegalArgumentException("结束时间必须晚于开始时间");
                }
                if (session.getCapacity() < 1) {
                    throw new IllegalArgumentException("座位数必须大于 0");
                }
                if (session.getId() > 0) {
                    sessionDao.update(session);
                    WebUtil.flash(req, "success", "场次已更新");
                } else {
                    sessionDao.insert(session);
                    WebUtil.flash(req, "success", "场次已新增");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/sessions");
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/sessions");
        }
    }

    private Timestamp parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.parse(value.trim()));
    }
}
