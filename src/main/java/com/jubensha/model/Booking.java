package com.jubensha.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/** 预约实体。 */
public class Booking {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private String code;
    private int sessionId;
    private int userId;
    private int playerCount;
    private BigDecimal amount;
    private String contactName;
    private String contactPhone;
    private String status;
    private String remark;
    private Timestamp createdAt;

    private String username;
    private String scriptTitle;
    private String room;
    private Timestamp sessionStart;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScriptTitle() {
        return scriptTitle;
    }

    public void setScriptTitle(String scriptTitle) {
        this.scriptTitle = scriptTitle;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Timestamp getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(Timestamp sessionStart) {
        this.sessionStart = sessionStart;
    }

    public String getStatusText() {
        switch (status == null ? "" : status) {
            case "PENDING":
                return "待确认";
            case "CONFIRMED":
                return "已确认";
            case "FINISHED":
                return "已完成";
            case "CANCELED":
                return "已取消";
            default:
                return status;
        }
    }

    public String getCreatedText() {
        return createdAt == null ? "" : createdAt.toLocalDateTime().format(DATE_TIME_FMT);
    }

    public String getSessionStartText() {
        return sessionStart == null ? "" : sessionStart.toLocalDateTime().format(DATE_TIME_FMT);
    }

    public boolean isCancelable() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }

    public boolean isPayable() {
        return "PENDING".equals(status);
    }
}
