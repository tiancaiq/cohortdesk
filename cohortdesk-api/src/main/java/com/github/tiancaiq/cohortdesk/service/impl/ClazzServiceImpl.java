package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.Student;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import com.github.tiancaiq.cohortdesk.persistence.CoreRepository;
import com.github.tiancaiq.cohortdesk.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public class ClazzServiceImpl implements ClazzService {




    private final CoreRepository repository;


    @Autowired
    public ClazzServiceImpl( CoreRepository repository ){
        this.repository = repository;
    }


    @Override
    public PageResult page( ClazzQueryParam queryParam ){
        return repository.pageCohorts(queryParam);
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Clazz clazz ){
        clazz.setCreateTime( LocalDateTime.now() );
        clazz.setUpdateTime( LocalDateTime.now() );
        repository.saveCohort( clazz );
    }

    @Override
    public Clazz getById( Integer id ){
        return repository.findCohort( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Clazz clazz ){
        validateClazzId( clazz.getId() );
        validateClazzName( clazz.getName() );

        clazz.setUpdateTime( LocalDateTime.now() );
        if ( !repository.updateCohort( clazz ) ){
            throw new BusinessException( "The cohort does not exist or no changes were made" );
        }
    }


    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteById( Integer id ){
        List< Student > students = repository.studentsInCohort( id );
        if ( students != null && ! students.isEmpty() ){
            throw new BusinessException( "This cohort has learners and cannot be deleted" );
        }

        if ( !repository.deleteCohort( id ) ){
            throw new BusinessException( "Cohort not found" );
        }
    }

    @Override
    public List< Clazz > list(){
        return repository.listCohorts();
    }


    private void validateClazzName( String clazzName ){
        if ( clazzName == null || clazzName.trim().isBlank() ){
            throw new BusinessException( "Cohort name is required" );
        }
    }

    private void validateClazzId( Integer clazzId ){
        if ( clazzId == null || clazzId <= 0 ){
            throw new BusinessException( "Cohort ID must be a positive number" );
        }
    }

}
