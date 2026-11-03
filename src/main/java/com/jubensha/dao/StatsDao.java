package com.jubensha.dao;

import com.jubensha.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** 首页与后台看板的统计数据。 */
public class StatsDao {

    public Map<String, Object> dashboard() throws SQLException {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("scriptCount", scalar("SELECT COUNT(*) FROM scripts WHERE status = 1"));
        stats.put("sessionToday", scalar("SELECT COUNT(*) FROM game_sessions WHERE DATE(start_time) = CURDATE() AND status <> 'CANCELED'"));
        stats.put("pendingBookings", scalar("SELECT COUNT(*) FROM bookings WHERE status = 'PENDING'"));
        stats.put("totalBookings", scalar("SELECT COUNT(*) FROM bookings"));
        stats.put("revenue", scalarDecimal("SELECT COALESCE(SUM(amount),0) FROM bookings WHERE status IN ('CONFIRMED','FINISHED')"));
        stats.put("avgRating", scalarDecimal("SELECT COALESCE(ROUND(AVG(rating),1),0) FROM reviews WHERE visible = 1"));
        stats.put("userCount", scalar("SELECT COUNT(*) FROM users"));
        stats.put("reviewCount", scalar("SELECT COUNT(*) FROM reviews WHERE visible = 1"));
        return stats;
    }

    private int scalar(String sql) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private double scalarDecimal(String sql) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }
}
