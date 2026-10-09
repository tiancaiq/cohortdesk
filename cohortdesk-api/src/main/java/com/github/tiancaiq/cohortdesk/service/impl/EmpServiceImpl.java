package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.EmpExpr;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.EmpQueryParam;
import com.github.tiancaiq.cohortdesk.util.JwtUtils;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import com.github.tiancaiq.cohortdesk.persistence.CoreRepository;
import com.github.tiancaiq.cohortdesk.service.EmpService;
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




    private final CoreRepository repository;


    @Autowired
    public EmpServiceImpl( CoreRepository repository ){
        this.repository = repository;
    }


    @Override
    public List< Emp > list(){
        List< Emp > empList = repository.listEmployees();
        return empList;
    }


    @Override
    public PageResult page( EmpQueryParam queryParam ){
        return repository.pageEmployees(queryParam);
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void save( Emp emp ){
        emp.setCreateTime( LocalDateTime.now() );
        emp.setUpdateTime( LocalDateTime.now() );

        repository.saveEmployee( emp );

        Integer empId = emp.getId();
        List< EmpExpr > exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            repository.saveExperience(exprList);
        }
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void deleteByIds( List< Integer > ids ){
        ids = ids.stream().distinct().toList();
        repository.deleteExperience( ids );
        int affectedRows = repository.deleteEmployees( ids );
        if ( affectedRows != ids.size() ){
            throw new BusinessException( "Some employees do not exist. Deletion failed" );
        }
    }

    @Override
    public Emp getInfo( Integer id ){
        Emp emp = repository.findEmployee( id );
        if (emp == null) return null;
        List< EmpExpr > empExprList = repository.experienceFor( id );
        emp.setExprList( empExprList );
        return emp;
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public void update( Emp emp ){
        emp.setUpdateTime(LocalDateTime.now());
        repository.updateEmployee(emp);

        repository.deleteExperience( Arrays.asList( emp.getId() ) );

        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            repository.saveExperience(exprList);
        }
    }

    @Override
    public LoginInfo login( Emp emp ){
        Emp empLogin = repository.authenticate( emp.getUsername(), emp.getPassword() );
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
        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            throw new BusinessException("Enter both passwords.");
        }
        Emp emp = repository.findEmployee( empId );
        if ( emp == null ){
            throw new BusinessException( "Employee not found" );
        }

        if ( ! emp.getPassword().equals( oldPassword ) ){
            throw new BusinessException( "Current password is incorrect" );
        }

        repository.updatePassword( empId, newPassword );
    }



}
