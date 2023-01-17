package ru.practicum.admin.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.exceptions.OperationNotAllowedException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

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
        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new OperationNotAllowedException("Deleting a category with events is not allowed");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoriesPage.getContent();
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }
}
