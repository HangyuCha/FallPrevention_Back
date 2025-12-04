package com.metaverse.dbfall.video;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VIDEO_TAG")
public class VideoTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VIDEO_ID")
    private VideoFile video;

    @Column(name = "TAG_NAME", nullable = false)
    private String tagName;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    protected VideoTag() {
    }

    public VideoTag(String tagName) {
        this.tagName = tagName;
    }

    public Long getId() {
        return id;
    }

    public VideoFile getVideo() {
        return video;
    }

    public void setVideo(VideoFile video) {
        this.video = video;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
