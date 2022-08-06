package com.f3f.community.category.service;

import com.f3f.community.category.domain.Category;
import com.f3f.community.category.dto.CategoryDto.SaveRequest;
import com.f3f.community.category.repository.CategoryRepository;
import com.f3f.community.exception.categoryException.*;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.f3f.community.common.constants.ResponseConstants.OK;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;

    @Transactional
    public Long createCategory(SaveRequest saveRequest) throws Exception {
        if (categoryRepository.existsByCategoryName(saveRequest.getCategoryName())) {
            throw new DuplicateCategoryNameException();
        }
        if (saveRequest.getParents() == null && !saveRequest.getCategoryName().equals("root")) {
            throw new NotFoundParentCategoryException();
        }
        Category category = saveRequest.toEntity();
        if (category.getParents() != null) {
            Category parent = categoryRepository.findById(saveRequest.getParents().getId()).get();
//            Category parent = saveRequest.getParents();
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
    public List<Category> getChildCategories(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        return categoryRepository.findCategoriesByParents(category);
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsOfChild(Long catId) {
        List<Post> result = new ArrayList<>();
        for (Category cat : getChildCategories(catId)) {
            result.addAll(cat.getPostList());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Category> getChildsOfRoot() {
        Category root = categoryRepository.findByCategoryName("root").orElseThrow(NotFoundCategoryByNameException::new);
        return getChildCategories(root.getId());
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsOfRootChild() {
        List<Post> result = new ArrayList<>();
        for (Category cat : getChildsOfRoot()) {
            result.addAll(cat.getPostList());
        }
        return result;
    }



    @Transactional
    public String updateCategoryName(Long catId, String newName) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        if (categoryRepository.existsByCategoryName(newName)) {
            throw new DuplicateCategoryNameException();
        } else {
            category.updateName(newName);
            return OK;
        }
    }

    @Transactional
    public String deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
        if (category.getChildCategory().isEmpty()) {
            categoryRepository.delete(category);
        }else {
            throw new NotEmptyChildCategoryException();
        }
        return OK;
    }

}
