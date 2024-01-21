package com.softee5.mobil2team.controller;

import com.softee5.mobil2team.dto.CommonCodeListDto;
import com.softee5.mobil2team.dto.DataResponseDto;
import com.softee5.mobil2team.dto.ReportDto;
import com.softee5.mobil2team.dto.ResponseDto;
import com.softee5.mobil2team.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/report")
public class ReportController {
    private final ReportService reportService;

    /* 게시글 신고 */
    @PostMapping("")
    public ResponseEntity<ResponseDto> reportPost(@RequestBody ReportDto reportDto) {
        return new ResponseEntity<>(reportService.report(reportDto), HttpStatus.OK);
    }

    /* 신고 사유 조회 */
    @GetMapping("/list")
    public ResponseEntity<DataResponseDto<CommonCodeListDto>> getReportList() {
        return new ResponseEntity<>(reportService.getReportList(), HttpStatus.OK);
    }
}
