/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.api.editor;

import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.File;

/**
 * Editor Agent manages Editors, it allows to open a new editor with given file,
 * retrieve current active editor and find all the opened editors.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface EditorAgent {

    /**
     * Open editor with gieven file
     *
     * @param file
     */
    public void openEditor(final File file);

    /**
     * Get all opened editors
     *
     * @return map with all opened editors
     */
    public JsonStringMap<EditorPartPresenter> getOpenedEditors();

    /**
     * Current active editor
     *
     * @return the current active editor
     */
    public EditorPartPresenter getActiveEditor();

}