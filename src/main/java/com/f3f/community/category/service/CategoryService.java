package com.f3f.community.category.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto.SaveRequest;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.exception.categoryException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;

    @Transactional
    public Long createCategory(SaveRequest saveRequest) throws Exception {
        if (saveRequest.getCategoryName() == null) {
            throw new NotFoundCategoryNameException();
        }
        if (saveRequest.getCategoryName().equals("")) {
            throw new NotFoundCategoryNameException("이름이 빈 스트링입니다.");
        }
        if (saveRequest.getPostList() == null) {
            throw new NotFoundCategoryPostListException();
        }
        if (saveRequest.getChildCategory() == null) {
            throw new NotFoundChildCategoryListException();
        }
        if (categoryRepository.existsByCategoryName(saveRequest.getCategoryName())) {
            throw new DuplicateCategoryNameException();
        }
        if (saveRequest.getParents() == null && categoryRepository.existsByCategoryName("root")) {
            throw new NotFoundParentCategoryException();
        }
        Category category = saveRequest.toEntity();
        if (category.getParents() != null) {
            Category parent = saveRequest.getParents();
            if (parent.getDepth() == 3) {
                throw new MaxDepthException();
            }
            parent.getChildCategory().add(category);
            category.setDepth(parent.getDepth() + 1);
        }

        categoryRepository.save(category);

        return category.getId();
    }

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        List<Post> post = new ArrayList<>(category.getPostList());
        for (Category child : category.getChildCategory()) {
            post.addAll(getPosts(child.getId()));
        }

        return post;
    }

    @Transactional
    public String updateCategoryName(Long catId, String newName) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        if (categoryRepository.existsByCategoryName(newName)) {
            throw new DuplicateCategoryNameException();
        } else {
            category.updateName(newName);
            return "ok";
        }
    }

    @Transactional
    public String deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        categoryRepository.delete(category);
        return "ok";
    }

}
