package com.github.tiancaiq.tliaswebmanagement.service.impl;

import com.github.tiancaiq.tlias_pojo.Dept;
import com.github.tiancaiq.tlias_pojo.Emp;
import com.github.tiancaiq.tliaswebmanagement.exception.BusinessException;
import com.github.tiancaiq.tliaswebmanagement.mapper.DeptMapper;
import com.github.tiancaiq.tliaswebmanagement.mapper.EmpMapper;
import com.github.tiancaiq.tliaswebmanagement.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public class DeptServiceImpl implements DeptService {




    private final DeptMapper deptMapper;
    private final EmpMapper empMapper;


    @Autowired
    public DeptServiceImpl( DeptMapper deptMapper, EmpMapper empMapper ){
        this.deptMapper = deptMapper;
        this.empMapper = empMapper;
    }


    @Override
    public List< Dept > findAll(){
        return deptMapper.selectAll();
    }

    @Override
    public Dept findById( Integer id ){
        return deptMapper.selectById( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteById( Integer id ){
        List< Emp > empList = empMapper.selectByDeptId( id );
        if ( empList != null && ! empList.isEmpty() ){
            throw new BusinessException( "This department has employees and cannot be deleted, cannot be deleted" );
        }

        int affectedRow = deptMapper.deleteById( id );
        if ( affectedRow <= 0 ){
            throw new BusinessException( "Department not found" );
        }
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void add( Dept dept ){
        dept.setCreateTime( LocalDateTime.now() );
        dept.setUpdateTime( LocalDateTime.now() );
        deptMapper.insert( dept );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void updateById( Dept dept ){
        validateDeptId( dept.getId() );
        dept.setUpdateTime( LocalDateTime.now() );
        deptMapper.updateById( dept );
    }


    private void validateDeptId( Integer deptId ){
        if ( deptId == null || deptId <= 0 ){
            throw new BusinessException( "department id must be a positive number" );
        }
    }


}
