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

/** 需要登录才能访问的路径。 */
@WebFilter(urlPatterns = {"/my/*", "/booking", "/review"})
public class AuthFilter implements Filter {

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
            WebUtil.flash(req, "warning", "请先登录后再进行预约操作");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + WebUtil.loginRedirect(req));
            return;
        }
        chain.doFilter(request, response);
    }
}
