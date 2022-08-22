package com.f3f.community.tag.repository;


import com.f3f.community.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    Optional<Tag> findById(Long id);

    Optional<Tag> findByTagName(String name);

    boolean existsById(Long id);

    boolean existsByTagName(String Name);

}
