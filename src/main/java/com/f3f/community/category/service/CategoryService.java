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

    // 기존에 짯던 포스트리스트만 리턴하는 getPost입니다
//    @Transactional(readOnly = true)
//    public List<Post> getPosts(Long catId) {
//        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
//        List<Post> post = new ArrayList<>(category.getPostList());
//        for (Category child : category.getChildCategory()) {
//            post.addAll(getPosts(child.getId()));
//        }
//
//        return post;
//    }

    // 리팩터링해서 깊이를 키로 가지고, 해당 깊이에 포스트리스트를 밸류로 가지는 해쉬맵을 리턴하게 변경하였습니다
//    @Transactional(readOnly = true)
//    public Map<Long, List<Post>> getPosts(Long catId) {
//        Category category = categoryRepository.findById(catId).orElseThrow(NotFoundCategoryByIdException::new);
//        List<Post> posts = new ArrayList<>(category.getPostList());
//        Map<Long, List<Post>> result = new HashMap<>();
//        result.put(category.getDepth(), posts);
//
//
//        for (Category child : category.getChildCategory()) {
//            Map<Long, List<Post>> temp = getPosts(child.getId());
//            for (Long key : temp.keySet()) {
//                if (!result.containsKey(key)) {
//                    result.put(key, new ArrayList<>());
//                }
//                result.get(key).addAll(temp.get(key));
//            }
//        }
//
//        return result;
//    }

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
