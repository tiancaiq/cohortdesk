package com.github.tiancaiq.tliaswebmanagement.controller;

import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Result;
import com.github.tiancaiq.tlias_pojo.Student;
import com.github.tiancaiq.tlias_pojo.dto.StudentQueryParam;
import com.github.tiancaiq.tliaswebmanagement.annotation.LogOperation;
import com.github.tiancaiq.tliaswebmanagement.service.StudentService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping( "/students" )
@RestController
public  class StudentController {




    private final StudentService studentService;
    private final EmpService empService;


    @Autowired
    public StudentController( StudentService studentService, EmpService empService ){
        this.studentService = studentService;
        this.empService = empService;
    }


    @GetMapping
    public Result< PageResult > page( StudentQueryParam queryParam ){
        log.info( "Search with pagination, Search criteria: {}", queryParam );
        PageResult result = studentService.page( queryParam );
        return Result.success( result );
    }

    @GetMapping( "/list" )
    public Result< List< Student > > list(){
        log.info( "List all learners" );
        List< Student > studentList = studentService.list();
        return Result.success( studentList );
    }

    @LogOperation
    @PostMapping
    public Result< Object > save( @RequestBody Student student ){
        log.info( "Add learner: {}", student );
        studentService.save( student );
        return Result.success();
    }

    @GetMapping( "/{id}" )
    public Result< Student > getById( @PathVariable Integer id ){
        log.info( "Find learner: id = {}", id );
        Student student = studentService.getById( id );
        return Result.success( student );
    }

    @LogOperation
    @PutMapping
    public Result< Student > update( @RequestBody Student student ){
        log.info( "Update learner: {}", student );
        studentService.update( student );
        return Result.success( student );
    }

    @LogOperation
    @DeleteMapping( "/{ids}" )
    public Result< Object > delete( @PathVariable List< Integer > ids ){
        log.info( "Delete learner, ids = {}", ids );
        studentService.deleteByIds( ids );
        return Result.success();
    }

    @LogOperation
    @PutMapping( "/violation/{id}/{score}" )
    public Result< Object > recordViolation( @PathVariable( "id" ) Integer id, @PathVariable( "score" ) Integer score ){
        log.info( "incident record, id = {}, deduct points = {}", id, score );
        studentService.recordViolation( id, score );
        return Result.success();
    }




}
