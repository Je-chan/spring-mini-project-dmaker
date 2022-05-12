package com.example.dmaker.repository;

import com.example.dmaker.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    // Spring JAP 에서 메소드 명만 가지고도 특정 컬럼명을 검색할 수 있다.
    // 그게 findByMemberId
    Optional<Developer> findByMemberId(String memberId);
}
