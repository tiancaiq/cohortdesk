package com.github.tiancaiq.tliaswebmanagement.service;

import com.github.tiancaiq.tlias_pojo.vo.ClazzStudentCountVO;
import com.github.tiancaiq.tlias_pojo.vo.JobCountVO;
import com.github.tiancaiq.tlias_pojo.vo.OptionVO;
import com.github.tiancaiq.tlias_pojo.vo.StudentSummaryVO;

import java.util.List;

public interface ReportService {
    List< JobCountVO > getEmpJobData();

    List< OptionVO > getEmpGenderData();

    List< ClazzStudentCountVO > getStudentCountData();

    List< OptionVO > getStudentDegreeData();

    StudentSummaryVO getStudentSummaryData();
}