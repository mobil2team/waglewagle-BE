package com.softee5.mobil2team.repository;

import com.softee5.mobil2team.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {
    // category 값으로 모든 값 조회
    @Query(value = "SELECT id, code, description FROM common_code " +
            "WHERE category = :category", nativeQuery = true)
    List<CommonCode> findAllByCategory(String category);
}
