package com.jubensha.servlet;

import com.jubensha.dao.BookingDao;
import com.jubensha.dao.ReviewDao;
import com.jubensha.model.Booking;
import com.jubensha.model.Review;
import com.jubensha.model.User;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 我的预约：列表 + 取消 + 模拟支付。 */
@WebServlet("/my/bookings")
public class MyBookingServlet extends HttpServlet {

    private final BookingDao bookingDao = new BookingDao();
    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        try {
            List<Booking> bookings = bookingDao.findByUser(user.getId());
            Map<Integer, Review> reviewed = new HashMap<>();
            for (Booking b : bookings) {
                Review review = reviewDao.findByBooking(b.getId());
                if (review != null) {
                    reviewed.put(b.getId(), review);
                }
            }
            req.setAttribute("bookings", bookings);
            req.setAttribute("reviewed", reviewed);
            WebUtil.forward(req, resp, "my_bookings");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
