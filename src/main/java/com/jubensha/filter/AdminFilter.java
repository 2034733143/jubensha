package com.jubensha.filter;

import com.jubensha.model.User;
import com.jubensha.util.WebUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 后台权限校验。 */
@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User user = WebUtil.currentUser(req);
        if (user == null) {
            WebUtil.flash(req, "warning", "请先以管理员身份登录");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + WebUtil.loginRedirect(req));
            return;
        }
        if (!user.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            WebUtil.forward(req, resp, "error");
            return;
        }
        chain.doFilter(request, response);
    }
}
