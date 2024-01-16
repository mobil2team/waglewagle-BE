package com.softee5.mobil2team.repository;

import com.softee5.mobil2team.entity.NicknameNoun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NicknameNounRepository extends JpaRepository<NicknameNoun, Long> {

    @Query(value = "SELECT n.noun FROM NicknameNoun n ORDER BY n.id ASC")
    List<String> findAllNouns();

}
