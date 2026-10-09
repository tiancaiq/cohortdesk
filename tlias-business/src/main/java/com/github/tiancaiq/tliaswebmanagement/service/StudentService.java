package com.github.tiancaiq.tliaswebmanagement.service;


import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Student;
import com.github.tiancaiq.tlias_pojo.dto.StudentQueryParam;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface StudentService {
    PageResult page( @NotNull( message = "Learner search criteria are required" ) StudentQueryParam queryParam );

    void save( @NotNull( message = "Learner information is required" ) Student student );

    Student getById( @NotNull( message = "learner id is required" ) @Positive( message = "learner id must be positive" ) Integer id );

    void update( @NotNull( message = "Learner information is required" ) Student student );

    void deleteByIds(  @NotEmpty( message = "Select at least one learner to delete" ) List< Integer > ids );

    List< Student> list();

    void recordViolation( 
            @NotNull( message = "learner id is required" ) @Positive( message = "learner id must be positive" ) Integer id,
            @PositiveOrZero( message = "Deduction cannot be negative" )  Integer score );
}
