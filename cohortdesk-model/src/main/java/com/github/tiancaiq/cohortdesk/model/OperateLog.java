package com.github.tiancaiq.cohortdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operate_log")
public class OperateLog {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer operateEmpId;

    private LocalDateTime operateTime;

    private String className;

    private String methodName;

    private String methodParams;

    private String returnValue;

    private Long costTime;

    @Transient
    private String operateEmpName;







}
