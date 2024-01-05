package com.softee5.mobil2team.service;

import com.softee5.mobil2team.dto.BriefInfoDto;
import com.softee5.mobil2team.dto.DataResponseDto;
import com.softee5.mobil2team.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public DataResponseDto<BriefInfoDto> getBriefInfo() {
        return DataResponseDto.of(new BriefInfoDto(stationRepository.getBriefStationInfo()));
    }
}
