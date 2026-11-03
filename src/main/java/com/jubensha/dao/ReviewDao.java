package com.jubensha.dao;

import com.jubensha.model.Review;
import com.jubensha.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** 评价数据访问。 */
public class ReviewDao {

    private static final String BASE_SELECT =
            "SELECT r.*, u.username, s.title AS script_title, b.code AS booking_code " +
            "FROM reviews r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN scripts s ON r.script_id = s.id " +
            "JOIN bookings b ON r.booking_id = b.id ";

    public List<Review> findByScript(int scriptId) throws SQLException {
        return query(BASE_SELECT + "WHERE r.script_id = ? AND r.visible = 1 ORDER BY r.created_at DESC", scriptId);
    }

    public List<Review> findAll() throws SQLException {
        return query(BASE_SELECT + "ORDER BY r.created_at DESC");
    }

    public Review findByBooking(int bookingId) throws SQLException {
        List<Review> list = query(BASE_SELECT + "WHERE r.booking_id = ?", bookingId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (booking_id, script_id, user_id, rating, content, visible) VALUES (?,?,?,?,?,1)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getBookingId());
            ps.setInt(2, review.getScriptId());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getContent());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void updateVisible(int id, int visible) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE reviews SET visible = ? WHERE id = ?")) {
            ps.setInt(1, visible);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public double avgRating() throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COALESCE(ROUND(AVG(rating),1),0) FROM reviews WHERE visible = 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    private List<Review> query(String sql, Object... args) throws SQLException {
        List<Review> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setBookingId(rs.getInt("booking_id"));
        r.setScriptId(rs.getInt("script_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setRating(rs.getInt("rating"));
        r.setContent(rs.getString("content"));
        r.setVisible(rs.getInt("visible") == 1);
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUsername(rs.getString("username"));
        r.setScriptTitle(rs.getString("script_title"));
        r.setBookingCode(rs.getString("booking_code"));
        return r;
    }
}
