package com.metaverse.dbfall.settings.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class AnalysisSettingsRequest {

    @Min(value = 1, message = "frameInterval must be >= 1")
    private Integer frameInterval;

    @Pattern(regexp = "off|on_high|always", message = "notificationOption must be one of off, on_high, always")
    private String notificationOption;

    private Boolean notificationEnabled;

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
}
