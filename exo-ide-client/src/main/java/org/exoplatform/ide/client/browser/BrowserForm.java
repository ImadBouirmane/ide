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
package org.exoplatform.ide.client.browser;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.ApplicationContext;

import java.util.List;
import java.util.Map;

public class BrowserForm extends View implements BrowserPanel, BrowserPresenter.Display
{

   private final String TREE_ID = "ideNavigatorItemTreeGrid";

   public static final String TITLE = "Workspace";

   private ItemTree treeGrid;

   private BrowserPresenter presenter;

   public BrowserForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(ID, eventBus);
      treeGrid = new ItemTree();
      ScrollPanel treeWrapper = new ScrollPanel(treeGrid);
      treeWrapper.ensureDebugId("Tree-itemTree-Wrapper");
      treeWrapper.setSize("100%", "100%");
//      treeGrid.getElement().setAttribute("style", "width:100%;height:100%");
      addMember(treeWrapper);
      
//      treeGrid = new ItemTreeGrid<Item>(TREE_ID);
//      treeGrid.setShowHeader(false);
//      treeGrid.setLeaveScrollbarGap(false);
//      treeGrid.setShowOpenIcons(true);
//      treeGrid.setEmptyMessage("Root folder not found!");
//
//      treeGrid.setSelectionType(SelectionStyle.MULTIPLE);
//
//      treeGrid.setHeight100();
//      treeGrid.setWidth100();
      presenter = new BrowserPresenter(eventBus);
      presenter.bindDisplay(this);

      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            //            ViewHighlightManager.getInstance().selectView(BrowserForm.this);
            //            event.cancel();
         }
      });
   }

   @Override
   public void destroy()
   {
      super.destroy();
      presenter.destroy();
   }

   public TreeGridItem<Item> getBrowserTree()
   {
      return treeGrid;
   }

   public void selectItem(String path)
   {
      treeGrid.selectItem(path);
   }

   public List<Item> getSelectedItems()
   {
      return treeGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#updateItemState(org.exoplatform.ide.client.framework.vfs.File)
    */
   public void updateItemState(File file)
   {
      treeGrid.updateFileState(file);
   }

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#setLockTokens(java.util.Map)
    */
   public void setLockTokens(Map<String, String> locktokens)
   {
      treeGrid.setLocktokens(locktokens);
   }

   /**
    * @see org.exoplatform.ide.client.browser.BrowserPresenter.Display#deselectItem(java.lang.String)
    */
   public void deselectItem(String path)
   {
      treeGrid.deselectItem(path);
   }

}
