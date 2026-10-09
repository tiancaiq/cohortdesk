package com.github.tiancaiq.cohortdesk.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpExpr {
    private Integer id; //ID
    private Integer empId;
    private LocalDate begin;
    private LocalDate end;
    private String company;
    private String job;
}