package com.softee5.mobil2team.repository;

import com.softee5.mobil2team.entity.NicknameModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NicknameModifierRepository extends JpaRepository<NicknameModifier, Long> {

    @Query(value = "SELECT m.modifier FROM NicknameModifier m"
            + " WHERE m.tag.id = :tagId"
            + " ORDER BY m.id ASC")
    List<String> findNicknameModifiersByTagId(@Param("tagId") Long tagId);

}
