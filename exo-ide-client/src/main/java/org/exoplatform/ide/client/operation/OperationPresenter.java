/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.PreviewForm;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.development.event.PreviewFileEvent;
import org.exoplatform.ide.client.module.development.event.PreviewFileHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationPresenter implements EditorActiveFileChangedHandler, PreviewFileHandler, ViewClosedHandler
{

   private HandlerManager eventBus;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private File activeFile;

   private PreviewForm previewForm;

   public OperationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      handlerRegistrations.put(EditorActiveFileChangedEvent.TYPE, eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this));
      handlerRegistrations.put(PreviewFileEvent.TYPE, eventBus.addHandler(PreviewFileEvent.TYPE, this));
   }
   
   /**
    * Remove handlers, that are no longer needed.
    */
   private void removeHandlers()
   {
      //TODO: such method is not very convenient.
      //If gwt mvp framework will be used , it will be good to use
      //ResettableEventBus class
      for (HandlerRegistration h : handlerRegistrations.values())
      {
         h.removeHandler();
      }
      handlerRegistrations.clear();
   }

   public void destroy()
   {
      removeHandlers();
   }

   public void bindDisplay()
   {

      //      handlers.addHandler(GadgetMetadaRecievedEvent.TYPE, this);
      //      handlers.addHandler(SecurityTokenRecievedEvent.TYPE, this);

   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (previewForm != null)
      {
         IDE.getInstance().closeView(PreviewForm.ID);
         previewForm = null;
      }
   }

   public void onOutput(OutputEvent event)
   {
   }

   public void onPreviewFile(PreviewFileEvent event)
   {
      if (previewForm != null)
      {
         IDE.getInstance().closeView(PreviewForm.ID);
         previewForm = null;
      }

      if (activeFile.isNewFile())
      {
         Dialogs.getInstance().showInfo("You should save the file!");
         return;
      }

      previewForm = new PreviewForm();
      previewForm.showPreview(activeFile.getHref());
      IDE.getInstance().openView(previewForm);
      eventBus.fireEvent(new RestorePerspectiveEvent());

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() == previewForm)
         previewForm = null;
   }

}
