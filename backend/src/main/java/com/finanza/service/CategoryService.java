package com.finanza.service;

import com.finanza.dto.CategoryRequest;
import com.finanza.dto.CategoryResponse;
import com.finanza.model.Category;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(Organization organization) {
        log.debug("Obteniendo todas las categorías activas para la organización: {}", organization.getId());
        return categoryRepository.findByOrganizationAndActiveTrue(organization)
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(Organization organization, Category.TransactionType type) {
        log.debug("Obteniendo categorías de tipo {} para la organización: {}", type, organization.getId());
        return categoryRepository.findByOrganizationAndTypeAndActiveTrue(organization, type)
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, Organization organization) {
        log.debug("Obteniendo categoría con id: {} para la organización: {}", id, organization.getId());
        Category category = categoryRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, Organization organization, User user) {
        log.debug("Creando nueva categoría: {} para la organización: {}", request.getName(), organization.getId());

        if (categoryRepository.existsByNameAndOrganization(request.getName(), organization)) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setActive(request.getActive() != null ? request.getActive() : true);
        category.setOrganization(organization);
        category.setCreatedBy(user);

        Category savedCategory = categoryRepository.save(category);
        log.info("Categoría creada exitosamente con id: {}", savedCategory.getId());
        
        return CategoryResponse.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, Organization organization) {
        log.debug("Actualizando categoría con id: {}", id);
        
        Category category = categoryRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar si el nuevo nombre ya existe (si cambió)
        if (!category.getName().equals(request.getName()) &&
            categoryRepository.existsByNameAndOrganization(request.getName(), organization)) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setActive(request.getActive() != null ? request.getActive() : category.getActive());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoría actualizada exitosamente con id: {}", updatedCategory.getId());
        
        return CategoryResponse.fromEntity(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id, Organization organization) {
        log.debug("Eliminando (desactivando) categoría con id: {}", id);
        
        Category category = categoryRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setActive(false);
        categoryRepository.save(category);
        
        log.info("Categoría desactivada exitosamente con id: {}", id);
    }
}
