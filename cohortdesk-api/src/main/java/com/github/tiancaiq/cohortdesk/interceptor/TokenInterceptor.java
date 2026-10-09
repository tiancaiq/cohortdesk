package com.github.tiancaiq.cohortdesk.interceptor;

import com.github.tiancaiq.cohortdesk.util.CurrentHolder;
import com.github.tiancaiq.cohortdesk.util.JwtUtils;
import com.github.tiancaiq.cohortdesk.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CurrentHolder.remove();
        String uri = request.getRequestURI();
        if ("/login".equals(uri)) {
            return true;
        }

        String jwt = request.getHeader("token");

        if(!StringUtils.hasText(jwt)){
            throw new UnauthorizedException("Sign in to continue.");
        }

        try {
            Claims claims = JwtUtils.parseJWT( jwt );
            Integer empId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(empId);
        } catch (Exception e) {
            throw new UnauthorizedException("Your session has expired. Please sign in again.");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentHolder.remove();
    }
}
