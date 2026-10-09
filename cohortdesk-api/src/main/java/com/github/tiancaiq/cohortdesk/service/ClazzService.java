package com.github.tiancaiq.cohortdesk.service;


import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ClazzService {
    PageResult page( @NotNull( message = "Cohort search criteria are required" ) ClazzQueryParam queryParam );

    void save(  @NotNull( message = "Cohort information is required" ) Clazz clazz );

    Clazz getById( @NotNull( message = "cohort id is required" ) @Positive( message = "cohort id must be positive" ) Integer id );

    void update(  @NotNull( message = "Cohort information is required" ) Clazz clazz );

    void deleteById( @NotNull( message = "cohort id is required" ) @Positive( message = "cohort id must be positive" ) Integer id );

    List< Clazz> list();
}
