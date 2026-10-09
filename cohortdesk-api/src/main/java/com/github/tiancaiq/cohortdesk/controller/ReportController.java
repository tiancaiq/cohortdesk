package com.github.tiancaiq.cohortdesk.controller;

import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.JobCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import com.github.tiancaiq.cohortdesk.model.vo.StudentSummaryVO;
import com.github.tiancaiq.cohortdesk.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController( ReportService reportService ){
        this.reportService = reportService;
    }

    @GetMapping("/empJobData")  // http://localhost:8080/report/empJobData
    public Result< List< JobCountVO > > getEmpJobData(){
        log.info("Employee counts by role");
        List< JobCountVO > empJobData = reportService.getEmpJobData();
        return Result.success( empJobData );
    }

    @GetMapping("/empGenderData")  // http://localhost:8080/report/empGenderData
    public Result< List< OptionVO > > getEmpGenderData(){
        log.info("Employee counts by gender");
        List< OptionVO > empGenderData = reportService.getEmpGenderData();
        return Result.success( empGenderData );
    }

    @GetMapping( "/studentCountData" )
    public Result< List< ClazzStudentCountVO > > getStudentCountData(){
        log.info("Learner counts by cohort");
        List< ClazzStudentCountVO > clazzStudentCount = reportService.getStudentCountData();
        return Result.success( clazzStudentCount );
    }

    @GetMapping( "/studentDegreeData" )
    public  Result< List< OptionVO > > getStudentDegreeData(){
        log.info( "Learner education levels" );
        List< OptionVO > studentDegreeData = reportService.getStudentDegreeData();
        return Result.success( studentDegreeData );
    }

    @GetMapping( "/studentSummaryData" )
    public Result< StudentSummaryVO > getStudentSummaryData(){
        log.info( "Learner summary: total learners, learners without a cohort, active cohorts, total cohorts" );
        StudentSummaryVO studentSummaryData = reportService.getStudentSummaryData();
        return Result.success( studentSummaryData );
    }
}
