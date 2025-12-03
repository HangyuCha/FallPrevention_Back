package com.metaverse.dbfall.user;

import com.metaverse.dbfall.user.dto.UpdateUserRequest;
import com.metaverse.dbfall.user.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        List<UserDto> dtos = userService.list().stream().map(UserDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return userService.findById(id)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(UserDto.from(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateUserRequest req) {
        try {
            LocalDate birth = null;
            if (req.birthDate != null && !req.birthDate.isBlank()) {
                birth = LocalDate.parse(req.birthDate);
            }
            User updated = userService.update(id, req.username, birth);
            return ResponseEntity.ok(UserDto.from(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 로그인 사용자 정보(선택)
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        return userService.findByUsername(username)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(UserDto.from(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}