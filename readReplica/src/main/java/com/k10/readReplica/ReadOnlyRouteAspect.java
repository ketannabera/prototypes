package com.k10.readReplica;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ReadOnlyRouteAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object enableReadReplicaForGET(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            RoutingDataSource.setReadOnly(true);
            return joinPoint.proceed();
        } finally {
            RoutingDataSource.clear();
        }
    }
}
