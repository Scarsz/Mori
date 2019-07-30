package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    public static String REQUESTED_API_VERSION = "1.6";
    private final ILoggerFactory factory;

    public static final StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    private StaticLoggerBinder() {
        factory = new MinlogFactory();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return factory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return MinlogFactory.class.getName();
    }

}
