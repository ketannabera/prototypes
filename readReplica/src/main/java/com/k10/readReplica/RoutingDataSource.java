package com.k10.readReplica;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<Boolean> isReadOnly = ThreadLocal.withInitial(() -> false);

    @Override
    protected Object determineCurrentLookupKey() {
        return isReadOnly.get() ? "REPLICA" : "PRIMARY";
    }

    public static void setReadOnly(boolean readOnly) {
        isReadOnly.set(readOnly);
    }

    public static void clear() {
        isReadOnly.remove();
    }
}
