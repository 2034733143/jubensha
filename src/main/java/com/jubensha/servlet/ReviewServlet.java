package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.dao.ReviewDao;
import com.jubensha.dao.ScriptDao;
import com.jubensha.dao.SessionDao;
import com.jubensha.model.Booking;
import com.jubensha.model.GameSession;
import com.jubensha.model.Review;
import com.jubensha.model.User;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 发表评价：仅本人已完成的预约可评价一次。 */
@WebServlet("/my/review")
public class ReviewServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();
    private final SessionDao sessionDao = new SessionDao();
    private final ReviewDao reviewDao = new ReviewDao();
    private final ScriptDao scriptDao = new ScriptDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int bookingId = WebUtil.intParam(req, "bookingId", 0);
        try {
            Booking booking = bookingDao.findById(bookingId);
            if (booking == null || booking.getUserId() != user.getId()) {
                WebUtil.flash(req, "error", "预约不存在或无权操作");
                resp.sendRedirect(req.getContextPath() + "/my/bookings");
                return;
            }
            if (!"FINISHED".equals(booking.getStatus())) {
                WebUtil.flash(req, "warning", "只有已完成的本局才能评价");
                resp.sendRedirect(req.getContextPath() + "/my/bookings");
                return;
            }
            if (reviewDao.findByBooking(bookingId) != null) {
                WebUtil.flash(req, "warning", "该预约已经评价过啦");
                resp.sendRedirect(req.getContextPath() + "/my/bookings");
                return;
            }
            GameSession session = sessionDao.findById(booking.getSessionId());
            req.setAttribute("booking", booking);
            req.setAttribute("script", scriptDao.findById(session.getScriptId()));
            WebUtil.forward(req, resp, "review_form");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int bookingId = WebUtil.intParam(req, "bookingId", 0);
        int rating = WebUtil.intParam(req, "rating", 0);
        String content = WebUtil.param(req, "content");
        try {
            Booking booking = bookingDao.findById(bookingId);
            if (booking == null || booking.getUserId() != user.getId()) {
                throw new IllegalStateException("预约不存在或无权操作");
            }
            if (!"FINISHED".equals(booking.getStatus())) {
                throw new IllegalStateException("只有已完成的本局才能评价");
            }
            if (reviewDao.findByBooking(bookingId) != null) {
                throw new IllegalStateException("该预约已经评价过啦");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("请选择 1-5 星评分");
            }
            if (content == null || content.length() < 5) {
                throw new IllegalArgumentException("评价内容至少 5 个字");
            }
            if (content.length() > 500) {
                throw new IllegalArgumentException("评价内容不能超过 500 字");
            }
            GameSession session = sessionDao.findById(booking.getSessionId());
            Review review = new Review();
            review.setBookingId(bookingId);
            review.setScriptId(session.getScriptId());
            review.setUserId(user.getId());
            review.setRating(rating);
            review.setContent(content);
            reviewDao.insert(review);
            WebUtil.flash(req, "success", "评价发布成功，感谢你的反馈");
            resp.sendRedirect(req.getContextPath() + "/my/bookings");
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/my/bookings");
        }
    }
}
