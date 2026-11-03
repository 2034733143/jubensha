package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 后台预约管理：确认 / 取消 / 完成。 */
@WebServlet("/admin/bookings")
public class AdminBookingServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String status = WebUtil.param(req, "status");
            req.setAttribute("bookings", bookingDao.findAll(status));
            req.setAttribute("status", status);
            WebUtil.forward(req, resp, "admin/bookings");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = WebUtil.intParam(req, "id", 0);
        String action = WebUtil.param(req, "action");
        String next = null;
        if ("confirm".equals(action)) {
            next = "CONFIRMED";
        } else if ("cancel".equals(action)) {
            next = "CANCELED";
        } else if ("finish".equals(action)) {
            next = "FINISHED";
        }
        try {
            if (next == null) {
                throw new IllegalStateException("不支持的操作");
            }
            bookingDao.changeStatus(id, next);
            WebUtil.flash(req, "success", "预约状态已更新");
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/bookings");
    }
}
