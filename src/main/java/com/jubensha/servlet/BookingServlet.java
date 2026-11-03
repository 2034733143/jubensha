package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.dao.ScriptDao;
import com.jubensha.dao.SessionDao;
import com.jubensha.model.Booking;
import com.jubensha.model.GameSession;
import com.jubensha.model.Script;
import com.jubensha.model.User;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** 预约下单：GET 显示表单，POST 创建预约。 */
@WebServlet("/booking")
public class BookingServlet extends HttpServlet {

    private final SessionDao sessionDao = new SessionDao();
    private final ScriptDao scriptDao = new ScriptDao();
    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sessionId = WebUtil.intParam(req, "sessionId", 0);
        try {
            GameSession session = sessionDao.findById(sessionId);
            if (session == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "场次不存在");
                return;
            }
            Script script = scriptDao.findById(session.getScriptId());
            User user = WebUtil.currentUser(req);
            req.setAttribute("gameSession", session);
            req.setAttribute("script", script);
            req.setAttribute("defaultName", user.getDisplayName());
            req.setAttribute("defaultPhone", user.getPhone());
            WebUtil.forward(req, resp, "booking_form");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int sessionId = WebUtil.intParam(req, "sessionId", 0);
        int playerCount = WebUtil.intParam(req, "playerCount", 1);
        String contactName = WebUtil.param(req, "contactName");
        String contactPhone = WebUtil.param(req, "contactPhone");
        String remark = WebUtil.param(req, "remark");
        try {
            GameSession session = sessionDao.findById(sessionId);
            if (session == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "场次不存在");
                return;
            }
            if (playerCount < 1 || playerCount > 10) {
                throw new IllegalArgumentException("预约人数需在 1-10 人之间");
            }
            if (contactName == null || contactName.isEmpty()) {
                throw new IllegalArgumentException("请填写联系人姓名");
            }
            if (contactPhone == null || !contactPhone.matches("1[3-9]\\d{9}")) {
                throw new IllegalArgumentException("请填写正确的 11 位手机号");
            }
            Booking booking = new Booking();
            booking.setSessionId(sessionId);
            booking.setUserId(user.getId());
            booking.setPlayerCount(playerCount);
            booking.setContactName(contactName);
            booking.setContactPhone(contactPhone);
            booking.setRemark(remark == null ? "" : remark);
            booking.setAmount(BigDecimal.ZERO);

            bookingDao.create(booking);
            WebUtil.flash(req, "success", "预约提交成功，编号 " + booking.getCode() + "，请在“我的预约”中查看");
            resp.sendRedirect(req.getContextPath() + "/my/bookings");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            try {
                GameSession session = sessionDao.findById(sessionId);
                req.setAttribute("gameSession", session);
                if (session != null) {
                    req.setAttribute("script", scriptDao.findById(session.getScriptId()));
                }
            } catch (Exception ignored) {
                // 回显失败时仅展示错误信息
            }
            req.setAttribute("defaultName", contactName);
            req.setAttribute("defaultPhone", contactPhone);
            req.setAttribute("playerCount", playerCount);
            req.setAttribute("remark", remark);
            WebUtil.forward(req, resp, "booking_form");
        }
    }
}
