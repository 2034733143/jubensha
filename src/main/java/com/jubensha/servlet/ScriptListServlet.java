package com.jubensha.servlet;

import com.jubensha.dao.ScriptDao;
import com.jubensha.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 剧本库列表，支持关键词/类型/难度/排序筛选。 */
@WebServlet("/scripts")
public class ScriptListServlet extends HttpServlet {

    private final ScriptDao scriptDao = new ScriptDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = WebUtil.param(req, "keyword");
        String category = WebUtil.param(req, "category");
        String difficulty = WebUtil.param(req, "difficulty");
        String sort = WebUtil.param(req, "sort");
        try {
            req.setAttribute("scripts", scriptDao.findAll(keyword, category, difficulty, true, sort));
            req.setAttribute("categories", scriptDao.findCategories());
            req.setAttribute("keyword", keyword);
            req.setAttribute("category", category);
            req.setAttribute("difficulty", difficulty);
            req.setAttribute("sort", sort == null ? "new" : sort);
            WebUtil.forward(req, resp, "scripts");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
