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
package org.exoplatform.ide.core.editor;

import com.google.inject.Inject;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.texteditor.BaseTextEditor;
import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.util.executor.UserActivityManager;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DefaultEditorProvider implements EditorProvider
{

   private final DocumentProvider documentProvider;

   private final TextEditorConfiguration configuration = new TextEditorConfiguration();

   private final Resources resources;

   private final UserActivityManager activityManager;

   @Inject
   public DefaultEditorProvider(Resources resources, UserActivityManager activityManager,
      DocumentProvider documentProvider)
   {
      super();
      this.resources = resources;
      this.activityManager = activityManager;
      this.documentProvider = documentProvider;
   }

   /**
    * @see org.exoplatform.ide.editor.EditorProvider#getEditor()
    */
   @Override
   public EditorPartPresenter getEditor()
   {
      return new BaseTextEditor(resources, activityManager, documentProvider, configuration);
   }

}