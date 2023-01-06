package ru.practicum.admin.users;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        try {
            this.userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                    && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
                throw new ConflictException("conflict");
            throw e;
        }
        return UserMapper.toDto(user);
    }

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
        checkUserExistenceOrThrowNotFound(id);
        userRepository.deleteById(id);
    }

    @Override
    public User findByIdOrThrowNotFound(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }

    public void checkUserExistenceOrThrowNotFound(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
    }
}
