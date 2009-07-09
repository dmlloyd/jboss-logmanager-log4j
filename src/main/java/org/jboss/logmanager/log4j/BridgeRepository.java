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

package org.jboss.logmanager.log4j;

import java.util.Enumeration;
import java.util.Collections;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;

import org.jboss.logmanager.LogContext;

public final class BridgeRepository implements LoggerRepository {
    private final org.jboss.logmanager.Logger.AttachmentKey<BridgeLogger> reposKey = new org.jboss.logmanager.Logger.AttachmentKey<BridgeLogger>();

    public void addHierarchyEventListener(final HierarchyEventListener listener) {
        // ignore
    }

    public boolean isDisabled(final int level) {
        return false;
    }

    public void setThreshold(final Level level) {
        // ignore
    }

    public void setThreshold(final String val) {
        // ignore
    }

    public void emitNoAppenderWarning(final Category cat) {
        // ignore
    }

    public Level getThreshold() {
        return Level.ALL;
    }

    public Logger getLogger(final String name) {
        final org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLogger(name);
        final Logger logger = lmLogger.getAttachment(reposKey);
        if (logger != null) {
            return logger;
        }
        return create(lmLogger);
    }

    public Logger getLogger(final String name, final LoggerFactory factory) {
        return getLogger(name);
    }

    public Logger getRootLogger() {
        return getLogger("");
    }

    public Logger exists(final String name) {
        final org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLoggerIfExists(name);
        if (lmLogger == null) {
            return null;
        }
        return create(lmLogger);
    }

    private Logger create(final org.jboss.logmanager.Logger lmLogger) {
        final BridgeLogger logger = new BridgeLogger(lmLogger);
        final BridgeLogger appearingLogger = lmLogger.attachIfAbsent(reposKey, logger);
        return appearingLogger != null ? appearingLogger : logger;
    }

    public void shutdown() {
        // ignore
    }

    public Enumeration getCurrentLoggers() {
        return Collections.enumeration(Collections.emptySet());
    }

    public Enumeration getCurrentCategories() {
        return Collections.enumeration(Collections.emptySet());
    }

    public void fireAddAppenderEvent(final Category logger, final Appender appender) {
        // ignore
    }

    public void resetConfiguration() {
        // ignore
    }
}
