/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.navigation;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.browser.event.SelectBrowserPanelEvent;
import org.exoplatform.ideall.client.browser.event.SelectBrowserPanelHandler;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.data.event.SearchResultReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.SearchResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class NavigationPresenter implements SearchResultReceivedHandler, SelectBrowserPanelHandler
{

   interface Display
   {

      void showSearchResult(Folder searchResult);
      
      void selectBrowserPanel();

   }

   protected Display display;

   private HandlerManager eventBus;
   
   private Handlers handlers;

   public NavigationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;
      handlers.addHandler(SearchResultReceivedEvent.TYPE, this);
      handlers.addHandler(SelectBrowserPanelEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void onSearchResultReceived(SearchResultReceivedEvent event)
   {
      Folder searchResult = event.getFolder();
      display.showSearchResult(searchResult);
   }

   public void onSelectBrowserPanel(SelectBrowserPanelEvent event)
   {
      display.selectBrowserPanel();
   }

}
