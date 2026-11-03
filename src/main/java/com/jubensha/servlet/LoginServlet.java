package com.jubensha.servlet;

import com.jubensha.dao.UserDao;
import com.jubensha.model.User;
import com.jubensha.util.Passwords;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;

/** 登录。 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (WebUtil.currentUser(req) != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.setAttribute("redirect", WebUtil.param(req, "redirect"));
        WebUtil.forward(req, resp, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = WebUtil.param(req, "username");
        String password = WebUtil.param(req, "password");
        String redirect = WebUtil.param(req, "redirect");
        try {
            User user = username == null ? null : userDao.findByUsername(username);
            if (user == null || !user.getPassword().equals(Passwords.sha256(password == null ? "" : password))) {
                req.setAttribute("error", "用户名或密码错误");
                req.setAttribute("username", username);
                req.setAttribute("redirect", redirect);
                WebUtil.forward(req, resp, "login");
                return;
            }
            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("user", user);
            WebUtil.flash(req, "success", "欢迎回来，" + user.getDisplayName());

            String target = safeRedirect(req, redirect);
            resp.sendRedirect(target);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String safeRedirect(HttpServletRequest req, String redirect) throws IOException {
        String ctx = req.getContextPath();
        if (redirect == null || redirect.isEmpty()) {
            return ctx + "/home";
        }
        String decoded = URLDecoder.decode(redirect, "UTF-8");
        if (decoded.startsWith(ctx + "/") && !decoded.contains("//")) {
            return decoded;
        }
        return ctx + "/home";
    }
}
