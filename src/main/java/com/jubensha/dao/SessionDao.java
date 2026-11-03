package com.jubensha.dao;

import com.jubensha.model.GameSession;
import com.jubensha.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** 场次数据访问。 */
public class SessionDao {

    private static final String BASE_SELECT =
            "SELECT gs.*, s.title AS script_title, s.category AS script_category " +
            "FROM game_sessions gs JOIN scripts s ON gs.script_id = s.id ";

    /** 前台可预约场次：未来的场次，可按日期、剧本筛选。 */
    public List<GameSession> findUpcoming(String date, Integer scriptId, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT)
                .append("WHERE gs.status IN ('OPEN','FULL') AND gs.start_time >= NOW() ");
        List<Object> args = new ArrayList<>();
        if (date != null && !date.isEmpty()) {
            sql.append("AND DATE(gs.start_time) = ? ");
            args.add(date);
        }
        if (scriptId != null) {
            sql.append("AND gs.script_id = ? ");
            args.add(scriptId);
        }
        sql.append("ORDER BY gs.start_time ASC ");
        if (limit > 0) {
            sql.append("LIMIT ").append(limit);
        }
        return query(sql.toString(), args);
    }

    /** 后台场次列表：包含已结束/已取消，可按状态与日期筛选。 */
    public List<GameSession> findAll(String status, String date) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1 = 1 ");
        List<Object> args = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql.append("AND gs.status = ? ");
            args.add(status);
        }
        if (date != null && !date.isEmpty()) {
            sql.append("AND DATE(gs.start_time) = ? ");
            args.add(date);
        }
        sql.append("ORDER BY gs.start_time DESC");
        return query(sql.toString(), args);
    }

    public GameSession findById(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT + "WHERE gs.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int insert(GameSession s) throws SQLException {
        String sql = "INSERT INTO game_sessions (script_id, room, dm_name, start_time, end_time, capacity, booked, price, status) " +
                "VALUES (?,?,?,?,?,?,0,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getScriptId());
            ps.setString(2, s.getRoom());
            ps.setString(3, s.getDmName());
            ps.setTimestamp(4, s.getStartTime());
            ps.setTimestamp(5, s.getEndTime());
            ps.setInt(6, s.getCapacity());
            ps.setBigDecimal(7, s.getPrice());
            ps.setString(8, s.getStatus() == null ? "OPEN" : s.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void update(GameSession s) throws SQLException {
        String sql = "UPDATE game_sessions SET script_id=?, room=?, dm_name=?, start_time=?, end_time=?, capacity=?, price=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getScriptId());
            ps.setString(2, s.getRoom());
            ps.setString(3, s.getDmName());
            ps.setTimestamp(4, s.getStartTime());
            ps.setTimestamp(5, s.getEndTime());
            ps.setInt(6, s.getCapacity());
            ps.setBigDecimal(7, s.getPrice());
            ps.setInt(8, s.getId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE game_sessions SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public int countToday() throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM game_sessions WHERE DATE(start_time) = CURDATE() AND status <> 'CANCELED'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<GameSession> query(String sql, List<Object> args) throws SQLException {
        List<GameSession> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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

    private GameSession map(ResultSet rs) throws SQLException {
        GameSession s = new GameSession();
        s.setId(rs.getInt("id"));
        s.setScriptId(rs.getInt("script_id"));
        s.setScriptTitle(rs.getString("script_title"));
        s.setScriptCategory(rs.getString("script_category"));
        s.setRoom(rs.getString("room"));
        s.setDmName(rs.getString("dm_name"));
        s.setStartTime(rs.getTimestamp("start_time"));
        s.setEndTime(rs.getTimestamp("end_time"));
        s.setCapacity(rs.getInt("capacity"));
        s.setBooked(rs.getInt("booked"));
        s.setPrice(rs.getBigDecimal("price"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
