package com.jubensha.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/** 场次实体（避免与 javax.servlet.http.HttpSession 混淆，类名使用 GameSession）。 */
public class GameSession {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final String[] WEEK = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private int id;
    private int scriptId;
    private String scriptTitle;
    private String scriptCategory;
    private String room;
    private String dmName;
    private Timestamp startTime;
    private Timestamp endTime;
    private int capacity;
    private int booked;
    private BigDecimal price;
    private String status;
    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScriptId() {
        return scriptId;
    }

    public void setScriptId(int scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptTitle() {
        return scriptTitle;
    }

    public void setScriptTitle(String scriptTitle) {
        this.scriptTitle = scriptTitle;
    }

    public String getScriptCategory() {
        return scriptCategory;
    }

    public void setScriptCategory(String scriptCategory) {
        this.scriptCategory = scriptCategory;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDmName() {
        return dmName;
    }

    public void setDmName(String dmName) {
        this.dmName = dmName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getBooked() {
        return booked;
    }

    public void setBooked(int booked) {
        this.booked = booked;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getRemaining() {
        return Math.max(capacity - booked, 0);
    }

    public boolean isBookable() {
        return "OPEN".equals(status) && getRemaining() > 0;
    }

    public String getDateText() {
        return startTime == null ? "" : startTime.toLocalDateTime().format(DATE_FMT);
    }

    public String getWeekText() {
        return startTime == null ? "" : WEEK[startTime.toLocalDateTime().getDayOfWeek().getValue() - 1];
    }

    public String getDayText() {
        return startTime == null ? "" : String.format("%02d", startTime.toLocalDateTime().getDayOfMonth());
    }

    public String getMonthText() {
        return startTime == null ? "" : (startTime.toLocalDateTime().getMonthValue() + "月");
    }

    public String getTimeRangeText() {
        if (startTime == null || endTime == null) {
            return "";
        }
        return startTime.toLocalDateTime().format(TIME_FMT) + " - " + endTime.toLocalDateTime().format(TIME_FMT);
    }

    public String getStartInput() {
        return startTime == null ? "" : startTime.toLocalDateTime().format(INPUT_FMT);
    }

    public String getEndInput() {
        return endTime == null ? "" : endTime.toLocalDateTime().format(INPUT_FMT);
    }

    public String getStatusText() {
        switch (status == null ? "" : status) {
            case "OPEN":
                return "可预约";
            case "FULL":
                return "已满员";
            case "STARTED":
                return "进行中";
            case "FINISHED":
                return "已结束";
            case "CANCELED":
                return "已取消";
            default:
                return status;
        }
    }
}
