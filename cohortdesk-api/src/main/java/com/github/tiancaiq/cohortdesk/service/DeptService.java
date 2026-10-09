package com.github.tiancaiq.cohortdesk.service;


import com.github.tiancaiq.cohortdesk.model.Dept;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface DeptService {
    List< Dept > findAll();

    Dept findById( @NotNull( message = "department id is required" ) @Positive( message = "department id must be positive" ) Integer id );

    void deleteById( @NotNull( message = "department id is required" ) @Positive( message = "department id must be positive" ) Integer id );

    void add( @NotNull( message = "Department information is required" ) Dept dept );

    void updateById( @NotNull( message = "Department information is required" ) Dept dept );
}
