package com.campus.trade.security;

import com.campus.trade.common.constant.Constant;
import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.result.ResultCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminOnlyAspect {

    @Around("@annotation(com.campus.trade.common.annotation.AdminOnly) || "
            + "@within(com.campus.trade.common.annotation.AdminOnly)")
    public Object verifyAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> (Constant.ROLE_PREFIX + Constant.ROLE_ADMIN)
                        .equals(authority.getAuthority()));
        if (!admin) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return joinPoint.proceed();
    }
}
