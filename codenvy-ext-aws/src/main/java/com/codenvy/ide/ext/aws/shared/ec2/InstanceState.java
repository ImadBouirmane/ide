/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.shared.ec2;

/**
 * EC2 instance state
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum InstanceState {
    pending("pending"),
    running("running"),
    shutting_down("shutting-down"),
    terminated("terminated"),
    stopping("stopping"),
    stopped("stopped");

    private final String value;

    private InstanceState(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    /**
     * Use this in place of valueOf.
     *
     * @param value
     *         real value
     * @return InstanceState corresponding to the value
     */
    public static InstanceState fromValue(String value) {
        for (InstanceState v : InstanceState.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}