package com.github.tiancaiq.cohortdesk.controller;

import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.annotation.LogOperation;
import com.github.tiancaiq.cohortdesk.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    private final EmpService empService;

    @Autowired
    public LoginController( EmpService empService ){
        this.empService = empService;
    }

    @PostMapping("/login")
    public Result< Object > login( @RequestBody Emp emp ){
        log.info("Employee sign-in attempt: {}", emp.getUsername());
        LoginInfo loginInfo = empService.login(emp);
        if(loginInfo != null){
            return Result.success(loginInfo);
        }
        return Result.error("Incorrect username or password.");
    }

}
