package com.f3f.community.media.repository;

import com.f3f.community.admin.domain.Admin;
import com.f3f.community.media.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {


}
