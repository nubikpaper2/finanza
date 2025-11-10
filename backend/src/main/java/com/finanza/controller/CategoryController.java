package com.finanza.controller;

import com.finanza.dto.CategoryRequest;
import com.finanza.dto.CategoryResponse;
import com.finanza.model.Category;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // En producción, obtener la organización del usuario autenticado
        // Por ahora, asumimos una organización predeterminada
        Organization organization = new Organization();
        organization.setId(1L);
        
        List<CategoryResponse> categories = categoryService.getAllCategories(organization);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(
            @PathVariable Category.TransactionType type,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        List<CategoryResponse> categories = categoryService.getCategoriesByType(organization, type);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        CategoryResponse category = categoryService.getCategoryById(id, organization);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        User user = new User();
        user.setId(1L);
        
        CategoryResponse category = categoryService.createCategory(request, organization, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        CategoryResponse category = categoryService.updateCategory(id, request, organization);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Organization organization = new Organization();
        organization.setId(1L);
        
        categoryService.deleteCategory(id, organization);
        return ResponseEntity.noContent().build();
    }
}
