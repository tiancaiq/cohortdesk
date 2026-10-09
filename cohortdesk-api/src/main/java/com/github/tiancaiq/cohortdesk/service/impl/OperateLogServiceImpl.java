package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.OperateLog;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.OperateLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.OperateLogSummaryVO;
import com.github.tiancaiq.cohortdesk.persistence.AuditRepository;
import com.github.tiancaiq.cohortdesk.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class OperateLogServiceImpl implements OperateLogService {




    private final AuditRepository repository;


    @Autowired
    public OperateLogServiceImpl( AuditRepository repository ){
        this.repository = repository;
    }


    @Override
    public PageResult page( OperateLogQueryParam queryParam ){
        return repository.pageOperations(queryParam);
    }

    @Override
    public OperateLogSummaryVO getSummaryData(){
        return repository.operationSummary();
    }



}
