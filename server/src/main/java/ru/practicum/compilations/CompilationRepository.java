package ru.practicum.compilations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    Page<Compilation> findAll(Pageable pageable);

    Page<Compilation> findAllByPinnedIsTrue(Pageable pageable);

    Page<Compilation> findAllByPinnedIsFalse(Pageable pageable);
}


