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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Manages instances of Searcher.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SearcherProvider {
    /**
     * Get Searcher for specified MountPoint.
     *
     * @param mountPoint
     *         MountPoint
     * @param create
     *         <code>true</code> to create new Searcher if there is no Searcher for specified <code>mountPoint</code> and <code>false</code>
     *         to return <code>null</code> if there is no Searcher
     * @return instance of Searcher
     * @throws VirtualFileSystemException
     * @see MountPoint
     */
    Searcher getSearcher(MountPoint mountPoint, boolean create) throws VirtualFileSystemException;
}