package com.f3f.community.scrap.repository;

import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {

    Optional<Scrap> findById(Long id);
    //스크랩 아이디로 스크랩에 해당 스크랩이 있는지 조회해서 특정 스크랩 반환
    Scrap findByName(String name);
    //스크랩 이름으로 스크랩을 찾아줌.
    List<Scrap> findScrapsByUser(User user);
    //유저객체로 유저의 스크랩 리스트 찾아줌.
    boolean existsById(Long id);
    //스크랩 아이디로 스크랩이 있는지 조회(응, 아니오)
    boolean existsByName(String name);
    //스크랩 이름으로 스크랩이 있는지 조회
    boolean existsByUser(User user);
    //유저로 스크랩이 있는지 조회
    List<Scrap> findScrapsByUserId(Long userId);
}
