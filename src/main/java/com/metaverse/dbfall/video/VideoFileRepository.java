package com.metaverse.dbfall.video;

import com.metaverse.dbfall.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoFileRepository extends JpaRepository<VideoFile, Long> {

    @EntityGraph(attributePaths = "tags")
    List<VideoFile> findByUserOrderByUploadDateDesc(User user);

    Optional<VideoFile> findByIdAndUser(Long id, User user);
}
