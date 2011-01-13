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

import java.util.Collections;
import org.jboss.logmanager.ExtLogRecord;

import org.apache.log4j.Category;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * A log4j logging event which was converted from a JBoss LogManager {@link org.jboss.logmanager.ExtLogRecord ExtLogRecord}.
 */
public final class ConvertedLoggingEvent extends LoggingEvent {

    private static final long serialVersionUID = -2741722431458191906L;

    /**
     * Construct a new instance.
     *
     * @param rec the log record
     */
    public ConvertedLoggingEvent(final ExtLogRecord rec) {
        super(rec.getLoggerClassName(),
                new DummyCategory(rec.getLoggerName()),
                rec.getMillis(),
                LevelMapping.getPriorityFor(rec.getLevel()),
                rec.getMessage(),
                rec.getThreadName(),
                rec.getThrown() == null ? null : new ThrowableInformation(rec.getThrown()),
                rec.getNdc(),
                new LocationInfo(new Throwable(), rec.getLoggerClassName()),
                Collections.singletonMap("org.jboss.logmanager.record", rec));
    }

    private static final class DummyCategory extends Category {
        protected DummyCategory(String name) {
            super(name);
        }
    }

    /**
     * Get a log record for a log4j event.  If the event wraps a log record, that record is returned; otherwise
     * a new record is built up from the event.
     *
     * @param event the event
     * @return the log record
     */
    public static ExtLogRecord getLogRecordFor(LoggingEvent event) {
        final ExtLogRecord rec = (ExtLogRecord)event.getProperties().get("org.jboss.logmanager.record");
        if (rec != null) {
            return rec;
        }
        final ExtLogRecord newRecord = new ExtLogRecord(LevelMapping.getLevelFor(event.getLevel()), (String) event.getMessage(), event.getFQNOfLoggerClass());
        newRecord.setLoggerName(event.getLoggerName());
        newRecord.setMillis(event.getTimeStamp());
        newRecord.setThreadName(event.getThreadName());
        newRecord.setThrown(event.getThrowableInformation().getThrowable());
        newRecord.setNdc(event.getNDC());
        if (event.locationInformationExists()) {
            final LocationInfo locationInfo = event.getLocationInformation();
            newRecord.setSourceClassName(locationInfo.getClassName());
            newRecord.setSourceFileName(locationInfo.getFileName());
            newRecord.setSourceLineNumber(Integer.parseInt(locationInfo.getLineNumber()));
            newRecord.setSourceMethodName(locationInfo.getMethodName());
        }
        return newRecord;
    }
}
