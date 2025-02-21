package com.k10.readReplica;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

@Slf4j
@Component
@Aspect
public class DataSourceAspect {

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}

    @Before("transactionalMethods()")
    public void logDataSource(JoinPoint joinPoint) {
        TransactionSynchronizationManager.getResource(DataSource.class);
        String currentDataSourceKey = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel() != null ?
                TransactionSynchronizationManager.getCurrentTransactionName() : "Unknown";

        log.info("Method: {} is using DataSource: {}",
                joinPoint.getSignature().getName(),
                currentDataSourceKey);
    }
}