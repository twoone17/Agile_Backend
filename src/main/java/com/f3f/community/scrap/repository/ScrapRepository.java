package com.f3f.community.scrap.repository;

import com.f3f.community.scrap.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {

    Scrap findByIdScrap(long id);

    boolean existsById(Long id);

    Optional<Scrap> findByName(String name);
}
