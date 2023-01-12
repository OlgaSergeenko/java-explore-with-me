package ru.practicum.admin.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select u " +
            "from User as u " +
            "where u.id in ?1")
    Page<User> findAllByIds(List<Long> ids, Pageable pageable);
}
