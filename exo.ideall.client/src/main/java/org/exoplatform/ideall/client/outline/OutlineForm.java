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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.ideall.client.component.ItemTreeGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineForm extends Tab implements OutlinePresenter.Display
{
   
   private HandlerManager eventBus;
   
   private ApplicationContext context;
   
   private OutlinePresenter presenter;
   
   private ItemTreeGrid<Item> treeGrid;
   
   public OutlineForm(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;
      
      setTitle("Outline");
      
      treeGrid = new ItemTreeGrid<Item>();
      treeGrid.setShowHeader(false);
      treeGrid.setLeaveScrollbarGap(false);
      treeGrid.setShowOpenIcons(true);
      treeGrid.setEmptyMessage("Root folder not found!");

      treeGrid.setSelectionType(SelectionStyle.MULTIPLE);

      treeGrid.setHeight100();
      treeGrid.setWidth100();
      setPane(treeGrid);
      
      presenter = new OutlinePresenter(eventBus, context);
      presenter.bindDisplay(this);
      
   }

}
