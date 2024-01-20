package com.softee5.mobil2team.service;

import com.softee5.mobil2team.dto.CommonCodeDto;
import com.softee5.mobil2team.dto.CommonCodeListDto;
import com.softee5.mobil2team.dto.DataResponseDto;
import com.softee5.mobil2team.dto.ReportDto;
import com.softee5.mobil2team.entity.CommonCode;
import com.softee5.mobil2team.entity.Post;
import com.softee5.mobil2team.entity.Report;
import com.softee5.mobil2team.repository.CommonCodeRepository;
import com.softee5.mobil2team.repository.PostRepository;
import com.softee5.mobil2team.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private CommonCodeRepository commonCodeRepository;


    /* 게시글 신고 */
    public boolean report(ReportDto reportDto) {
        Report report = new Report();

        Long postId = reportDto.getPostId();
        Long reportId = reportDto.getReportId();

        // null값 확인
        if(postId == null || reportId == null) {
            return false;
        }

        // 게시글 및 신고 사유 있는지 확인
        if (!postRepository.existsById(postId) || !commonCodeRepository.existsById(reportId)) {
            return false;
        }

        // 게시글, 신고 세팅
        report.setPost(Post.builder().id(reportDto.getPostId()).build());
        report.setCommonCode(CommonCode.builder().id(reportDto.getReportId()).build());

        reportRepository.save(report);

        return true;
    }

    /* 신고 사유 전체 조회 */
    public DataResponseDto<CommonCodeListDto> getReportList() {
        // REPORT 카테고리로 조회
        List<CommonCode> reportList = commonCodeRepository.findAllByCategory("REPORT");

        List<CommonCodeDto> results = new ArrayList<>();

        // 조회한 값들 dto로 변환
        for (CommonCode r : reportList) {
            CommonCodeDto commonCodeDto = new CommonCodeDto(r.getId(), r.getCode(), r.getDescription());
            results.add(commonCodeDto);
        }

        return DataResponseDto.of(new CommonCodeListDto(results));
    }
}
