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
package org.exoplatform.ide.client.outline;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.SelectionStyle;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.panel.SimpleTabPanel;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineForm extends SimpleTabPanel implements OutlinePresenter.Display
{
   private static final String OUTLINE_TREE_GRID_ID = "ideOutlineTreeGrid";
   
   private static final String ID = "ideOutlineForm";

   private HandlerManager eventBus;

   private OutlinePresenter presenter;

   private OutlineTreeGrid<Token> treeGrid;

   public OutlineForm(HandlerManager bus)
   {
      super(ID);
      eventBus = bus;

      treeGrid = new OutlineTreeGrid<Token>(OUTLINE_TREE_GRID_ID);
      treeGrid.setShowHeader(false);
      treeGrid.setLeaveScrollbarGap(false);
      treeGrid.setShowOpenIcons(true);
      treeGrid.setEmptyMessage("");

      treeGrid.setSelectionType(SelectionStyle.SINGLE);

      treeGrid.setHeight100();
      treeGrid.setWidth100();
      
      addMember(treeGrid);
      presenter = new OutlinePresenter(eventBus);
      presenter.bindDisplay(this);

   }

   public TreeGridItem<Token> getOutlineTree()
   {
      return treeGrid;
   }

   public void selectToken(Token token)
   {
      if (token != null)
      {
         treeGrid.selectToken(token);
      }
   }

   public boolean isFormVisible()
   {
      return isVisible();
   }
   
   public List<Token> getSelectedTokens()
   {
      return treeGrid.getSelectedTokens();
   }
   
   public void setFocus()
   {
      treeGrid.focus();
   }

}
