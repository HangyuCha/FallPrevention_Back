package com.metaverse.dbfall.settings;

import com.metaverse.dbfall.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ANALYSIS_SETTINGS")
public class UserAnalysisSettings {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "FRAME_INTERVAL")
    private Integer frameInterval = 5;

    @Column(name = "NOTIFICATION_OPTION", length = 32)
    private String notificationOption = "off";

    @Column(name = "NOTIFICATION_ENABLED")
    private Boolean notificationEnabled = Boolean.TRUE;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
