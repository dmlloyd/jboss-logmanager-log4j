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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.LogManager;

import org.jboss.logmanager.Logger;
import org.jboss.logmanager.Level;

/**
 * A simple repository selector which chooses the {@link BridgeRepository} always.  Contains a {@code start()} method to
 * facilitate container deployment.
 */
public final class BridgeRepositorySelector implements RepositorySelector {

    private static final Object guard = new Object();
    private static final Logger log = Logger.getLogger(BridgeRepositorySelector.class.getName());

    private final BridgeRepository bridgeRepository = new BridgeRepository();

    public LoggerRepository getLoggerRepository() {
        return bridgeRepository;
    }

    /**
     * Container install method.  Performs two steps: installs this repository selector into the log4j logmanager, and
     * clears the system property which prevents log4j from initializing (thus allowing other log4j instances to be
     * started and configured independently).
     */
    public void start() {
        LogManager.setRepositorySelector(this, guard);
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                try {
                    System.clearProperty("log4j.defaultInitOverride");
                } catch (SecurityException ex) {
                    log.log(Level.WARN, "Unable to clear system property 'log4j.defaultInitOverride': " +
                            ex.getMessage() + " (nested log4j deployments may not function as expected");
                }
                return null;
            }
        });
    }
}
