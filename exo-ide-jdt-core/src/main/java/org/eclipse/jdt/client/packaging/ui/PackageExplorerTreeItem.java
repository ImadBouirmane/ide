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
package org.eclipse.jdt.client.packaging.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public abstract class PackageExplorerTreeItem extends TreeItem
{

   private static final String PREFIX_ID = "ide.package_explorer.item.";

   public PackageExplorerTreeItem(Item item)
   {
      setUserObject(item);
      render();
   }

   /**
    * Render tree item.
    */
   protected void render()
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      TreeIcon treeNodeIcon = new TreeIcon(getItemIcon());
      //treeNodeIcon.setWidth("16px");
      treeNodeIcon.setHeight("16px");
      grid.setWidget(0, 0, treeNodeIcon);
      // Label l = new Label(text, false);
      HTMLPanel l = new HTMLPanel("div", getItemTitle());
      l.setStyleName("ide-Tree-label");
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      setWidget(grid);

      Item item = (Item)getUserObject();
      getElement().setId(PREFIX_ID + Utils.md5(item.getPath()));

      if (!getState() && getItems() != null && !getItems().isEmpty())
      {
         if (getChildCount() == 0)
         {
            addItem("");
         }
      }
      else if (!getState() && getItems() != null && getItems().isEmpty())
      {
         removeItems();
      }
   }

   /**
    * Get item icon.
    * 
    * @return
    */
   protected abstract ImageResource getItemIcon();

   /**
    * Get item title.
    * 
    * @return
    */
   protected abstract String getItemTitle();

   /**
    * Get child by Item ID.
    * 
    * @param id
    * @return
    */
   public PackageExplorerTreeItem getChildByItemId(String id)
   {
      for (int i = 0; i < getChildCount(); i++)
      {
         TreeItem child = getChild(i);
         if (child instanceof PackageExplorerTreeItem)
         {
            PackageExplorerTreeItem treeItem = (PackageExplorerTreeItem)child;
            if (((Item)treeItem.getUserObject()).getId().equals(id))
            {
               return treeItem;
            }
         }
      }

      return null;
   }

   public abstract List<Item> getItems();

   public abstract void refresh(boolean expand);

   public boolean select(Item item)
   {
      if (item.getId().equals(((Item)getUserObject()).getId()))
      {
         getTree().setSelectedItem(this);
         getTree().ensureSelectedItemVisible();
         return true;
      }

      for (int i = 0; i < getChildCount(); i++)
      {
         TreeItem child = getChild(i);
         if (child instanceof PackageExplorerTreeItem)
         {
            String path = ((Item)child.getUserObject()).getPath();
            if (child instanceof PackageExplorerTreeItem)
            {
               if (item.getPath().startsWith(path))
               {
                  ((PackageExplorerTreeItem)child).refresh(true);
                  return ((PackageExplorerTreeItem)child).select(item);
               }
            }
         }
      }

      return false;
   }

   protected void removeNonexistendTreeItems()
   {
      /*
       * Remove nonexistent
       */
      List<String> idList = new ArrayList<String>();
      List<Item> items = getItems();
      for (Item item : items)
      {
         idList.add(item.getId());
      }

      ArrayList<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
      for (int i = 0; i < getChildCount(); i++)
      {
         TreeItem child = getChild(i);
         if (!(child instanceof PackageExplorerTreeItem))
         {
            itemsToRemove.add(child);
            continue;
         }

         PackageExplorerTreeItem childTreeItem = (PackageExplorerTreeItem)child;
         Item childItem = (Item)childTreeItem.getUserObject();
         if (!idList.contains(childItem.getId()))
         {
            itemsToRemove.add(child);
         }
      }

      for (TreeItem child : itemsToRemove)
      {
         removeItem(child);
      }
   }

   /**
    * Comparator for comparing items in received directory.
    */
   protected Comparator<Item> COMPARATOR = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof FolderModel && item2 instanceof FileModel)
         {
            return -1;
         }
         else if (item1 instanceof FileModel && item2 instanceof FolderModel)
         {
            return 1;
         }
         return item1.getName().compareTo(item2.getName());
      }
   };
   
   public void setIcons()
   {
      
   }

}
