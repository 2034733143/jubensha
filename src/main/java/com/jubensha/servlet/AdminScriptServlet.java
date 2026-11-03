package com.jubensha.servlet;

import com.jubensha.dao.ScriptDao;
import com.jubensha.model.Script;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** 后台剧本管理：列表 / 新增 / 编辑 / 上下架。 */
@WebServlet("/admin/scripts")
public class AdminScriptServlet extends HttpServlet {

    private final ScriptDao scriptDao = new ScriptDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if ("form".equals(WebUtil.param(req, "action"))) {
                int id = WebUtil.intParam(req, "id", 0);
                Script script = id > 0 ? scriptDao.findById(id) : null;
                req.setAttribute("script", script);
                WebUtil.forward(req, resp, "admin/script_form");
                return;
            }
            String keyword = WebUtil.param(req, "keyword");
            String category = WebUtil.param(req, "category");
            req.setAttribute("scripts", scriptDao.findAll(keyword, category, null, false, "new"));
            req.setAttribute("categories", scriptDao.findCategories());
            req.setAttribute("keyword", keyword);
            req.setAttribute("category", category);
            WebUtil.forward(req, resp, "admin/scripts");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = WebUtil.param(req, "action");
        try {
            if ("toggle".equals(action)) {
                int id = WebUtil.intParam(req, "id", 0);
                int status = WebUtil.intParam(req, "status", 1);
                scriptDao.updateStatus(id, status);
                WebUtil.flash(req, "success", status == 1 ? "剧本已上架" : "剧本已下架");
            } else if ("save".equals(action)) {
                Script script = new Script();
                script.setId(WebUtil.intParam(req, "id", 0));
                script.setTitle(WebUtil.param(req, "title"));
                script.setAuthor(WebUtil.param(req, "author"));
                script.setCategory(WebUtil.param(req, "category"));
                script.setDifficulty(WebUtil.param(req, "difficulty"));
                script.setMinPlayers(WebUtil.intParam(req, "minPlayers", 1));
                script.setMaxPlayers(WebUtil.intParam(req, "maxPlayers", 1));
                script.setDurationMin(WebUtil.intParam(req, "durationMin", 120));
                script.setPrice(new BigDecimal(WebUtil.param(req, "price") == null ? "0" : WebUtil.param(req, "price")));
                script.setCoverColor(WebUtil.param(req, "coverColor"));
                script.setSummary(WebUtil.param(req, "summary"));
                script.setDescription(WebUtil.param(req, "description"));
                script.setStatus("1".equals(WebUtil.param(req, "status")));

                if (script.getTitle() == null || script.getTitle().isEmpty()) {
                    throw new IllegalArgumentException("剧本名称不能为空");
                }
                if (script.getMinPlayers() > script.getMaxPlayers()) {
                    throw new IllegalArgumentException("最少人数不能大于最多人数");
                }
                if (script.getId() > 0) {
                    scriptDao.update(script);
                    WebUtil.flash(req, "success", "剧本《" + script.getTitle() + "》已更新");
                } else {
                    scriptDao.insert(script);
                    WebUtil.flash(req, "success", "剧本《" + script.getTitle() + "》已新增");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/scripts");
        } catch (Exception e) {
            WebUtil.flash(req, "error", e.getMessage());
            if ("save".equals(action)) {
                req.setAttribute("error", e.getMessage());
                req.setAttribute("script", null);
                WebUtil.forward(req, resp, "admin/script_form");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/admin/scripts");
        }
    }
}
