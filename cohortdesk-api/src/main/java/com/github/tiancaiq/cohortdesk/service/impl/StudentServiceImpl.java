package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.Student;
import com.github.tiancaiq.cohortdesk.model.dto.StudentQueryParam;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import com.github.tiancaiq.cohortdesk.persistence.CoreRepository;
import com.github.tiancaiq.cohortdesk.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public  class StudentServiceImpl implements StudentService {




    private final CoreRepository repository;


    @Autowired
    public StudentServiceImpl( CoreRepository repository ){
        this.repository = repository;
    }


    @Override
    public PageResult page( StudentQueryParam queryParam ){
        return repository.pageStudents(queryParam);
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Student student ){
        student.setCreateTime( LocalDateTime.now() );
        student.setUpdateTime( LocalDateTime.now() );
        repository.saveStudent( student );
    }

    @Override
    public Student getById( Integer id ){
        return repository.findStudent( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Student student ){
        student.setUpdateTime( LocalDateTime.now() );
        repository.updateStudent( student );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteByIds( List< Integer > ids ){
        ids = ids.stream().distinct().toList();
        int affectedRows = repository.deleteStudents( ids );
        if ( affectedRows != ids.size() ){
            throw new BusinessException( "Some learners do not exist. Deletion failed" );
        }
    }

    @Override
    public List< Student > list(){
        return repository.listStudents();
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void recordViolation( Integer id, Integer score ){
        Student student = repository.findStudent( id );
        if ( student == null ){
            throw new BusinessException( "Learner not found" );
        }

        Short violationCount = student.getViolationCount();
        student.setViolationCount( ++violationCount );
        Short violationScore = student.getViolationScore();
        student.setViolationScore( ( short ) (violationScore + score) );

        student.setUpdateTime( LocalDateTime.now() );
        repository.updateStudent( student );
    }



}
