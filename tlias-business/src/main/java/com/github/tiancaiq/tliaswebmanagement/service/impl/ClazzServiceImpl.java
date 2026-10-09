package com.github.tiancaiq.tliaswebmanagement.service.impl;

import com.github.tiancaiq.tlias_pojo.Clazz;
import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Student;
import com.github.tiancaiq.tlias_pojo.dto.ClazzQueryParam;
import com.github.tiancaiq.tliaswebmanagement.exception.BusinessException;
import com.github.tiancaiq.tliaswebmanagement.mapper.ClazzMapper;
import com.github.tiancaiq.tliaswebmanagement.mapper.StudentMapper;
import com.github.tiancaiq.tliaswebmanagement.service.ClazzService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional( readOnly = true )
public class ClazzServiceImpl implements ClazzService {




    private final ClazzMapper clazzMapper;
    private final StudentMapper studentMapper;


    @Autowired
    public ClazzServiceImpl( ClazzMapper clazzMapper, StudentMapper studentMapper ){
        this.clazzMapper = clazzMapper;
        this.studentMapper = studentMapper;
    }


    @Override
    public PageResult page( ClazzQueryParam queryParam ){
        PageHelper.startPage( queryParam.getPage(), queryParam.getPageSize() );

        List< Clazz > clazzList = clazzMapper.selectByQuery( queryParam );
        Page< Clazz > page = ( Page< Clazz > ) clazzList;

        return new PageResult( page.getTotal(), page.getResult() );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Clazz clazz ){
        clazz.setCreateTime( LocalDateTime.now() );
        clazz.setUpdateTime( LocalDateTime.now() );
        clazzMapper.insert( clazz );
    }

    @Override
    public Clazz getById( Integer id ){
        return clazzMapper.selectById( id );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Clazz clazz ){
        validateClazzId( clazz.getId() );
        validateClazzName( clazz.getName() );

        clazz.setUpdateTime( LocalDateTime.now() );
        int affectedRow = clazzMapper.updateById( clazz );
        if ( affectedRow <= 0 ){
            throw new BusinessException( "The cohort does not exist or no changes were made" );
        }
    }


    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteById( Integer id ){
        List< Student > students = studentMapper.selectByClazzId( id );
        if ( students != null && ! students.isEmpty() ){
            throw new BusinessException( "This cohort has learners and cannot be deleted" );
        }

        int affectedRow = clazzMapper.deleteById( id );
        if ( affectedRow <= 0 ){
            throw new BusinessException( "Cohort not found" );
        }
    }

    @Override
    public List< Clazz > list(){
        return clazzMapper.selectAll();
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
