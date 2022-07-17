package com.f3f.community.scrap.repository;

import com.f3f.community.scrap.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {
}
