package com.jubensha.util;

import com.jubensha.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/** Web 层通用工具。 */
public final class WebUtil {

    private WebUtil() {
    }

    public static void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    public static User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    public static String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? null : value.trim();
    }

    public static int intParam(HttpServletRequest req, String name, int defaultValue) {
        String value = param(req, name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    public static String loginRedirect(HttpServletRequest req) {
        String target = req.getRequestURI();
        String query = req.getQueryString();
        if (query != null && !query.isEmpty()) {
            target = target + "?" + query;
        }
        try {
            return URLEncoder.encode(target, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return target;
        }
    }
}
