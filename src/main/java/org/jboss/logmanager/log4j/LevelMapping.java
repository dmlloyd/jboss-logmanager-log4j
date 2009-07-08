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

import java.util.Map;
import java.util.IdentityHashMap;

import java.util.logging.Level;
import org.apache.log4j.Priority;

import org.jboss.logmanager.log4j.handlers.Log4jJDKLevel;

public final class LevelMapping {

    private static final Map<Level, org.apache.log4j.Level> priorityMap;

    private LevelMapping() {
    }

    static {
        final Map<Level, org.apache.log4j.Level> map = new IdentityHashMap<Level, org.apache.log4j.Level>();
        map.put(Level.SEVERE, Log4jJDKLevel.SEVERE);
        map.put(Level.WARNING, Log4jJDKLevel.WARNING);
        map.put(Level.CONFIG, Log4jJDKLevel.CONFIG);
        map.put(Level.INFO, Log4jJDKLevel.INFO);
        map.put(Level.FINE, Log4jJDKLevel.FINE);
        map.put(Level.FINER, Log4jJDKLevel.FINER);
        map.put(Level.FINEST, Log4jJDKLevel.FINEST);

        map.put(org.jboss.logmanager.Level.FATAL, org.apache.log4j.Level.FATAL);
        map.put(org.jboss.logmanager.Level.ERROR, org.apache.log4j.Level.ERROR);
        map.put(org.jboss.logmanager.Level.WARN, org.apache.log4j.Level.WARN);
        map.put(org.jboss.logmanager.Level.INFO, org.apache.log4j.Level.INFO);
        map.put(org.jboss.logmanager.Level.DEBUG, org.apache.log4j.Level.DEBUG);
        map.put(org.jboss.logmanager.Level.TRACE, org.apache.log4j.Level.TRACE);
        priorityMap = map;
    }

    static org.apache.log4j.Level getPriorityFor(Level level) {
        final org.apache.log4j.Level p;
        return (p = priorityMap.get(level)) == null ? org.apache.log4j.Level.DEBUG : p;
    }

    static Level getLevelFor(Priority level) {
        switch (level.toInt()) {
            case org.apache.log4j.Level.TRACE_INT:
                return org.jboss.logmanager.Level.TRACE;
            case org.apache.log4j.Level.DEBUG_INT:
                return org.jboss.logmanager.Level.DEBUG;
            case org.apache.log4j.Level.INFO_INT:
                return org.jboss.logmanager.Level.INFO;
            case org.apache.log4j.Level.WARN_INT:
                return org.jboss.logmanager.Level.WARN;
            case org.apache.log4j.Level.ERROR_INT:
                return org.jboss.logmanager.Level.ERROR;
            case org.apache.log4j.Level.FATAL_INT:
                return org.jboss.logmanager.Level.FATAL;
            default:
                return org.jboss.logmanager.Level.DEBUG;
        }
    }
}