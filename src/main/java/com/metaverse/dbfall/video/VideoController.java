package com.metaverse.dbfall.video;

import com.metaverse.dbfall.security.JwtTokenProvider;
import com.metaverse.dbfall.user.User;
import com.metaverse.dbfall.user.UserService;
import com.metaverse.dbfall.video.dto.UpdateVideoRequest;
import com.metaverse.dbfall.video.dto.VideoResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public VideoController(VideoService videoService, UserService userService, JwtTokenProvider tokenProvider) {
        this.videoService = videoService;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping
    public ResponseEntity<List<VideoResponse>> list(Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(videoService.list(user));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> upload(@RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "description", required = false) String description,
                                                Authentication authentication) {
        User user = resolveUser(authentication);
        VideoResponse response = videoService.upload(user, file, title, description);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoResponse> update(@PathVariable Long id,
                                                @RequestBody UpdateVideoRequest request,
                                                Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(videoService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        User user = resolveUser(authentication);
        videoService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> stream(@PathVariable Long id,
                                           @RequestParam(value = "token", required = false) String token,
                                           Authentication authentication) {
        User user = resolveUser(authentication, token);
        VideoService.VideoStream stream = videoService.loadForStreaming(user, id);
        String filename = stream.originalFilename() != null ? stream.originalFilename() : "video.mp4";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(stream.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encoded + "\"")
                .body(stream.resource());
    }

    private User resolveUser(Authentication authentication) {
        return resolveUser(authentication, null);
    }

    private User resolveUser(Authentication authentication, String token) {
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.requireByUsername(authentication.getName());
        }
        if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
            String username = tokenProvider.getUsername(token);
            if (username != null) {
                return userService.requireByUsername(username);
            }
        }
        throw new IllegalArgumentException("unauthorized");
    }
}
