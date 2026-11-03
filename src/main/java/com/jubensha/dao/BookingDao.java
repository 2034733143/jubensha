package com.jubensha.dao;

import com.jubensha.model.Booking;
import com.jubensha.util.DBUtil;
import com.jubensha.util.Passwords;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** 预约数据访问（含座位锁定与状态流转）。 */
public class BookingDao {

    private static final String BASE_SELECT =
            "SELECT b.*, u.username, s.title AS script_title, gs.room, gs.start_time AS session_start " +
            "FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "JOIN game_sessions gs ON b.session_id = gs.id " +
            "JOIN scripts s ON gs.script_id = s.id ";

    /**
     * 创建预约：加行锁校验余位，写入预约并更新场次已订人数（同一事务）。
     */
    public int create(Booking booking) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int capacity;
                int booked;
                BigDecimal price;
                String status;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT capacity, booked, price, status FROM game_sessions WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, booking.getSessionId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("场次不存在或已被删除");
                        }
                        capacity = rs.getInt("capacity");
                        booked = rs.getInt("booked");
                        price = rs.getBigDecimal("price");
                        status = rs.getString("status");
                    }
                }
                if (!"OPEN".equals(status)) {
                    throw new IllegalStateException("该场次当前不可预约");
                }
                if (booked + booking.getPlayerCount() > capacity) {
                    throw new IllegalStateException("剩余座位不足，请减少人数或选择其它场次");
                }

                booking.setAmount(price.multiply(BigDecimal.valueOf(booking.getPlayerCount())));
                booking.setCode(Passwords.bookingCode());

                int bookingId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO bookings (code, session_id, user_id, player_count, amount, contact_name, " +
                        "contact_phone, status, remark) VALUES (?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, booking.getCode());
                    ps.setInt(2, booking.getSessionId());
                    ps.setInt(3, booking.getUserId());
                    ps.setInt(4, booking.getPlayerCount());
                    ps.setBigDecimal(5, booking.getAmount());
                    ps.setString(6, booking.getContactName());
                    ps.setString(7, booking.getContactPhone());
                    ps.setString(8, "PENDING");
                    ps.setString(9, booking.getRemark());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        bookingId = keys.next() ? keys.getInt(1) : 0;
                    }
                }

                int newBooked = booked + booking.getPlayerCount();
                String newStatus = newBooked >= capacity ? "FULL" : "OPEN";
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE game_sessions SET booked = ?, status = ? WHERE id = ?")) {
                    ps.setInt(1, newBooked);
                    ps.setString(2, newStatus);
                    ps.setInt(3, booking.getSessionId());
                    ps.executeUpdate();
                }

                conn.commit();
                return bookingId;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw e;
            }
        }
    }

    public List<Booking> findByUser(int userId) throws SQLException {
        return query(BASE_SELECT + "WHERE b.user_id = ? ORDER BY b.created_at DESC", userId);
    }

    public List<Booking> findAll(String status) throws SQLException {
        if (status != null && !status.isEmpty()) {
            List<Object> args = new ArrayList<>();
            args.add(status);
            return query(BASE_SELECT + "WHERE b.status = ? ORDER BY b.created_at DESC", args.toArray());
        }
        return query(BASE_SELECT + "ORDER BY b.created_at DESC", new Object[0]);
    }

    public Booking findById(int id) throws SQLException {
        List<Booking> list = query(BASE_SELECT + "WHERE b.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Booking findByBookingCode(String code) throws SQLException {
        List<Booking> list = query(BASE_SELECT + "WHERE b.code = ?", code);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 变更预约状态。取消时释放座位，确认/完成按状态机校验。
     */
    public void changeStatus(int bookingId, String newStatus) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int sessionId;
                int playerCount;
                String current;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT session_id, player_count, status FROM bookings WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, bookingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("预约不存在");
                        }
                        sessionId = rs.getInt("session_id");
                        playerCount = rs.getInt("player_count");
                        current = rs.getString("status");
                    }
                }
                if (!isAllowed(current, newStatus)) {
                    throw new IllegalStateException("当前状态（" + current + "）不允许变更为 " + newStatus);
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = ? WHERE id = ?")) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, bookingId);
                    ps.executeUpdate();
                }

                if ("CANCELED".equals(newStatus)) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE game_sessions SET booked = GREATEST(booked - ?, 0), " +
                            "status = CASE WHEN status = 'FULL' THEN 'OPEN' ELSE status END WHERE id = ?")) {
                        ps.setInt(1, playerCount);
                        ps.setInt(2, sessionId);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw e;
            }
        }
    }

    /** 场次结束时，把该场次所有已确认预约标记为已完成。 */
    public void finishBySession(int sessionId) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE bookings SET status = 'FINISHED' WHERE session_id = ? AND status IN ('CONFIRMED','PENDING')")) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        }
    }

    public int countByStatus(String status) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM bookings WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public BigDecimal sumRevenue() throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COALESCE(SUM(amount),0) FROM bookings WHERE status IN ('CONFIRMED','FINISHED')");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private boolean isAllowed(String current, String next) {
        switch (next) {
            case "CONFIRMED":
                return "PENDING".equals(current);
            case "FINISHED":
                return "CONFIRMED".equals(current) || "PENDING".equals(current);
            case "CANCELED":
                return "PENDING".equals(current) || "CONFIRMED".equals(current);
            default:
                return false;
        }
    }

    private List<Booking> query(String sql, Object... args) throws SQLException {
        List<Booking> list = new ArrayList<>();
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

    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setCode(rs.getString("code"));
        b.setSessionId(rs.getInt("session_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setPlayerCount(rs.getInt("player_count"));
        b.setAmount(rs.getBigDecimal("amount"));
        b.setContactName(rs.getString("contact_name"));
        b.setContactPhone(rs.getString("contact_phone"));
        b.setStatus(rs.getString("status"));
        b.setRemark(rs.getString("remark"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        b.setUsername(rs.getString("username"));
        b.setScriptTitle(rs.getString("script_title"));
        b.setRoom(rs.getString("room"));
        b.setSessionStart(rs.getTimestamp("session_start"));
        return b;
    }
}
