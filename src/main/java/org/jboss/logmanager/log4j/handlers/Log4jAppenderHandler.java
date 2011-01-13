/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.logmanager.log4j.handlers;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.log4j.ConvertedLoggingEvent;

import java.util.logging.Formatter;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A handler which delegates to a log4j appender.
 */
public final class Log4jAppenderHandler extends ExtHandler {
    private volatile Appender appender = null;
    private final boolean applyLayout;

    private static final AtomicReferenceFieldUpdater<Log4jAppenderHandler, Appender> appenderUpdater = AtomicReferenceFieldUpdater.newUpdater(Log4jAppenderHandler.class, Appender.class, "appender");

    /**
     * Construct a new instance.
     *
     * @param appender the appender to delegate to
     */
    public Log4jAppenderHandler(final Appender appender) {
        this(appender, false);
    }

    /**
     * Construct a new instance, possibly applying a {@code Layout} to the given appender instance.
     *
     * @param appender the appender to delegate to
     * @param applyLayout {@code true} to apply an emulated layout, {@code false} otherwise
     */
    public Log4jAppenderHandler(final Appender appender, final boolean applyLayout) {
        appenderUpdater.set(this, appender);
        this.applyLayout = applyLayout;
        if (applyLayout) {
            appender.setLayout(null);
        }
    }

    /**
     * Get the log4j appender.
     *
     * @return the log4j appender
     */
    public Appender getAppender() {
        return appender;
    }

    /**
     * Set the Log4j appender.
     *
     * @param appender the log4j appender
     */
    public void setAppender(final Appender appender) {
        checkAccess();
        appenderUpdater.set(this, appender);
        if (applyLayout && appender != null) {
            final Formatter formatter = getFormatter();
            appender.setLayout(formatter == null ? null : new FormatterLayout(formatter));
        }
    }

    /** {@inheritDoc} */
    public void setFormatter(final Formatter newFormatter) throws SecurityException {
        if (applyLayout) {
            final Appender appender = this.appender;
            if (appender != null) {
                appender.setLayout(new FormatterLayout(newFormatter));
            }
        }
        super.setFormatter(newFormatter);
    }

    /**
     * Publish a log record.
     *
     * @param record the log record to publish
     */
    protected void doPublish(final ExtLogRecord record) {
        final Appender appender = this.appender;
        if (appender == null) {
            throw new IllegalStateException("Appender is closed");
        }
        final LoggingEvent event = new ConvertedLoggingEvent(record);
        appender.doAppend(event);
        super.doPublish(record);
    }

    /**
     * Do nothing (there is no equivalent method on log4j appenders).
     */
    public void flush() {
    }

    /**
     * Close the handler and its corresponding appender.
     *
     * @throws SecurityException if you are not allowed to close a handler
     */
    public void close() throws SecurityException {
        checkAccess();
        final Appender appender = appenderUpdater.getAndSet(this, null);
        if (appender != null) {
            appender.close();
        }
    }
}
