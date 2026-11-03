package com.jubensha.dao;

import com.jubensha.model.Script;
import com.jubensha.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** 剧本数据访问。 */
public class ScriptDao {

    private static final String BASE_SELECT =
            "SELECT s.*, " +
            " COALESCE((SELECT ROUND(AVG(r.rating),1) FROM reviews r WHERE r.script_id = s.id AND r.visible = 1), 0) AS avg_rating, " +
            " (SELECT COUNT(*) FROM reviews r WHERE r.script_id = s.id AND r.visible = 1) AS review_count, " +
            " (SELECT COUNT(*) FROM game_sessions g WHERE g.script_id = s.id AND g.status = 'OPEN' AND g.start_time >= NOW()) AS upcoming " +
            "FROM scripts s ";

    public List<Script> findAll(String keyword, String category, String difficulty,
                                boolean onlyActive, String sort) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1 = 1 ");
        List<Object> args = new ArrayList<>();
        if (onlyActive) {
            sql.append("AND s.status = 1 ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (s.title LIKE ? OR s.author LIKE ? OR s.summary LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            args.add(like);
            args.add(like);
            args.add(like);
        }
        if (category != null && !category.isEmpty()) {
            sql.append("AND s.category = ? ");
            args.add(category);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            sql.append("AND s.difficulty = ? ");
            args.add(difficulty);
        }
        if ("rating".equals(sort)) {
            sql.append("ORDER BY avg_rating DESC, review_count DESC ");
        } else if ("price".equals(sort)) {
            sql.append("ORDER BY s.price ASC ");
        } else if ("hot".equals(sort)) {
            sql.append("ORDER BY review_count DESC, avg_rating DESC ");
        } else {
            sql.append("ORDER BY s.created_at DESC ");
        }
        List<Script> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) {
                ps.setObject(i + 1, args.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Script> findTopRated(int limit) throws SQLException {
        String sql = BASE_SELECT + "WHERE s.status = 1 ORDER BY avg_rating DESC, review_count DESC LIMIT ?";
        List<Script> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Script findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int insert(Script s) throws SQLException {
        String sql = "INSERT INTO scripts (title, author, category, difficulty, min_players, max_players, " +
                "duration_min, price, cover_color, summary, description, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getTitle());
            ps.setString(2, s.getAuthor());
            ps.setString(3, s.getCategory());
            ps.setString(4, s.getDifficulty());
            ps.setInt(5, s.getMinPlayers());
            ps.setInt(6, s.getMaxPlayers());
            ps.setInt(7, s.getDurationMin());
            ps.setBigDecimal(8, s.getPrice());
            ps.setString(9, s.getCoverColor());
            ps.setString(10, s.getSummary());
            ps.setString(11, s.getDescription());
            ps.setInt(12, s.isStatus() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void update(Script s) throws SQLException {
        String sql = "UPDATE scripts SET title=?, author=?, category=?, difficulty=?, min_players=?, max_players=?, " +
                "duration_min=?, price=?, cover_color=?, summary=?, description=?, status=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getTitle());
            ps.setString(2, s.getAuthor());
            ps.setString(3, s.getCategory());
            ps.setString(4, s.getDifficulty());
            ps.setInt(5, s.getMinPlayers());
            ps.setInt(6, s.getMaxPlayers());
            ps.setInt(7, s.getDurationMin());
            ps.setBigDecimal(8, s.getPrice());
            ps.setString(9, s.getCoverColor());
            ps.setString(10, s.getSummary());
            ps.setString(11, s.getDescription());
            ps.setInt(12, s.isStatus() ? 1 : 0);
            ps.setInt(13, s.getId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, int status) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE scripts SET status=? WHERE id=?")) {
            ps.setInt(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<String> findCategories() throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT category FROM scripts ORDER BY category");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }

    private Script map(ResultSet rs) throws SQLException {
        Script s = new Script();
        s.setId(rs.getInt("id"));
        s.setTitle(rs.getString("title"));
        s.setAuthor(rs.getString("author"));
        s.setCategory(rs.getString("category"));
        s.setDifficulty(rs.getString("difficulty"));
        s.setMinPlayers(rs.getInt("min_players"));
        s.setMaxPlayers(rs.getInt("max_players"));
        s.setDurationMin(rs.getInt("duration_min"));
        s.setPrice(rs.getBigDecimal("price"));
        s.setCoverColor(rs.getString("cover_color"));
        s.setSummary(rs.getString("summary"));
        s.setDescription(rs.getString("description"));
        s.setStatus(rs.getInt("status") == 1);
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setAvgRating(rs.getDouble("avg_rating"));
        s.setReviewCount(rs.getInt("review_count"));
        s.setUpcomingSessions(rs.getInt("upcoming"));
        return s;
    }
}
