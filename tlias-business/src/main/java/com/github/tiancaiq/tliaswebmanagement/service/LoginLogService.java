package com.github.tiancaiq.tliaswebmanagement.service;


import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.dto.LoginLogQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.LoginLogSummaryVO;
import jakarta.validation.constraints.NotNull;

public interface LoginLogService {
    PageResult page( @NotNull( message = "Login log search criteria are required" ) LoginLogQueryParam queryParam );

    LoginLogSummaryVO getSummaryData();
}
