package com.f3f.community.post.repository;

import com.f3f.community.category.domain.Category;
import com.f3f.community.post.domain.Post;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

//    List<Post> findPostListByUserId(Long userid);
     List<Post> findByAuthor(User author);

     Optional<Post> findById(Long id);

    List<Post> findByAuthorId(Long userid);


    List<Post> findByTitle(String title);

    boolean existsByAuthor(User author);

    boolean existsByAuthorId(Long userid);

    boolean existsByTitle(String title);

    List<Post> findPostsByCategory(Category category);

    // 철웅 추가, 유저 ID로 찾되 조회수 순으로 나열
    List<Post> findByAuthorOrderByViewCountDesc(User author);

}
