package com.f3f.community.post.repository;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.scrap.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapPostRepository extends JpaRepository<ScrapPost,Long> {

    Optional<ScrapPost> findById(Long id);

    boolean existsByPostAndScrap(Post post, Scrap scrap);

    boolean existsByPostIdAndScrapId(Long pid, Long sid);

    List<ScrapPost> findScrapPostsByScrap(Scrap scrap);

    Optional<ScrapPost> findByScrapAndPost(Scrap scrap, Post post);

    List<ScrapPost> findScrapPostsByScrapId(Long id);

    List<ScrapPost> findScrapPostsByPostId(Long id);
}
