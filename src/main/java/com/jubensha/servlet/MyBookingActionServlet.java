package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.model.Booking;
import com.jubensha.model.User;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 用户取消预约 / 模拟支付确认。 */
@WebServlet("/my/booking/action")
public class MyBookingActionServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int bookingId = WebUtil.intParam(req, "bookingId", 0);
        String action = WebUtil.param(req, "action");
        try {
            Booking booking = bookingDao.findById(bookingId);
            if (booking == null || booking.getUserId() != user.getId()) {
                WebUtil.flash(req, "error", "预约不存在或无权操作");
                resp.sendRedirect(req.getContextPath() + "/my/bookings");
                return;
            }
            if ("cancel".equals(action)) {
                bookingDao.changeStatus(bookingId, "CANCELED");
                WebUtil.flash(req, "success", "预约 " + booking.getCode() + " 已取消，座位已释放");
            } else if ("pay".equals(action)) {
                bookingDao.changeStatus(bookingId, "CONFIRMED");
                WebUtil.flash(req, "success", "预约 " + booking.getCode() + " 支付成功，开场前 15 分钟到店即可");
            } else {
                WebUtil.flash(req, "error", "不支持的操作");
            }
            resp.sendRedirect(req.getContextPath() + "/my/bookings");
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/my/bookings");
        }
    }
}
