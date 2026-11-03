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
import java.io.IOException;

/** 注册。 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        WebUtil.forward(req, resp, "register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = WebUtil.param(req, "username");
        String password = WebUtil.param(req, "password");
        String realName = WebUtil.param(req, "realName");
        String phone = WebUtil.param(req, "phone");
        try {
            if (username == null || !username.matches("[A-Za-z0-9_]{3,20}")) {
                throw new IllegalArgumentException("用户名需为 3-20 位字母、数字或下划线");
            }
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("密码至少 6 位");
            }
            if (phone != null && !phone.isEmpty() && !phone.matches("1[3-9]\\d{9}")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
            if (userDao.findByUsername(username) != null) {
                throw new IllegalArgumentException("该用户名已被注册");
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(Passwords.sha256(password));
            user.setRealName(realName);
            user.setPhone(phone);
            user.setRole("USER");
            int id = userDao.insert(user);
            user.setId(id);
            req.getSession().setAttribute("user", user);
            WebUtil.flash(req, "success", "注册成功，已自动登录");
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("realName", realName);
            req.setAttribute("phone", phone);
            WebUtil.forward(req, resp, "register");
        }
    }
}
