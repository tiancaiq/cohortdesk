package com.github.tiancaiq.tliaswebmanagement.controller;

import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.Result;
import com.github.tiancaiq.tlias_pojo.dto.LoginLogQueryParam;
import com.github.tiancaiq.tlias_pojo.dto.OperateLogQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.LoginLogSummaryVO;
import com.github.tiancaiq.tlias_pojo.vo.OperateLogSummaryVO;
import com.github.tiancaiq.tliaswebmanagement.service.LoginLogService;
import com.github.tiancaiq.tliaswebmanagement.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/logs" )
public class LogController {




    private final OperateLogService operateLogService;
    private final LoginLogService loginLogService;


    @Autowired
    public LogController( OperateLogService operateLogService, LoginLogService loginLogService ){
        this.operateLogService = operateLogService;
        this.loginLogService = loginLogService;
    }


    @GetMapping( "/operation" )
    public Result< PageResult > page( OperateLogQueryParam queryParam ){
        log.info( "Search operation logs with pagination, criteria: {}", queryParam );
        PageResult result = operateLogService.page( queryParam );
        return Result.success( result );
    }

    @GetMapping( "/operation/summary" )
    public Result< OperateLogSummaryVO > getOperateLogSummary(){
        log.info( "Get operation log summary" );
        OperateLogSummaryVO operateLogSummary = operateLogService.getSummaryData();
        return Result.success( operateLogSummary );
    }

    @GetMapping( "/login" )
    public Result< PageResult > page( LoginLogQueryParam queryParam ){
        log.info( "Search login logs with pagination, criteria: {}", queryParam );
        PageResult result = loginLogService.page( queryParam );
        return Result.success( result );
    }

    @GetMapping( "/login/summary" )
    public Result< LoginLogSummaryVO > getLoginLogSummary(){
        log.info( "Get login log summary" );
        LoginLogSummaryVO loginLogSummary = loginLogService.getSummaryData();
        return Result.success( loginLogSummary );
    }




}
