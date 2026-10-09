package com.github.tiancaiq.cohortdesk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tiancaiq.cohortdesk.exception.UnauthorizedException;
import com.github.tiancaiq.cohortdesk.interceptor.TokenInterceptor;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.util.CurrentHolder;
import com.github.tiancaiq.cohortdesk.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationTest {
    @AfterEach
    void clearCurrentEmployee() {
        CurrentHolder.remove();
    }

    @Test
    void rejectsInvalidTokenAndClearsPreviousEmployee() {
        TokenInterceptor interceptor = new TokenInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/emps/list");
        when(request.getHeader("token")).thenReturn("invalid");
        CurrentHolder.setCurrentId(99);

        assertThrows(UnauthorizedException.class,
                () -> interceptor.preHandle(request, response, new Object()));
        assertNull(CurrentHolder.getCurrentId());
    }

    @Test
    void acceptsValidTokenAndClearsEmployeeAfterRequest() throws Exception {
        TokenInterceptor interceptor = new TokenInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/emps/list");
        when(request.getHeader("token")).thenReturn(JwtUtils.generateJwt(Map.of("id", 10)));

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(10, CurrentHolder.getCurrentId());
        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(CurrentHolder.getCurrentId());
    }

    @Test
    void passwordIsAcceptedButNotReturned() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Emp employee = mapper.readValue(
                "{\"username\":\"alex\",\"password\":\"demo-secret\"}", Emp.class);

        assertEquals("demo-secret", employee.getPassword());
        assertFalse(mapper.writeValueAsString(employee).contains("demo-secret"));
        assertFalse(employee.toString().contains("demo-secret"));
    }
}
