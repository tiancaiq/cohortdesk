package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.JobCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import com.github.tiancaiq.cohortdesk.model.vo.StudentSummaryVO;
import com.github.tiancaiq.cohortdesk.mapper.ClazzMapper;
import com.github.tiancaiq.cohortdesk.mapper.EmpMapper;
import com.github.tiancaiq.cohortdesk.mapper.StudentMapper;
import com.github.tiancaiq.cohortdesk.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class ReportServiceImpl implements ReportService {

    private final EmpMapper empMapper;
    private final StudentMapper studentMapper;
    private final ClazzMapper clazzMapper;

    @Autowired
    public ReportServiceImpl( EmpMapper empMapper, StudentMapper studentMapper, ClazzMapper clazzMapper ){
        this.empMapper = empMapper;
        this.studentMapper = studentMapper;
        this.clazzMapper = clazzMapper;
    }

    @Override
    public List< JobCountVO > getEmpJobData() {
        List< JobCountVO > empJobData = empMapper.countEmpJobData();
        return empJobData;
    }

    @Override
    public List< OptionVO > getEmpGenderData(){
        List< OptionVO > genderToCountMaps = empMapper.countEmpGenderData();
        return genderToCountMaps;
    }

    @Override
    public List< ClazzStudentCountVO > getStudentCountData(){
        List< ClazzStudentCountVO > clazzStudentCountData = studentMapper.countStudentCountData();
        return clazzStudentCountData;
    }

    @Override
    public List< OptionVO > getStudentDegreeData(){
        List< OptionVO > studentDegreeData = studentMapper.countStudentDegreeData();
        return studentDegreeData;
    }

    @Override
    public StudentSummaryVO getStudentSummaryData(){
        StudentSummaryVO studentSummary = new StudentSummaryVO();

        studentSummary.setTotalStudent( studentMapper.countAll() );
        studentSummary.setStudentHaveNoClazz( studentMapper.countStudentHaveNoClazz() );
        studentSummary.setTotalClazz( clazzMapper.countAll() );
        studentSummary.setOpenClazzCount( clazzMapper.countOpenClazz() );
        studentSummary.setEndClazzCount( clazzMapper.countEndClazz() );
        studentSummary.setNotStartClazzCount( clazzMapper.countNotStart() );

        return studentSummary;
    }
}