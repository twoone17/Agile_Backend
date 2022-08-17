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

     //primary key인 id값으로 찾기 : 왜 Post로 반환을 하면 오류가 나는걸까?
     Optional<Post> findById(Long id);

    List<Post> findByAuthorId(Long userid);


    List<Post> findByTitle(String title);

    boolean existsByAuthor(User author);

    boolean existsByAuthorId(Long userid);

    boolean existsByTitle(String title);

    List<Post> findPostsByCategory(Category category);

}
