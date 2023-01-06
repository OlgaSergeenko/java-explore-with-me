package ru.practicum.admin.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CategoryNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public void remove(int catId) {
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoriesPage.getContent();
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Category getCategoryByIdOrThrowNotFound(int catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
        return category.get();
    }
}
