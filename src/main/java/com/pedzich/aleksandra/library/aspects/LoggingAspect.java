package com.pedzich.aleksandra.library.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.pedzich.aleksandra.library.services.*.*(..))")
    public Object aroundOnServiceFunction(
            ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();

        String method = proceedingJoinPoint.getSignature().toShortString();
        long duration = end - begin;
        System.out.println("Successfully executed method: " + method);
        System.out.println("Duration: " + duration / 1000.0 + " seconds");

        return result;
    }

    @AfterThrowing(
            pointcut="execution(* com.pedzich.aleksandra.library.services.*.*(..))",
            throwing="exc")
    public void afterThrowingOnServiceFunction(
            JoinPoint joinPoint, Throwable exc) {
        String method = joinPoint.getSignature().toShortString();
        System.out.println("Exception was thrown while executing method: " + method);
        System.out.println("The exception was: " + exc);

    }

}
