package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.JobCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import com.github.tiancaiq.cohortdesk.model.vo.StudentSummaryVO;
import com.github.tiancaiq.cohortdesk.persistence.ReportRepository;
import com.github.tiancaiq.cohortdesk.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repository;

    @Autowired
    public ReportServiceImpl( ReportRepository repository ){
        this.repository = repository;
    }

    @Override
    public List< JobCountVO > getEmpJobData() {
        List< JobCountVO > empJobData = repository.employeeJobs();
        return empJobData;
    }

    @Override
    public List< OptionVO > getEmpGenderData(){
        List< OptionVO > genderToCountMaps = repository.employeeGenders();
        return genderToCountMaps;
    }

    @Override
    public List< ClazzStudentCountVO > getStudentCountData(){
        List< ClazzStudentCountVO > clazzStudentCountData = repository.cohortEnrollment();
        return clazzStudentCountData;
    }

    @Override
    public List< OptionVO > getStudentDegreeData(){
        List< OptionVO > studentDegreeData = repository.studentDegrees();
        return studentDegreeData;
    }

    @Override
    public StudentSummaryVO getStudentSummaryData(){
        return repository.studentSummary();
    }
}
