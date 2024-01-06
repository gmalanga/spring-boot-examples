package com.example.postgresqlDemoApp.persistence.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<Route> routeContext = new ThreadLocal<>();

    public static void clearReplicaRoute() {
        routeContext.remove();
    }

    public static void setReplicaRoute() {
        routeContext.set(Route.REPLICA);
    }

    @Override
    public Object determineCurrentLookupKey() {
        return routeContext.get();
    }

    public enum Route {
        PRIMARY, REPLICA
    }
}
