/*
 * Copyright 2015 The FireNio Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firenio.log;

import org.slf4j.LoggerFactory;

import com.firenio.common.Util;
import com.firenio.component.FastThreadLocal;

public class SLF4JLogger implements Logger {

    private org.slf4j.Logger logger = null;

    private String name;

    public SLF4JLogger(Class<?> clazz) {
        this(clazz.getSimpleName());
    }

    public SLF4JLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
        this.name = name;
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String msg, Object param) {
        logger.debug(msg, param);
    }

    @Override
    public void debug(String msg, Object... param) {
        logger.debug(msg, param);
    }

    @Override
    public void debug(String msg, Object param, Object param1) {
        logger.debug(msg, param, param1);
    }

    @Override
    public void debug(String msg, Throwable e) {
        StringBuilder sb = FastThreadLocal.get().getStringBuilder();
        sb.append(msg);
        logger.debug(Util.stackTraceToString(e, true));
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String msg, Object param) {
        logger.error(msg, param);
    }

    @Override
    public void error(String msg, Object... params) {
        logger.error(msg, params);
    }

    @Override
    public void error(String msg, Object param, Object param1) {
        logger.error(msg, param, param1);
    }

    @Override
    public void error(String msg, Throwable e) {
        StringBuilder sb = FastThreadLocal.get().getStringBuilder();
        sb.append(msg);
        logger.error(Util.stackTraceToString(e, true));
    }

    @Override
    public void error(Throwable e) {
        logger.error(Util.stackTraceToString(e, true));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String msg, Object param) {
        logger.info(msg, param);
    }

    @Override
    public void info(String msg, Object... param) {
        logger.info(msg, param);
    }

    @Override
    public void info(String msg, Object param, Object param1) {
        logger.info(msg, param, param1);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String msg, Object param) {
        logger.warn(msg, param);
    }

    @Override
    public void warn(String msg, Object... params) {
        logger.warn(msg, params);
    }

    @Override
    public void warn(String msg, Object param, Object param1) {
        logger.warn(msg, param, param1);
    }

    @Override
    public void warn(String msg, Throwable e) {
        StringBuilder sb = FastThreadLocal.get().getStringBuilder();
        sb.append(msg);
        logger.warn(Util.stackTraceToString(e, true));
    }

}
