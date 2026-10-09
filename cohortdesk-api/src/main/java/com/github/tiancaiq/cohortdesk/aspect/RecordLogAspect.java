package com.github.tiancaiq.cohortdesk.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.OperateLog;
import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.util.CurrentHolder;
import com.github.tiancaiq.cohortdesk.annotation.LogOperation;
import com.github.tiancaiq.cohortdesk.mapper.LoginLogMapper;
import com.github.tiancaiq.cohortdesk.mapper.OperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
//@Order( 2 )
public class RecordLogAspect {




    private final OperateLogMapper operateLogMapper;
    private final ObjectMapper jsonMapper;
    private final LoginLogMapper loginLogMapper;



    public RecordLogAspect( LoginLogMapper loginLogMapper, ObjectMapper jsonMapper, OperateLogMapper operateLogMapper ){
        this.loginLogMapper = loginLogMapper;
        this.jsonMapper = jsonMapper;
        this.operateLogMapper = operateLogMapper;
    }


    @Around("@annotation(logOperation)")
    public Object recordOperateLog(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();  
        Object result = null;
        Throwable exception = null;                   

        try {
            result = joinPoint.proceed();             
            return result;
        } catch (Throwable e) {
            exception = e;                            
            throw e;                                  
        } finally {
            long costTime = System.currentTimeMillis() - startTime;

            OperateLog operateLog = new OperateLog(); 

            operateLog.setOperateEmpId(getOperatorId());
            operateLog.setOperateTime(LocalDateTime.now());
            operateLog.setClassName(joinPoint.getTarget().getClass().getName());
            operateLog.setMethodName(joinPoint.getSignature().getName());
            operateLog.setCostTime(costTime);

            try {
                operateLog.setMethodParams(jsonMapper.writeValueAsString(joinPoint.getArgs()));
            } catch (Exception e) {
                operateLog.setMethodParams("[Could not serialize parameters]");
            }

            if (exception != null) {
                operateLog.setReturnValue("Error: " + exception.getClass().getSimpleName()
                        + " - " + exception.getMessage());
            } else {
                try {
                    operateLog.setReturnValue(jsonMapper.writeValueAsString(result));
                } catch (Exception e) {
                    operateLog.setReturnValue("[Could not serialize response]");
                }
            }

            try {
                operateLogMapper.insert(operateLog);
            } catch (Exception e) {
                RecordLogAspect.log.error("Could not record operation log", e);
            }
        }
    }


    @Around("execution(* com.github.tiancaiq.cohortdesk.controller.LoginController.login*(..))")
    public Object recordLoginLog( ProceedingJoinPoint joinPoint) throws Throwable {
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginTime(LocalDateTime.now());

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Emp) {
            Emp emp = (Emp) args[0];
            loginLog.setUsername(emp.getUsername());
            loginLog.setPassword(emp.getPassword());
        }

        long startTime = System.currentTimeMillis();
        Object resultObj;

        try {
            resultObj = joinPoint.proceed();

            loginLog.setCostTime(System.currentTimeMillis() - startTime);

            if (resultObj instanceof Result) {
                Result<?> result = (Result<?>) resultObj;
                boolean success = (result.getCode() == 1);
                loginLog.setIsSuccess(success ? (short) 1 : (short) 0);

                if (success && result.getData() instanceof LoginInfo) {
                    loginLog.setJwt(((LoginInfo) result.getData()).getToken());
                }
            }
        } catch (Throwable e) {
            loginLog.setCostTime(System.currentTimeMillis() - startTime);
            loginLog.setIsSuccess((short) 0);
            throw e;
        } finally {
            try {
                loginLogMapper.insert( loginLog );
            } catch (Exception e) {
                log.error( "Could not record login log", e );
            }
        }

        return resultObj;
    }


    private Integer getOperatorId(){
        return CurrentHolder.getCurrentId();
    }

}
