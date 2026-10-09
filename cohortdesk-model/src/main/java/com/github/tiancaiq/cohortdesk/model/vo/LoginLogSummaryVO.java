package com.github.tiancaiq.cohortdesk.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogSummaryVO {
    private Long todayLoginCount;
    private Long todayFailCount;
    private List<PieChartVO> totalSuccessFail;
    private List<FailRankVO> failRank;

    @Data
    @AllArgsConstructor
    public static class PieChartVO {
        private String name; 
        private Integer value;
    }

    @Data
    public static class FailRankVO {
        private String username;
        private Integer count;
    }
}