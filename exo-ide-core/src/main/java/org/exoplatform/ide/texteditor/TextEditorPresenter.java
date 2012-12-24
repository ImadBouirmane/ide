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
package org.exoplatform.ide.texteditor;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.exoplatform.ide.Resources;
import org.exoplatform.ide.editor.AbstractTextEditorPresenter;
import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.DocumentProvider.DocumentCallback;
import org.exoplatform.ide.editor.SelectionProvider;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.annotation.AnnotationModel;
import org.exoplatform.ide.text.store.TextChange;
import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.texteditor.api.TextEditorPartView;
import org.exoplatform.ide.texteditor.api.TextListener;
import org.exoplatform.ide.util.executor.UserActivityManager;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class TextEditorPresenter extends AbstractTextEditorPresenter
{

   protected TextEditorPartView editor;

   private final TextListener textListener = new TextListener()
   {

      @Override
      public void onTextChange(TextChange textChange)
      {
         if (!isDirty())
         {
            updateDirtyState(true);
         }
      }
   };

   /**
    * @param documentProvider 
    * 
    */
   public TextEditorPresenter(Resources resources, UserActivityManager userActivityManager,
      DocumentProvider documentProvider, TextEditorConfiguration configuration)
   {
      super(configuration, documentProvider);
      editor = new TextEditorViewImpl(resources, userActivityManager);
      editor.getTextListenerRegistrar().add(textListener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initializeEditor()
   {
      editor.configure(configuration);
      documentProvider.getDocument(input, new DocumentCallback()
      {

         @Override
         public void onDocument(Document document)
         {
            TextEditorPresenter.this.document = document;
            AnnotationModel annotationModel = documentProvider.getAnnotationModel(input);
            editor.setDocument((DocumentImpl)document, annotationModel);
            firePropertyChange(PROP_INPUT);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#close(boolean)
    */
   @Override
   public void close(boolean save)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#isEditable()
    */
   @Override
   public boolean isEditable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#doRevertToSaved()
    */
   @Override
   public void doRevertToSaved()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.TextEditorPartPresenter#getSelectionProvider()
    */
   @Override
   public SelectionProvider getSelectionProvider()
   {
      // TODO Auto-generated method stub
      return null;
   }

   protected Widget getWidget()
   {
      HTML h = new HTML();
      h.getElement().appendChild(editor.getElement());
      return h;
   }

   /**
    * @see org.exoplatform.ide.presenter.Presenter#go(com.google.gwt.user.client.ui.HasWidgets)
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(getWidget());
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#getTitleToolTip()
    */
   @Override
   public String getTitleToolTip()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
