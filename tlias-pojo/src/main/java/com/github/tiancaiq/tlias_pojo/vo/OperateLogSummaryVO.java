package com.github.tiancaiq.tlias_pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperateLogSummaryVO {
    private Long todayCount;      //
    private Long totalCount;      //
    private List<AvgCostVO> avgCostTimes;    //
    private List<TypeCountVO> totalTypeCounts; //

    @Data
    public static class AvgCostVO {
        private String type;
        private Double avgTime;
    }

    @Data
    public static class TypeCountVO {
        private String type;
        private Integer count;
    }
}