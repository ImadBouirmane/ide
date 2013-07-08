/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.html.server;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;

/**
 * Default implementation of {@link ApplicationInstance}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstanceImpl.java Jun 26, 2013 1:05:49 PM azatsarynnyy $
 */
public class ApplicationInstanceImpl implements ApplicationInstance {
    /** Application's port. */
    private String name;

    /** Application's port. */
    private int    port     = 80;

    /** Lifetime of application instance in minutes. After this time application may be stopped automatically. */
    private int    lifetime = -1;

    /**
     * Constructs new instance of {@link ApplicationInstanceImpl} with the given name and lifetime.
     * 
     * @param name app's name
     * @param lifetime app's lifetime
     */
    public ApplicationInstanceImpl(String name, int lifetime) {
        this.name = name;
        this.lifetime = lifetime;
    }

    /**
     * Constructs new instance of {@link ApplicationInstanceImpl} with the given name.
     * 
     * @param name app's name
     */
    public ApplicationInstanceImpl(String name) {
        this.name = name;
    }

    public ApplicationInstanceImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public String toString() {
        return "ApplicationInstanceImpl{" +
               "name='" + name + '\'' +
               ", port=" + port +
               ", lifetime=" + lifetime +
               '}';
    }
}
