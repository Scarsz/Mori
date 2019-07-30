package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class MinlogFactory implements ILoggerFactory {

    private final MinlogAdapter logger;

    public MinlogFactory() {
        this.logger = new MinlogAdapter();
    }

    @Override
    public Logger getLogger(String name) {
        return logger;
    }

}
