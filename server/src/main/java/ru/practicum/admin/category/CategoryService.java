package ru.practicum.admin.category;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void remove(int catId);

    List<CategoryDto> findAll(Pageable pageable);

    Category getCategoryByIdOrThrowNotFound(int catId);
}
