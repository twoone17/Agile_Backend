package com.f3f.community.tag.repository;

import com.f3f.community.post.domain.Post;
import com.f3f.community.tag.domain.PostTag;
import com.f3f.community.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findPostTagsByPost(Post post);
    List<PostTag> findPostTagsByPostId(Long id);

    Optional<PostTag> findByPostAndTag(Post post, Tag tag);

    boolean existsByPostAndTag(Post post, Tag tag);

    boolean existsById(Long id);

    List<PostTag> findPostTagsByTagId(Long id);

    boolean existsByPostIdAndTagId(Long post, Long tag);
}
