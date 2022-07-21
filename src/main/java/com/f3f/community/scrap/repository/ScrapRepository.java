package com.f3f.community.scrap.repository;

import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {

    Scrap findScrapById(Long id);

    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsByUserAndName(User user, String name);
    List<Scrap> findScrapListByUserId(Long userid);
}
