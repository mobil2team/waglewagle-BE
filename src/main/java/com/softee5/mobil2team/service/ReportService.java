package com.softee5.mobil2team.service;

import com.softee5.mobil2team.config.GeneralException;
import com.softee5.mobil2team.config.ResponseCode;
import com.softee5.mobil2team.dto.*;
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
    public ResponseDto report(ReportDto reportDto) {
        Report report = new Report();

        Long postId = reportDto.getPostId();
        Long reportId = reportDto.getReportId();

        // 데이터 유효성 검증 (널 값, 해당 데이터 있는지 확인)
        if(!validateData(postId, reportId)) {
            throw new GeneralException(ResponseCode.BAD_REQUEST, "게시글 신고 실패");
        }

        // 게시글, 신고 세팅
        report.setPost(Post.builder().id(reportDto.getPostId()).build());
        report.setCommonCode(CommonCode.builder().id(reportDto.getReportId()).build());

        reportRepository.save(report);

        return ResponseDto.of(true, ResponseCode.OK, "게시글 신고 성공");
    }

    private boolean validateData(Long postId, Long reportId) {
        return postId != null && reportId != null && postRepository.existsById(postId) && commonCodeRepository.existsById(reportId);
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
