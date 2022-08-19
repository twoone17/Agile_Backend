package com.f3f.community.category.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findCategoriesByParents(Category parent);

    boolean existsByCategoryName(String name);

    boolean existsById(Long id);

    Optional<Category> findByCategoryName(String name);

    List<Category> findCategoriesByParentsId(Long pid);

}
