package com.github.tiancaiq.tliaswebmanagement.controller;

import com.github.tiancaiq.tlias_pojo.Emp;
import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Result;
import com.github.tiancaiq.tlias_pojo.dto.EmpQueryParam;
import com.github.tiancaiq.tlias_pojo.dto.PasswordDto;
import com.github.tiancaiq.tlias_util.CurrentHolder;
import com.github.tiancaiq.tliaswebmanagement.annotation.LogOperation;
import com.github.tiancaiq.tliaswebmanagement.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping( "/emps" ) // http://localhost:8080/emps
@RestController
public  class EmpController {




    private EmpService empService;


    @Autowired
    public EmpController( EmpService empService ){
        this.empService = empService;
    }



    @GetMapping( "/list" )
    public Result< List< Emp > > list( ){
        log.info( "List all employees" );
        List< Emp > empList = empService.list();
        return Result.success( empList );
    }

    @GetMapping // http://localhost:8080/emps?name=&gender=1&begin=2010-01-01&end=2026-01-01&pageStart=1&pageSize=10
    public Result< PageResult > page( EmpQueryParam queryParam ){
        log.info( "Search employees with pagination, criteria: {}", queryParam );
        PageResult pageResult = empService.page( queryParam );
        return Result.success( pageResult );
    }

    @GetMapping( "/{id}" )
    public Result< Emp > getInfo( @PathVariable Integer id ){
        log.info( "Find employee: id = {}", id );
        Emp emp = empService.getInfo( id );
        return Result.success( emp );
    }

    @LogOperation
    @PostMapping
    public Result< Emp > save( @RequestBody Emp emp ){
        log.info( "Add employee: {}", emp );
        empService.save( emp );
        return Result.success( emp );
    }

    @LogOperation
    @DeleteMapping
    public Result< Object > delete( @RequestParam( "ids" ) List< Integer > ids ){
        log.info( "Delete employee: ids = {}", ids );
        empService.deleteByIds( ids );
        return Result.success();
    }

    @LogOperation
    @PutMapping
    public Result< Emp > update( @RequestBody Emp emp ){
        log.info( "Update employee: {}", emp );
        empService.update( emp );
        return Result.success( emp );
    }

    @LogOperation
    @PutMapping( "/password" )
    public Result< Object > updatePassword( @RequestBody PasswordDto passwordDto ){
        log.info( "Change employee password, Request data: {}", passwordDto );
        Integer empId = CurrentHolder.getCurrentId();
        String oldPassword = passwordDto.getOldPassword();
        String newPassword = passwordDto.getNewPassword();

        empService.updatePassword( empId, oldPassword, newPassword );
        return Result.success();
    }


}
