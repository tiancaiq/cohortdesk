package com.github.tiancaiq.cohortdesk.service;

import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.JobCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import com.github.tiancaiq.cohortdesk.model.vo.StudentSummaryVO;

import java.util.List;

public interface ReportService {
    List< JobCountVO > getEmpJobData();

    List< OptionVO > getEmpGenderData();

    List< ClazzStudentCountVO > getStudentCountData();

    List< OptionVO > getStudentDegreeData();

    StudentSummaryVO getStudentSummaryData();
}