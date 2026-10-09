package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.LoginLogSummaryVO;
import com.github.tiancaiq.cohortdesk.persistence.AuditRepository;
import com.github.tiancaiq.cohortdesk.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class LoginLogServiceImpl implements LoginLogService {




    private final AuditRepository repository;


    @Autowired
    public LoginLogServiceImpl( AuditRepository repository ){
        this.repository = repository;
    }


    @Override
    public PageResult page( LoginLogQueryParam queryParam ){
        return repository.pageLogins(queryParam);
    }

    @Override
    public LoginLogSummaryVO getSummaryData(){
        return repository.loginSummary();
    }



}
