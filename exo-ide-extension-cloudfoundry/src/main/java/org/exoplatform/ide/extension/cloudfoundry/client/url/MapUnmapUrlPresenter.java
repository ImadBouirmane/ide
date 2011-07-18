/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

import java.util.List;

/**
 * Presenter for map and unmap URLs to application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MapUnmapUrlPresenter.java Jul 18, 2011 9:22:02 AM vereshchaka $
 *
 */
public class MapUnmapUrlPresenter implements ItemsSelectedHandler, MapUrlHandler, UnmapUrlHandler
{

   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   private String mapUrl;
   
   private String unmapUrl;
   
   public MapUnmapUrlPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(MapUrlEvent.TYPE, this);
      eventBus.addHandler(UnmapUrlEvent.TYPE, this);
   }
   
   public void bindDisplay(List<Framework> frameworks)
   {
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   
   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler mapUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         mapUrl(mapUrl);
      }
   };
   
   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationHandler#onStartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent)
    */
   @Override
   public void onMapUrl(MapUrlEvent event)
   {
      askForUrlToMap();
   }
   
   private void askForUrlToMap()
   {
      Dialogs.getInstance().askForValue(CloudFoundryExtension.LOCALIZATION_CONSTANT.mapUrlDialogTitle(), 
         CloudFoundryExtension.LOCALIZATION_CONSTANT.mapUrlDialogMessage(), "", new StringValueReceivedHandler()
      {
         @Override
         public void stringValueReceived(String value)
         {
            if (value == null)
            {
               return;
            }
            else
            {
               mapUrl = value;
               mapUrl(value);
            }
         }
      });
   }
   
   private void mapUrl(final String url)
   {
      String workDir = getWorkDir();
      
      CloudFoundryClientService.getInstance().mapUrl(workDir, null, url,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, mapUrlLoggedInHandler, null)
         {

            @Override
            protected void onSuccess(String result)
            {
               String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.mapUrlRegisteredSuccess(url);
               eventBus.fireEvent(new OutputEvent(msg));
            }
         });     
   }
   
   private String getWorkDir()
   {
      if (selectedItems.size() == 0)
         return null;
      
      String workDir = selectedItems.get(0).getHref();
      if (selectedItems.get(0) instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      return workDir;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent)
    */
   @Override
   public void onUnmapUrl(UnmapUrlEvent event)
   {
      askForUrlToUnmap();
   }
   
   private void askForUrlToUnmap()
   {
      Dialogs.getInstance().askForValue(CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapUrlDialogTitle(), 
         CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapUrlDialogMessage(), "", new StringValueReceivedHandler()
      {
         @Override
         public void stringValueReceived(String value)
         {
            if (value == null)
            {
               return;
            }
            else
            {
               unmapUrl = value;
               unmapUrl(value);
            }
         }
      });
   }
   
   private LoggedInHandler unmapUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         unmapUrl(unmapUrl);
      }
   };
   
   private void unmapUrl(final String unmapUrl)
   {
      String workDir = getWorkDir();
      
      CloudFoundryClientService.getInstance().unmapUrl(workDir, null, unmapUrl,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, unmapUrlLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapUrlUnregisteredSuccess(unmapUrl);
               eventBus.fireEvent(new OutputEvent(msg));
            }
         });
   }

}
