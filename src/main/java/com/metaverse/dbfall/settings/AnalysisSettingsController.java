package com.metaverse.dbfall.settings;

import com.metaverse.dbfall.settings.dto.AnalysisSettingsRequest;
import com.metaverse.dbfall.settings.dto.AnalysisSettingsResponse;
import com.metaverse.dbfall.user.User;
import com.metaverse.dbfall.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/analysis")
public class AnalysisSettingsController {

    private final UserService userService;
    private final UserAnalysisSettingsService settingsService;

    public AnalysisSettingsController(UserService userService, UserAnalysisSettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<?> get(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.requireByUsername(authentication.getName());
        return settingsService.findByUser(user)
                .<ResponseEntity<?>>map(settings -> ResponseEntity.ok(AnalysisSettingsResponse.from(settings)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping
    public ResponseEntity<?> save(@Valid @RequestBody AnalysisSettingsRequest request,
                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.requireByUsername(authentication.getName());
        UserAnalysisSettings saved = settingsService.save(user, request);
        return ResponseEntity.ok(AnalysisSettingsResponse.from(saved));
    }
}
