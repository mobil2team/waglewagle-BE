package com.softee5.mobil2team.repository;

import com.softee5.mobil2team.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /* 역 ID로 포스트 조회 */
    @Query("SELECT p FROM Post p WHERE p.station.id = :stationId " +
            "AND p.createdDatetime > :createdDatetime " +
            "AND p.id NOT IN (" +
            "   SELECT r.post.id FROM Report r " +
            "   GROUP BY r.post.id " +
            "   HAVING COUNT(r.post.id) >= 5" +
            ")")
    Page<Post> findByStationIdAndCreatedDatetimeAfter(Long stationId, Pageable pageable, Date createdDatetime);

    /* 역 ID & tag로 포스트 조회 */
    @Query("SELECT p FROM Post p WHERE p.station.id = :stationId " +
            "AND p.tag.id = :tagId " +
            "AND p.createdDatetime > :createdDatetime " +
            "AND p.id NOT IN (" +
            "   SELECT r.post.id FROM Report r " +
            "   GROUP BY r.post.id " +
            "   HAVING COUNT(r.post.id) >= 5" +
            ")")
    Page<Post> findByStationIdAndTagIdAndCreatedDatetimeAfter(Long stationId, Long tagId, Pageable pageable, Date createdDatetime);
}
