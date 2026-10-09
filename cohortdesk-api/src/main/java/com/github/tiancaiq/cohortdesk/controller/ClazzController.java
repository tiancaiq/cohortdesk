package com.github.tiancaiq.cohortdesk.controller;

import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import com.github.tiancaiq.cohortdesk.annotation.LogOperation;
import com.github.tiancaiq.cohortdesk.service.ClazzService;
import com.github.tiancaiq.cohortdesk.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping( "/clazzs" ) // http://localhost:8080/clazzs
@RestController
public  class ClazzController {




    private final ClazzService clazzService;
    private final EmpService empService;


    @Autowired
    public ClazzController( ClazzService clazzService, EmpService empService ){
        this.clazzService = clazzService;
        this.empService = empService;
    }


    @GetMapping
    public Result< PageResult > page( ClazzQueryParam queryParam ){
        log.info( "Search with pagination, Search criteria: {}", queryParam );
        PageResult result = clazzService.page( queryParam );
        return Result.success( result );
    }

    @GetMapping( "/list" )
    public Result< List< Clazz > > list(){
        log.info( "List all cohorts" );
        List< Clazz > clazzList = clazzService.list();
        return Result.success( clazzList );
    }

    @GetMapping( "/listHeadTeachers" )
    public Result< List< Emp > > listHeadTeachers(){
        log.info( "List cohort leads (all employees)" );
        List< Emp > empList = empService.list();
        return Result.success( empList );
    }

    @LogOperation
    @PostMapping
    public Result< Object > save( @RequestBody Clazz clazz ){
        log.info( "Add cohort: {}", clazz );
        clazzService.save( clazz );
        return Result.success();
    }

    @GetMapping( "/{id}" )
    public Result< Clazz > getById( @PathVariable Integer id ){
        log.info( "Find cohort: id = {}", id );
        Clazz clazz = clazzService.getById( id );
        return Result.success( clazz );
    }

    @LogOperation
    @PutMapping
    public Result< Clazz > update( @RequestBody Clazz clazz ){
        log.info( "Update cohort: {}", clazz );
        clazzService.update( clazz );
        return Result.success( clazz );
    }

    @LogOperation
    @DeleteMapping( "/{id}" )
    public Result< Object > delete( @PathVariable Integer id ){
        log.info( "Delete cohort, id = {}", id );
        clazzService.deleteById( id );
        return Result.success();
    }




}
