package com.metaverse.dbfall.video;

import com.metaverse.dbfall.user.User;
import com.metaverse.dbfall.video.dto.UpdateVideoRequest;
import com.metaverse.dbfall.video.dto.VideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VideoService {

    private final VideoFileRepository videoFileRepository;
    private final Path storageRoot;

    public VideoService(VideoFileRepository videoFileRepository,
                        @Value("${app.storage.video-dir:storage/videos}") String storageDir) {
        this.videoFileRepository = videoFileRepository;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize video storage directory", e);
        }
    }

    @Transactional(readOnly = true)
    public List<VideoResponse> list(User user) {
        return videoFileRepository.findByUserOrderByUploadDateDesc(user)
                .stream()
                .map(video -> VideoResponse.from(video, buildStreamPath(video.getId())))
                .toList();
    }

    @Transactional
    public VideoResponse upload(User user, MultipartFile file, String title, String description) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        try {
            byte[] bytes = file.getBytes();
            Path userDir = resolveUserDir(user);
            Files.createDirectories(userDir);
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + (extension != null ? "." + extension : "");
            Path target = userDir.resolve(storedName);
            file.transferTo(target);

            VideoFile entity = new VideoFile();
            entity.setUser(user);
            entity.setStoredFilename(storedName);
            entity.setStoragePath(userDir.toString());
            entity.setOriginalFilename(file.getOriginalFilename());
            entity.setTitle(StringUtils.hasText(title) ? title : file.getOriginalFilename());
            entity.setDescription(description);
            entity.setFileSize(file.getSize());
            entity.setContentType(file.getContentType());
            entity.setData(bytes);
            entity.replaceTags(generateTags(title, description, file.getOriginalFilename(), user));

            VideoFile saved = videoFileRepository.save(entity);
            return VideoResponse.from(saved, buildStreamPath(saved.getId()));
        } catch (IOException e) {
            throw new IllegalStateException("failed to store video", e);
        }
    }

    @Transactional
    public VideoResponse update(User user, Long id, UpdateVideoRequest request) {
        VideoFile video = requireOwnedVideo(user, id);
        if (request.getTitle() != null) {
            video.setTitle(StringUtils.hasText(request.getTitle()) ? request.getTitle() : video.getTitle());
        }
        if (request.getDescription() != null) {
            video.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            video.replaceTags(request.getTags());
        }
        return VideoResponse.from(video, buildStreamPath(video.getId()));
    }

    @Transactional
    public void delete(User user, Long id) {
        VideoFile video = requireOwnedVideo(user, id);
        Path path = resolveFilePath(video);
        videoFileRepository.delete(video);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    @Transactional(readOnly = true)
    public VideoStream loadForStreaming(User user, Long id) {
        VideoFile video = requireOwnedVideo(user, id);
        byte[] data = video.getData();
        if (data != null && data.length > 0) {
            String contentType = StringUtils.hasText(video.getContentType())
                    ? video.getContentType()
                    : "application/octet-stream";
            return new VideoStream(new ByteArrayResource(data), contentType, video.getOriginalFilename());
        }
        try {
            Path path = resolveFilePath(video);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new IllegalArgumentException("video not found");
            }
            String contentType = StringUtils.hasText(video.getContentType())
                    ? video.getContentType()
                    : "application/octet-stream";
            return new VideoStream(resource, contentType, video.getOriginalFilename());
        } catch (IOException e) {
            throw new IllegalArgumentException("video not found");
        }
    }

    private VideoFile requireOwnedVideo(User user, Long id) {
        return videoFileRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("video not found"));
    }

    private Path resolveUserDir(User user) {
        return storageRoot.resolve("user-" + user.getId());
    }

    private Path resolveFilePath(VideoFile file) {
        Path base = file.getStoragePath() != null ? Paths.get(file.getStoragePath()) : resolveUserDir(file.getUser());
        return base.resolve(file.getStoredFilename());
    }

    private List<String> generateTags(String title, String description, String originalName, User user) {
        Set<String> tags = new LinkedHashSet<>();
        if (StringUtils.hasText(title)) {
            tags.addAll(tokenize(title));
        }
        if (StringUtils.hasText(originalName)) {
            tags.addAll(tokenize(originalName));
        }
        if (StringUtils.hasText(description)) {
            tags.addAll(tokenize(description));
        }
        tags.add("user-" + user.getId());
        if (tags.size() < 3) {
            List<String> defaults = List.of("analysis", "routine", "fall-monitor", "safety", "practice");
            ThreadLocalRandom random = ThreadLocalRandom.current();
            while (tags.size() < 3) {
                tags.add(defaults.get(random.nextInt(defaults.size())));
            }
        }
        List<String> ordered = new ArrayList<>(tags);
        int limit = Math.min(ordered.size(), 5);
        return new ArrayList<>(ordered.subList(0, limit));
    }

    private List<String> tokenize(String text) {
        List<String> values = new ArrayList<>();
        if (!StringUtils.hasText(text)) {
            return values;
        }
        String cleaned = text.replaceAll("[^a-zA-Z0-9가-힣 ]", " ").toLowerCase();
        for (String token : cleaned.split(" ")) {
            if (token.isBlank()) {
                continue;
            }
            values.add(token.trim());
        }
        return values;
    }

    private String buildStreamPath(Long id) {
        return "/api/videos/" + id + "/stream";
    }

    public record VideoStream(Resource resource, String contentType, String originalFilename) {
    }
}
