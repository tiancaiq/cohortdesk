package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.Dept;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import com.github.tiancaiq.cohortdesk.persistence.CoreRepository;
import com.github.tiancaiq.cohortdesk.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public class DeptServiceImpl implements DeptService {




    private final CoreRepository repository;


    @Autowired
    public DeptServiceImpl( CoreRepository repository ){
        this.repository = repository;
    }


    @Override
    public List< Dept > findAll(){
        return repository.listDepartments();
    }

    @Override
    public Dept findById( Integer id ){
        return repository.findDepartment( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteById( Integer id ){
        List< Emp > empList = repository.employeesInDepartment( id );
        if ( empList != null && ! empList.isEmpty() ){
            throw new BusinessException( "This department has employees and cannot be deleted, cannot be deleted" );
        }

        if ( !repository.deleteDepartment( id ) ){
            throw new BusinessException( "Department not found" );
        }
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void add( Dept dept ){
        dept.setCreateTime( LocalDateTime.now() );
        dept.setUpdateTime( LocalDateTime.now() );
        repository.saveDepartment( dept );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void updateById( Dept dept ){
        validateDeptId( dept.getId() );
        dept.setUpdateTime( LocalDateTime.now() );
        repository.updateDepartment( dept );
    }


    private void validateDeptId( Integer deptId ){
        if ( deptId == null || deptId <= 0 ){
            throw new BusinessException( "department id must be a positive number" );
        }
    }


}
