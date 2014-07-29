/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions.delete;

/**
 * Provides a way to delete an item .
 *
 * @param <T>
 *         type of the object which this provider can delete
 * @author Artem Zatsarynnyy
 */
public interface DeleteProvider<T> {
    /**
     * Perform deleting the specified item.
     *
     * @param item
     *         item to delete
     */
    void deleteItem(T item);

    /**
     * Checks whether it is possible to delete the specified item?
     *
     * @param item
     *         item to check
     * @return <code>true</code> if the specified item may be deleted and <code>false</code> otherwise
     */
    boolean canDelete(Object item);
}
