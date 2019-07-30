package org.slf4j.impl;

import com.esotericsoftware.minlog.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class MinlogAdapter implements Logger {

    @Override
    public String getName() {
        return "Minlog";
    }

    @Override
    public boolean isInfoEnabled() {
        return Log.INFO;
    }
    @Override
    public boolean isInfoEnabled(Marker marker) {
        return Log.INFO;
    }

    @Override
    public boolean isWarnEnabled() {
        return Log.WARN;
    }
    @Override
    public boolean isWarnEnabled(Marker marker) {
        return Log.WARN;
    }

    @Override
    public boolean isErrorEnabled() {
        return Log.ERROR;
    }
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return Log.ERROR;
    }

    @Override
    public boolean isDebugEnabled() {
        return Log.DEBUG;
    }
    @Override
    public boolean isDebugEnabled(Marker marker) {
        return Log.DEBUG;
    }

    @Override
    public boolean isTraceEnabled() {
        return Log.TRACE;
    }
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return Log.TRACE;
    }

    @Override
    public void info(String msg) {
        Log.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        Log.info(String.format(format, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        Log.info(String.format(format, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        Log.info(String.format(format, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        Log.info(msg, t);
    }

    @Override
    public void info(Marker marker, String msg) {
        Log.info(marker.getName());
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        Log.info(marker.getName(), String.format(format, arg));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        Log.info(marker.getName(), String.format(format, arg1, arg2));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        Log.info(marker.getName(), String.format(format, arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        Log.info(marker.getName(), msg, t);
    }

    @Override
    public void warn(String msg) {
        Log.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        Log.warn(String.format(format, arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
        Log.warn(String.format(format, arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        Log.warn(String.format(format, arg1, arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
        Log.warn(msg, t);
    }

    @Override
    public void warn(Marker marker, String msg) {
        Log.warn(marker.getName(), msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        Log.warn(marker.getName(), String.format(format, arg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        Log.warn(marker.getName(), String.format(format, arg1, arg2));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        Log.warn(marker.getName(), String.format(format, arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        Log.warn(marker.getName(), msg, t);
    }

    @Override
    public void error(String msg) {
        Log.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        Log.error(String.format(format, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        Log.error(String.format(format, arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        Log.error(String.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        Log.error(msg, t);
    }

    @Override
    public void error(Marker marker, String msg) {
        Log.error(marker.getName(), msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        Log.error(marker.getName(), String.format(format, arg));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        Log.error(marker.getName(), String.format(format, arg1, arg2));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        Log.error(marker.getName(), String.format(format, arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        Log.error(marker.getName(), msg, t);
    }

    @Override
    public void debug(String msg) {
        Log.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        Log.debug(String.format(format, arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        Log.debug(String.format(format, arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        Log.debug(String.format(format, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        Log.debug(msg, t);
    }

    @Override
    public void debug(Marker marker, String msg) {
        Log.debug(marker.getName(), msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        Log.debug(marker.getName(), String.format(format, arg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        Log.debug(marker.getName(), String.format(format, arg1, arg2));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        Log.debug(marker.getName(), String.format(format, arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        Log.debug(marker.getName(), msg, t);
    }

    @Override
    public void trace(String msg) {
        Log.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        Log.trace(String.format(format, arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        Log.trace(String.format(format, arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        Log.trace(String.format(format, arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        Log.trace(msg, t);
    }

    @Override
    public void trace(Marker marker, String msg) {
        Log.trace(marker.getName(), msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        Log.trace(marker.getName(), String.format(format, arg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        Log.trace(marker.getName(), String.format(format, arg1, arg2));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        Log.trace(marker.getName(), String.format(format, argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        Log.trace(marker.getName(), msg, t);
    }

}
