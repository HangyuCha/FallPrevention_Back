package com.metaverse.dbfall.settings;

import com.metaverse.dbfall.settings.dto.AnalysisSettingsRequest;
import com.metaverse.dbfall.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserAnalysisSettingsService {

    private final UserAnalysisSettingsRepository repository;

    public UserAnalysisSettingsService(UserAnalysisSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<UserAnalysisSettings> findByUser(User user) {
        return repository.findById(user.getId());
    }

    @Transactional
    public UserAnalysisSettings save(User user, AnalysisSettingsRequest request) {
        UserAnalysisSettings settings = repository.findById(user.getId())
                .orElseGet(() -> {
                    UserAnalysisSettings created = new UserAnalysisSettings();
                    created.setUser(user);
                    created.setUserId(user.getId());
                    return created;
                });

        // Ensure the shared primary key is always in sync
        if (settings.getUser() == null) {
            settings.setUser(user);
        }
        if (settings.getUserId() == null) {
            settings.setUserId(user.getId());
        }

        if (request.getFrameInterval() != null) {
            int safeValue = Math.max(1, request.getFrameInterval());
            settings.setFrameInterval(safeValue);
        }
        if (request.getNotificationOption() != null && !request.getNotificationOption().isBlank()) {
            settings.setNotificationOption(request.getNotificationOption());
        }
        if (request.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(request.getNotificationEnabled());
        }

        return repository.save(settings);
    }
}
