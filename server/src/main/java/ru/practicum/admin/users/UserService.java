package ru.practicum.admin.users;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void remove(long id);
}
