package com.github.tiancaiq.tliaswebmanagement.exception;

import com.github.tiancaiq.tlias_pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException( BusinessException e) {
        log.warn("Business error: {}", e.getMessage(), e); 
        return Result.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("Unexpected system error: ", e);
        return Result.error("System error. Try again later or contact an administrator");
    }

    @ExceptionHandler({DataAccessException.class, MyBatisSystemException.class})
    public Result<String> handleDataAccessException(Exception e) {
        log.error("Database access error（MySQL may be offline or unreachable）", e);

        return Result.error("The database is temporarily unavailable");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler( DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("Unique constraint violation（DuplicateKeyException）", e);

        String detail = e.getMessage();
        if (e.getCause() != null) {
            detail += " | " + e.getCause().getMessage();
        }
        String lowerDetail = detail.toLowerCase();

        String errorMessage = "Duplicate data. Check the entered information.";

        if (lowerDetail.contains("dept.name")) {
            errorMessage = "This department name is already in use";
        } else if (lowerDetail.contains("emp.username")) {
            errorMessage = "This username is already in use";
        } else if (lowerDetail.contains("emp.phone")) {
            errorMessage = "This employee phone number is already in use";
        } else if (lowerDetail.contains("clazz.name")) {
            errorMessage = "This cohort name is already in use";
        } else if (lowerDetail.contains("student.no")) {
            errorMessage = "This learner ID is already in use";
        } else if (lowerDetail.contains("student.phone")) {
            errorMessage = "This learner phone number is already in use";
        } else if (lowerDetail.contains("student.id_card")) {
            errorMessage = "This ID number is already in use";
        }

        return Result.error(errorMessage);
    }



}
