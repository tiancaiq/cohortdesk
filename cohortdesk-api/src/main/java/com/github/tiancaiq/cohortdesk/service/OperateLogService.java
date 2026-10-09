package com.github.tiancaiq.cohortdesk.service;


import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.OperateLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.OperateLogSummaryVO;
import jakarta.validation.constraints.NotNull;

public interface OperateLogService {
    PageResult page( @NotNull( message = "Operation log search criteria are required" ) OperateLogQueryParam queryParam );

    OperateLogSummaryVO getSummaryData();
}
