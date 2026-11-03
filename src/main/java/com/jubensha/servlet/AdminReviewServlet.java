package com.jubensha.servlet;

import com.jubensha.dao.ReviewDao;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 后台评价管理：显示 / 隐藏 / 删除。 */
@WebServlet("/admin/reviews")
public class AdminReviewServlet extends HttpServlet {

    private final ReviewDao reviewDao = new ReviewDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("reviews", reviewDao.findAll());
            WebUtil.forward(req, resp, "admin/reviews");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = WebUtil.intParam(req, "id", 0);
        String action = WebUtil.param(req, "action");
        try {
            if ("hide".equals(action)) {
                reviewDao.updateVisible(id, 0);
                WebUtil.flash(req, "success", "评价已隐藏");
            } else if ("show".equals(action)) {
                reviewDao.updateVisible(id, 1);
                WebUtil.flash(req, "success", "评价已恢复显示");
            } else if ("delete".equals(action)) {
                reviewDao.delete(id);
                WebUtil.flash(req, "success", "评价已删除");
            }
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/reviews");
    }
}
