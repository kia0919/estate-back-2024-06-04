package com.estate.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.estate.back.entity.UserEntity;

// estate 데이터베이스의 User 테이블의 작업을 위한 리포지토리
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // existsBy: 있다 없다 판별하는 것
    boolean existsByUserId(String userId);
    boolean existsByUserEmail(String userEmail);

    UserEntity findByUserId(String userId);

}
