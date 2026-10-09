package com.github.tiancaiq.cohortdesk.mapper;

import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.LoginLogSummaryVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoginLogMapper {
    @Insert( """
            INSERT INTO emp_login_log
                ( username, password, login_time, is_success, jwt, cost_time )
            VALUES ( #{username}, #{password}, #{loginTime}, #{isSuccess}, #{jwt}, #{costTime} )
            """ )
    void insert( LoginLog loginLog );

    List< LoginLog > selectByQuery( LoginLogQueryParam queryParam );

    Long countTodayLogin();

    Long countTodayFail();

    List< LoginLogSummaryVO.PieChartVO> countTotalSuccessFail();

    List< LoginLogSummaryVO.FailRankVO> selectFailRank();
}
