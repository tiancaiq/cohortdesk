package com.github.tiancaiq.tliaswebmanagement.service;


import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.dto.OperateLogQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.OperateLogSummaryVO;
import jakarta.validation.constraints.NotNull;

public interface OperateLogService {
    PageResult page( @NotNull( message = "Operation log search criteria are required" ) OperateLogQueryParam queryParam );

    OperateLogSummaryVO getSummaryData();
}
