package com.metaverse.dbfall.video.dto;

import com.metaverse.dbfall.video.VideoFile;
import com.metaverse.dbfall.video.VideoTag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class VideoResponse {

    private Long id;
    private String title;
    private String originalFilename;
    private String description;
    private Long fileSize;
    private String contentType;
    private String streamUrl;
    private LocalDateTime uploadDate;
    private List<String> tags;

    public static VideoResponse from(VideoFile file, String streamUrl) {
        VideoResponse resp = new VideoResponse();
        resp.setId(file.getId());
        resp.setTitle(file.getTitle());
        resp.setOriginalFilename(file.getOriginalFilename());
        resp.setDescription(file.getDescription());
        resp.setFileSize(file.getFileSize());
        resp.setContentType(file.getContentType());
        resp.setUploadDate(file.getUploadDate());
        resp.setStreamUrl(streamUrl);
        List<String> tagValues = file.getTags() == null ? List.of() : file.getTags()
            .stream()
            .map(VideoTag::getTagName)
            .collect(Collectors.toList());
        resp.setTags(tagValues);
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
