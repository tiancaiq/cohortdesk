package com.github.tiancaiq.cohortdesk.service.impl;

import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.LoginLogSummaryVO;
import com.github.tiancaiq.cohortdesk.mapper.LoginLogMapper;
import com.github.tiancaiq.cohortdesk.service.LoginLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class LoginLogServiceImpl implements LoginLogService {




    private final LoginLogMapper loginLogMapper;


    @Autowired
    public LoginLogServiceImpl( LoginLogMapper loginLogMapper ){
        this.loginLogMapper = loginLogMapper;
    }


    @Override
    public PageResult page( LoginLogQueryParam queryParam ){
        PageHelper.startPage( queryParam.getPage(), queryParam.getPageSize() );

        List< LoginLog > logList = loginLogMapper.selectByQuery( queryParam );
        Page< LoginLog > logPage = ( Page< LoginLog> ) logList;

        return new PageResult( logPage.getTotal(), logPage.getResult() );
    }

    @Override
    public LoginLogSummaryVO getSummaryData(){
        LoginLogSummaryVO summaryData = new LoginLogSummaryVO();

        summaryData.setTodayLoginCount( loginLogMapper.countTodayLogin() );
        summaryData.setTodayFailCount( loginLogMapper.countTodayFail() );
        summaryData.setTotalSuccessFail( loginLogMapper.countTotalSuccessFail() );
        summaryData.setFailRank( loginLogMapper.selectFailRank() );

        return summaryData;
    }



}
