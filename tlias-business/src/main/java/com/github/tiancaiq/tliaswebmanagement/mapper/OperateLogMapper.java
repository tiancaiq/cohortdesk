package com.github.tiancaiq.tliaswebmanagement.mapper;

import com.github.tiancaiq.tlias_pojo.OperateLog;
import com.github.tiancaiq.tlias_pojo.dto.OperateLogQueryParam;
import com.github.tiancaiq.tlias_pojo.vo.OperateLogSummaryVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperateLogMapper {
    @Insert( """
            insert into operate_log 
                ( operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time ) 
            values ( #{operateEmpId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime} )
            """ )
    void insert( OperateLog operateLog );

    List< OperateLog > selectByQuery( OperateLogQueryParam queryParam );

    @Select( "SELECT count( 0 ) FROM operate_log" )
    Long countAll();

    @Select( """
            SELECT count( 0 ) FROM operate_log
            WHERE operate_time >= curdate()
                AND operate_time < DATE_ADD( CURDATE(), INTERVAL 1 DAY )
            """ )
    Long countToday();

    List< OperateLogSummaryVO.TypeCountVO> countTotalOperateTypes();

    List< OperateLogSummaryVO.AvgCostVO> selectAvgCostTimes();

}
