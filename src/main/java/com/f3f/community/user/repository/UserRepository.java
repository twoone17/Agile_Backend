package com.f3f.community.user.repository;

import com.f3f.community.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsById(long id);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String Email);
    Optional<User> findByEmail(String Email);
}
