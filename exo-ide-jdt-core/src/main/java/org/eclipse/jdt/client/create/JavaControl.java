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
package org.eclipse.jdt.client.create;

import org.eclipse.jdt.client.packaging.PackageExplorerPresenter;
import org.eclipse.jdt.client.packaging.model.ProjectItem;
import org.eclipse.jdt.client.packaging.model.ResourceDirectoryItem;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, ViewVisibilityChangedHandler
{

   private View currnetView;

   /**
    * @param id
    */
   public JavaControl(String id)
   {
      super(id);
      setShowInContextMenu(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1 && event.getSelectedItems().get(0) instanceof ItemContext)
      {
         Item item = event.getSelectedItems().get(0);
         ProjectModel project = ((ItemContext)item).getProject();
         if (project != null)
         {
            boolean enabled = isInResourceDirectory(item);
            setEnabled(enabled);
         }
         else
            setEnabled(false);
      }
      else
      {
         setEnabled(false);
      }
   }
   
   private boolean isInResourceDirectory(Item item)
   {
      
//      String sourcePath =
//             project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder")
//                : CreateJavaClassPresenter.DEFAULT_SOURCE_FOLDER;
//          sourcePath = (project.getPath().endsWith("/") ? project.getPath() : project.getPath() + "/") + sourcePath;
//          if (item.getPath().startsWith(sourcePath))
//             setEnabled(true);
//          else
//             setEnabled(false);
      
      
      
      ProjectItem projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
      if (projectItem == null)
      {
         return false;
      }
      
      for (ResourceDirectoryItem resourceDirectory : projectItem.getResourceDirectories())
      {
         if (item.getPath().startsWith(resourceDirectory.getFolder().getPath()))
         {
            return true;
         }
      }
      
      return false;
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
   }

   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      currnetView = event.getView();
      
   }

}