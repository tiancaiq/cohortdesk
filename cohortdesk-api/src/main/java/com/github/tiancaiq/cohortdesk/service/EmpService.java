package com.github.tiancaiq.cohortdesk.service;


import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.EmpQueryParam;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface EmpService {
    List< Emp > list();

    PageResult page( @NotNull( message = "Employee search criteria are required" ) EmpQueryParam queryParam );

    void save( @NotNull( message = "Employee information is required" ) Emp emp );

    void deleteByIds( @NotEmpty( message = "Select at least one employee to delete" ) List< Integer > ids );

    Emp getInfo( @NotNull( message = "employee id is required" ) @Positive( message = "employee id must be positive" ) Integer id );

    void update( @NotNull( message = "Employee information is required" ) Emp emp );

    LoginInfo login( @NotNull( message = "Employee information is required" ) Emp emp );

    void updatePassword(
            @NotNull( message = "employee id is required" ) @Positive( message = "employee id must be positive" ) Integer empId, 
            @NotEmpty( message = "Current password is required" ) String oldPassword,
            @NotEmpty( message = "New password is required" ) String newPassword );
}
