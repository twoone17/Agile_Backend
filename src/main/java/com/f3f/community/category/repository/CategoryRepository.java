package com.f3f.community.category.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {


}
