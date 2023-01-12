package ru.practicum.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.category.CategoryDto;
import ru.practicum.admin.category.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@AllArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto categorySaved = categoryService.create(categoryDto);
        log.info(String.format("Category %s is created", categorySaved.getName()));
        return ResponseEntity.ok(categorySaved);
    }

    @PatchMapping
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto categoryUpdated = categoryService.update(categoryDto);
        log.info(String.format("Category %s is updated", categoryDto.getId()));
        return ResponseEntity.ok(categoryUpdated);
    }

    @DeleteMapping("/{catId}")
    public long removeCategory(@PathVariable Integer catId) {
        log.info("Remove category {}", catId);
        categoryService.remove(catId);
        return catId;
    }
}
