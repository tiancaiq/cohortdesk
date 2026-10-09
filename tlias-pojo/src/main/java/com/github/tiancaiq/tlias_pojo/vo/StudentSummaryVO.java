package com.github.tiancaiq.tlias_pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSummaryVO {




    private Integer totalStudent;
    private Integer studentHaveNoClazz;
    private Integer totalClazz;
    private Integer openClazzCount;
    private Integer endClazzCount;
    private Integer notStartClazzCount;







}
