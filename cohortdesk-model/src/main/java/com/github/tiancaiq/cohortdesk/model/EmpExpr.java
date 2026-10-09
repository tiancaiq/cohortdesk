package com.github.tiancaiq.cohortdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "emp_expr")
public class EmpExpr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //ID
    private Integer empId;
    private LocalDate begin;
    private LocalDate end;
    private String company;
    private String job;
}
