package com.github.tiancaiq.tliaswebmanagement.service.impl;

import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Student;
import com.github.tiancaiq.tlias_pojo.dto.StudentQueryParam;
import com.github.tiancaiq.tliaswebmanagement.exception.BusinessException;
import com.github.tiancaiq.tliaswebmanagement.mapper.StudentMapper;
import com.github.tiancaiq.tliaswebmanagement.service.StudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public  class StudentServiceImpl implements StudentService {




    private final StudentMapper studentMapper;


    @Autowired
    public StudentServiceImpl( StudentMapper studentMapper ){
        this.studentMapper = studentMapper;
    }


    @Override
    public PageResult page( StudentQueryParam queryParam ){
        PageHelper.startPage( queryParam.getPage(), queryParam.getPageSize() );

        List< Student > studentList = studentMapper.selectByQuery( queryParam );
        Page< Student > page = ( Page< Student> ) studentList;

        return new PageResult( page.getTotal(), page.getResult() );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Student student ){
        student.setCreateTime( LocalDateTime.now() );
        student.setUpdateTime( LocalDateTime.now() );
        studentMapper.insert( student );
    }

    @Override
    public Student getById( Integer id ){
        return studentMapper.selectById( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Student student ){
        student.setUpdateTime( LocalDateTime.now() );
        studentMapper.update( student );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteByIds( List< Integer > ids ){
        ids = ids.stream().distinct().toList();
        int affectedRows = studentMapper.deleteByIds( ids );
        if ( affectedRows != ids.size() ){
            throw new BusinessException( "Some learners do not exist. Deletion failed" );
        }
    }

    @Override
    public List< Student > list(){
        return studentMapper.selectAll();
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void recordViolation( Integer id, Integer score ){
        Student student = studentMapper.selectById( id );
        if ( student == null ){
            throw new BusinessException( "Learner not found" );
        }

        Short violationCount = student.getViolationCount();
        student.setViolationCount( ++violationCount );
        Short violationScore = student.getViolationScore();
        student.setViolationScore( ( short ) (violationScore + score) );

        student.setUpdateTime( LocalDateTime.now() );
        studentMapper.updateViolation( student );
    }



}
