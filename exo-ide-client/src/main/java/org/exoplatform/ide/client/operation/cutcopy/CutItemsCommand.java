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
package org.exoplatform.ide.client.operation.cutcopy;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"administrators", "developers"})
public class CutItemsCommand extends SimpleControl implements IDEControl, VfsChangedHandler, ItemsSelectedHandler,
   ViewActivatedHandler
{

   private static final String ID = "Edit/Cut Item(s)";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.cutItemsTitleControl();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.cutItemsPromptControl();

   private VirtualFileSystemInfo vfsInfo;

   private boolean browserPanelSelected = false;

   private List<Item> selectedItems;

   /**
    * 
    */
   public CutItemsCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.cut(), IDEImageBundle.INSTANCE.cutDisabled());
      setEvent(new CutItemsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent)
    */
   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {

      browserPanelSelected =
         (event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay);
      updateState();
   }

   protected void updateState()
   {
      if (vfsInfo == null || selectedItems == null || selectedItems.size() != 1)
      {
         setEnabled(false);
         return;
      }

      if (selectedItems.get(0) instanceof ProjectModel
         || selectedItems.get(0).getId().equals(vfsInfo.getRoot().getId()))
      {
         setEnabled(false);
         return;
      }

      setEnabled(browserPanelSelected);
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      setVisible(vfsInfo != null);
      updateState();
   }

}