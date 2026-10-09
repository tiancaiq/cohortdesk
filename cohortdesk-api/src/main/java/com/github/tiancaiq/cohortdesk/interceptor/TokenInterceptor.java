package com.github.tiancaiq.cohortdesk.interceptor;

import com.github.tiancaiq.cohortdesk.util.CurrentHolder;
import com.github.tiancaiq.cohortdesk.util.JwtUtils;
import com.github.tiancaiq.cohortdesk.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();

//            return true;
//        }
        String uri = request.getRequestURI();
        if ("/login".equals(uri)) {
            log.info("Login request , allow request");
            return true;
        }

        String jwt = request.getHeader("token");

        if(!StringUtils.hasLength(jwt)){
            log.warn("ReceivedjwtToken missing, return an error");
            response.setStatus( HttpStatus.SC_UNAUTHORIZED);
            throw new BusinessException( "Sign in to continue." );
        }

        try {
            Claims claims = JwtUtils.parseJWT( jwt );
            Integer empId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(empId);
        } catch (Exception e) {
            log.warn( "Could not parse token, return an error", e );
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            throw new BusinessException("Your session has expired. Please sign in again");
        }

        log.info("Token valid, allow");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("Request complete, clearing ThreadLocal data");
        CurrentHolder.remove();
    }
}
