package com.f3f.community.scrap.repository;

import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {

    Scrap findByScrapId(Long id);

    boolean existsByScrapId(Long id);

    boolean existsByName(String name);

    boolean existsByUser(User user);
    List<Scrap> findScrapsByUserId(Long userid);
}
