package com.github.tiancaiq.cohortdesk.service;


import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.LoginLogSummaryVO;
import jakarta.validation.constraints.NotNull;

public interface LoginLogService {
    PageResult page( @NotNull( message = "Login log search criteria are required" ) LoginLogQueryParam queryParam );

    LoginLogSummaryVO getSummaryData();
}
