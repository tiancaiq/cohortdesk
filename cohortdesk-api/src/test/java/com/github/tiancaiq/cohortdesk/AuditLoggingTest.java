package com.github.tiancaiq.cohortdesk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tiancaiq.cohortdesk.aspect.RecordLogAspect;
import com.github.tiancaiq.cohortdesk.mapper.LoginLogMapper;
import com.github.tiancaiq.cohortdesk.mapper.OperateLogMapper;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.LoginInfo;
import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.OperateLog;
import com.github.tiancaiq.cohortdesk.model.Result;
import com.github.tiancaiq.cohortdesk.model.dto.PasswordDto;
import com.github.tiancaiq.cohortdesk.util.CurrentHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditLoggingTest {
    @AfterEach
    void clearCurrentEmployee() {
        CurrentHolder.remove();
    }

    @Test
    void loginAuditKeepsOutcomeButNeverCredentials() throws Throwable {
        LoginLogMapper loginMapper = mock(LoginLogMapper.class);
        RecordLogAspect aspect = new RecordLogAspect(
                loginMapper, new ObjectMapper(), mock(OperateLogMapper.class));
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Emp employee = new Emp();
        employee.setUsername("alex");
        employee.setPassword("demo-secret");
        when(joinPoint.getArgs()).thenReturn(new Object[]{employee});
        when(joinPoint.proceed()).thenReturn(
                Result.success(new LoginInfo(10, "alex", "Alex", "sensitive-jwt")));

        aspect.recordLoginLog(joinPoint);

        ArgumentCaptor<LoginLog> saved = ArgumentCaptor.forClass(LoginLog.class);
        verify(loginMapper).insert(saved.capture());
        assertEquals("alex", saved.getValue().getUsername());
        assertEquals((short) 1, saved.getValue().getIsSuccess());
        assertTrue(saved.getValue().getCostTime() >= 0);
    }

    @Test
    void operationAuditRedactsPasswordArgumentsAndResponseData() throws Throwable {
        OperateLogMapper operateMapper = mock(OperateLogMapper.class);
        RecordLogAspect aspect = new RecordLogAspect(
                mock(LoginLogMapper.class), new ObjectMapper(), operateMapper);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("updatePassword");
        when(joinPoint.getArgs()).thenReturn(new Object[]{
                new PasswordDto("old-secret", "new-secret")});
        when(joinPoint.proceed()).thenReturn(
                Result.success(new LoginInfo(10, "alex", "Alex", "sensitive-jwt")));
        CurrentHolder.setCurrentId(10);

        aspect.recordOperateLog(joinPoint, null);

        ArgumentCaptor<OperateLog> saved = ArgumentCaptor.forClass(OperateLog.class);
        verify(operateMapper).insert(saved.capture());
        assertEquals(10, saved.getValue().getOperateEmpId());
        assertFalse(saved.getValue().getMethodParams().contains("secret"));
        assertFalse(saved.getValue().getReturnValue().contains("sensitive-jwt"));
        assertEquals("Result code: 1", saved.getValue().getReturnValue());
    }
}
