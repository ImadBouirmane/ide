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
package org.exoplatform.ideall.client.common.command.edit;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.common.command.MultipleSelectionItemsCommand;
import org.exoplatform.ideall.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ideall.client.event.edit.ItemsToPasteSelectedHandler;
import org.exoplatform.ideall.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsCompleteHandler;
import org.exoplatform.ideall.client.event.edit.PasteItemsEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommand extends MultipleSelectionItemsCommand implements ItemsToPasteSelectedHandler,
   PasteItemsCompleteHandler, ItemsSelectedHandler
{
   private static final String ID = "Edit/Paste Item(s)";

   private boolean pastePrepared = false;

   public PasteItemsCommand()
   {
      super(ID);
      setTitle("Paste Item(s)");
      setPrompt("Paste Selected Item(s)");
      setIcon(Images.Edit.PASTE_FILE);
      setEvent(new PasteItemsEvent());
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      setEnabled(false);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsToPasteSelectedEvent.TYPE, this);
      addHandler(PasteItemsCompleteEvent.TYPE, this);
      addHandler(ItemsSelectedEvent.TYPE, this);
      super.onRegisterHandlers();
   }

   public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event)
   {
      pastePrepared = true;
      setEnabled(true);
   }

   public void onPasteItemsComlete(PasteItemsCompleteEvent event)
   {
      setEnabled(false);
      pastePrepared = false;
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }
      
      if (pastePrepared)
      {
         setEnabled(true);
      }
   }
   
   @Override
   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId())) {
         browserSelected = true;
         if(pastePrepared)
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }
      }
      else
      {
         browserSelected = false;
         setEnabled(false);
      }
   }

}
