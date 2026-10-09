package com.github.tiancaiq.cohortdesk.controller;

import com.github.tiancaiq.cohortdesk.model.Dept;
import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.annotation.LogOperation;
import com.github.tiancaiq.cohortdesk.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@RequestMapping( "/depts" )
@RestController
public  class DeptController {




    private final DeptService deptService;


    @Autowired
    public DeptController( DeptService deptService ){
        this.deptService = deptService;
    }


    @LogOperation
    @PostMapping
    public Result< Object > add( @RequestBody Dept dept ){
        log.info( "Add department: {}", dept );
        deptService.add( dept );
        return Result.success(  );
    }

    @LogOperation
    @DeleteMapping
    public Result< Object > delete( @RequestParam( "id" ) Integer id ){
        log.info( "Delete department, id is {}", id );
        deptService.deleteById( id );
        return Result.success(  );
    }

    @LogOperation
    @PutMapping // ?id=1
    public Result< Dept > update( @RequestBody Dept dept ){
        log.info( "Update department: {}", dept );
        deptService.updateById( dept );
        return Result.success( dept );
    }

    @GetMapping
//            ( "/list" )
    public Result< List< Dept > > list(){
        log.info( "List all departments" );
        List< Dept > depts = deptService.findAll();
        return Result.success( depts );
    }

    @GetMapping( "/{id}" )
    public Result< Dept > getById( @PathVariable( "id" ) Integer id ){
        log.info( "Find department with ID {}", id );
        Dept dept = deptService.findById( id );
        return Result.success( dept );
    }




}
