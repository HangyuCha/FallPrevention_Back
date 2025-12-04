package com.metaverse.dbfall.settings.dto;

import com.metaverse.dbfall.settings.UserAnalysisSettings;

import java.time.LocalDateTime;

public class AnalysisSettingsResponse {

    private Long userId;
    private Integer frameInterval;
    private String notificationOption;
    private Boolean notificationEnabled;
    private LocalDateTime updatedAt;

    public static AnalysisSettingsResponse from(UserAnalysisSettings settings) {
        AnalysisSettingsResponse resp = new AnalysisSettingsResponse();
        resp.setUserId(settings.getUserId());
        resp.setFrameInterval(settings.getFrameInterval());
        resp.setNotificationOption(settings.getNotificationOption());
        resp.setNotificationEnabled(settings.getNotificationEnabled());
        resp.setUpdatedAt(settings.getUpdatedAt());
        return resp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getFrameInterval() {
        return frameInterval;
    }

    public void setFrameInterval(Integer frameInterval) {
        this.frameInterval = frameInterval;
    }

    public String getNotificationOption() {
        return notificationOption;
    }

    public void setNotificationOption(String notificationOption) {
        this.notificationOption = notificationOption;
    }

    public Boolean getNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(Boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
