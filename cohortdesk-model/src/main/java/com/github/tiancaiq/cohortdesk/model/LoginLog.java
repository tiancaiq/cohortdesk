package com.github.tiancaiq.cohortdesk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLog {
    private Integer id; //ID
    private String username;
    private String password;
    private LocalDateTime loginTime;
    private Short isSuccess;
    private String jwt;
    private Long costTime;
}