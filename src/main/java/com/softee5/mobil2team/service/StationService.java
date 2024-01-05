package com.softee5.mobil2team.service;

import com.softee5.mobil2team.dto.BriefInfoDto;
import com.softee5.mobil2team.dto.DataResponseDto;
import com.softee5.mobil2team.dto.StationListDto;
import com.softee5.mobil2team.entity.Station;
import com.softee5.mobil2team.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public DataResponseDto<BriefInfoDto> getBriefInfo() {
        return DataResponseDto.of(new BriefInfoDto(stationRepository.getBriefStationInfo()));
    }

    /* 가까운 역 리스트 */
    public DataResponseDto<StationListDto> getNearStationList(Double currentX, Double currentY) {

        List<Long> stationIdList;
        if (currentX == null || currentY == null || currentX.isNaN() || currentY.isNaN()) {
            stationIdList = getAllStationList();
        }
        else {
            stationIdList = stationRepository.findNearestStations(currentX, currentY, 43);
        }

        return DataResponseDto.of(new StationListDto(stationIdList));
    }

    public List<Long> getAllStationList() {
        List<Long> list = stationRepository.findAllId();
        return list;
    }

}
