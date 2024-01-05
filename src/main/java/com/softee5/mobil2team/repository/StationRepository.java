package com.softee5.mobil2team.repository;

import com.softee5.mobil2team.dto.BriefInfoDto;
import com.softee5.mobil2team.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Query(value = "SELECT s.id AS stationId, " +
            "       s.name AS stationName, " +
            "       (SELECT p1.tag_id FROM post p1 WHERE p1.station_id = s.id AND p1.created_datetime >= CURRENT_TIMESTAMP - INTERVAL '2' HOUR GROUP BY p1.tag_id ORDER BY COUNT(p1.id) DESC LIMIT 1) AS tagId, " +
            "       COUNT(p.id) AS contentCount " +
            "FROM station s " +
            "LEFT JOIN post p ON s.id = p.station_id AND p.created_datetime >= CURRENT_TIMESTAMP - INTERVAL '2' HOUR " +
            "GROUP BY s.id, s.name", nativeQuery = true)
    List<BriefInfoDto.StationInfo> getBriefStationInfo();

    @Query(value = "SELECT s FROM Station s " +
            "ORDER BY SQRT(CAST(POW(s.locationX - :inputX, 2) as DOUBLE)" +
            "   + CAST(POW(s.locationY - :inputY, 2) as DOUBLE)) " +
            "LIMIT :cnt")
    List<Station> findNearestStations(@Param("inputX") double inputX, @Param("inputY") double inputY, @Param("cnt") int cnt);
}
