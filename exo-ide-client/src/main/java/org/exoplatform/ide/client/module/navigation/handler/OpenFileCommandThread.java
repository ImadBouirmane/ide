// $codepro.audit.disable logExceptions
/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileCommandThread implements OpenFileHandler, FileContentReceivedHandler, ExceptionThrownHandler,
   ItemPropertiesReceivedHandler, ApplicationSettingsReceivedHandler, ItemLockedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private String selectedEditor;

   private ApplicationSettings applicationSettings;

   private ApplicationContext context;

   private Map<String, String> lockTokens;

   public OpenFileCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(OpenFileEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onOpenFile(OpenFileEvent event)
   {
      selectedEditor = event.getEditor();

      File file = event.getFile();
      if (file != null)
      {
         if (file.isNewFile())
         {
            openFile(file);
            return;
         }

         //TODO Check opened file!!!
         String lockToken = lockTokens.get(file.getHref());
         if (lockToken != null)
         {
            openFile(file);
            return;
         }
      }
      else
      {
         file = new File(event.getHref());
      }

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ItemLockedEvent.TYPE, this);

      VirtualFileSystem.getInstance().getProperties(file);
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      File file = (File)event.getItem();
      for (Property p : file.getProperties())
      {
         if (ItemProperty.Namespace.JCR.equals(p.getName().getNamespaceURI())
            && ItemProperty.JCR_LOCKOWNER.getLocalName().equalsIgnoreCase(p.getName().getLocalName()))
         {
            VirtualFileSystem.getInstance().getContent((File)event.getItem());
            return;
         }
      }

      VirtualFileSystem.getInstance().lock(event.getItem(), 600, context.getUserInfo().getName());
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent)
    */
   public void onItemLocked(ItemLockedEvent event)
   {
      File file = (File)event.getItem();
      if (file.getContent() != null)
      {
         openFile(file);
         return;
      }
      VirtualFileSystem.getInstance().getContent((File)event.getItem());
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      handlers.removeHandlers();
      openFile(event.getFile());
   }

   @SuppressWarnings("unchecked")
   private void openFile(File file)
   {
      handlers.removeHandlers();

      try
      {
         if (selectedEditor == null)
         {
            Map<String, String> defaultEditors = (Map<String, String>)applicationSettings.getValue("default-editors");
            if (defaultEditors != null)
            {
               selectedEditor = defaultEditors.get(file.getContentType());
            }
         }

         Editor editor = EditorUtil.getEditor(file.getContentType(), selectedEditor);
         eventBus.fireEvent(new EditorOpenFileEvent(file, editor));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError("Can't find editor for type <b>" + file.getContentType() + "</b>");
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   @SuppressWarnings("unchecked")
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValue("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = (Map<String, String>)applicationSettings.getValue("lock-tokens");
   }

}
