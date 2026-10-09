package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.EmpExpr;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.EmpQueryParam;
import com.github.tiancaiq.cohortdesk.util.JwtUtils;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import com.github.tiancaiq.cohortdesk.mapper.EmpExprMapper;
import com.github.tiancaiq.cohortdesk.mapper.EmpMapper;
import com.github.tiancaiq.cohortdesk.service.EmpService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional( readOnly = true )
public class EmpServiceImpl implements EmpService {




    private final EmpExprMapper empExprMapper;
    private final EmpMapper empMapper;


    @Autowired
    public EmpServiceImpl( EmpMapper empMapper, EmpExprMapper empExprMapper ){
        this.empMapper = empMapper;
        this.empExprMapper = empExprMapper;
    }


    @Override
    public List< Emp > list(){
        List< Emp > empList = empMapper.selectAll();
        return empList;
    }


    @Override
    public PageResult page( EmpQueryParam queryParam ){
        PageHelper.startPage( queryParam.getPage(), queryParam.getPageSize() );

        List< Emp > empList = empMapper.selectByQuery( queryParam );
        Page< Emp > empPage = ( Page< Emp> ) empList;

        return new PageResult( empPage.getTotal(), empPage.getResult() );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Emp emp ){
        emp.setCreateTime( LocalDateTime.now() );
        emp.setUpdateTime( LocalDateTime.now() );

        empMapper.insert( emp );

        Integer empId = emp.getId();
        List< EmpExpr > exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteByIds( List< Integer > ids ){
        ids = ids.stream().distinct().toList();
        empExprMapper.deleteBatchByEmpIds( ids );
        int affectedRows = empMapper.deleteBatch( ids );
        if ( affectedRows != ids.size() ){
            throw new BusinessException( "Some employees do not exist. Deletion failed" );
        }
    }

    @Override
    public Emp getInfo( Integer id ){
        Emp emp = empMapper.selectById( id );
        List< EmpExpr > empExprList = empExprMapper.findByEmpId( id );
        emp.setExprList( empExprList );
        return emp;
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Emp emp ){
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        empExprMapper.deleteBatchByEmpIds( Arrays.asList( emp.getId() ) );

        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    }

    @Override
    public LoginInfo login( Emp emp ){
        Emp empLogin = empMapper.selectByUsernameAndPassword( emp.getUsername(), emp.getPassword() );
        if ( empLogin == null ){
            return null;
        }

        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("id", empLogin.getId());
        dataMap.put("username", empLogin.getUsername());

        String jwt = JwtUtils.generateJwt( dataMap );
        return new LoginInfo( empLogin.getId(), empLogin.getUsername(), empLogin.getName(), jwt );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void updatePassword( Integer empId, String oldPassword, String newPassword ){
        Emp emp = empMapper.selectById( empId );
        if ( emp == null ){
            throw new BusinessException( "Employee not found" );
        }

        if ( ! emp.getPassword().equals( oldPassword ) ){
            throw new BusinessException( "Current password is incorrect" );
        }

        empMapper.updatePasswordById( empId, newPassword );
    }



}
