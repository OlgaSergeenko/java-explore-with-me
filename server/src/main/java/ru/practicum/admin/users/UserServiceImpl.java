package ru.practicum.admin.users;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User saved = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids != null) {
            Page<User> users = userRepository.findAllByIds(ids, page);
            return users.getContent().stream().map(UserMapper::toDto).collect(Collectors.toList());
        }
        Page<User> users = userRepository.findAll(page);
        return users.getContent().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void remove(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteById(id);
    }
}
