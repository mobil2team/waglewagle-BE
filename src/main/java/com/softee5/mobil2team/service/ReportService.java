package com.softee5.mobil2team.service;

import com.softee5.mobil2team.dto.ReportDto;
import com.softee5.mobil2team.entity.CommonCode;
import com.softee5.mobil2team.entity.Post;
import com.softee5.mobil2team.entity.Report;
import com.softee5.mobil2team.repository.CommonCodeRepository;
import com.softee5.mobil2team.repository.PostRepository;
import com.softee5.mobil2team.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        if(postId == null || reportId == null) {
            return false;
        }

        if (!postRepository.existsById(postId) || !commonCodeRepository.existsById(reportId)) {
            return false;
        }

        report.setPost(Post.builder().id(reportDto.getPostId()).build());
        report.setCommonCode(CommonCode.builder().id(reportDto.getReportId()).build());

        reportRepository.save(report);

        return true;
    }
}
