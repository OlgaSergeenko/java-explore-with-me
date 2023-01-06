package ru.practicum.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.users.UserDto;
import ru.practicum.admin.users.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        UserDto userSaved = userService.create(userDto);
        log.info(String.format("User with id %d is created", userSaved.getId()));
        return ResponseEntity.ok(userSaved);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(@RequestParam(required = false) List<Long> ids,
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(userService.getAll(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    public long removeUser(@PathVariable Long userId) {
        log.info("Remove user {}", userId);
        userService.remove(userId);
        return userId;
    }
}
