package com.github.tiancaiq.tliaswebmanagement.service.impl;

import com.github.tiancaiq.tlias_pojo.OperateLog;
import com.github.tiancaiq.tlias_pojo.PageResult;
import com.github.tiancaiq.tlias_pojo.dto.OperateLogQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.OperateLogSummaryVO;
import com.github.tiancaiq.tliaswebmanagement.mapper.OperateLogMapper;
import com.github.tiancaiq.tliaswebmanagement.service.OperateLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional( readOnly = true )
public class OperateLogServiceImpl implements OperateLogService {




    private final OperateLogMapper operateLogMapper;


    @Autowired
    public OperateLogServiceImpl( OperateLogMapper operateLogMapper ){
        this.operateLogMapper = operateLogMapper;
    }


    @Override
    public PageResult page( OperateLogQueryParam queryParam ){
        PageHelper.startPage( queryParam.getPage(), queryParam.getPageSize() );

        List< OperateLog > logList = operateLogMapper.selectByQuery( queryParam );
        Page< OperateLog > logPage = ( Page< OperateLog> ) logList;

        return new PageResult( logPage.getTotal(), logPage.getResult() );
    }

    @Override
    public OperateLogSummaryVO getSummaryData(){
        OperateLogSummaryVO summaryData = new OperateLogSummaryVO();

        summaryData.setTotalCount( operateLogMapper.countAll() );
        summaryData.setTodayCount( operateLogMapper.countToday() );
        summaryData.setTotalTypeCounts( operateLogMapper.countTotalOperateTypes() );
        summaryData.setAvgCostTimes( operateLogMapper.selectAvgCostTimes() );

        return summaryData;
    }



}
