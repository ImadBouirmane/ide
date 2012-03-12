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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.command.ui.ToolbarBuilder;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.menu.Menu;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.menu.RefreshMenuHandler;

import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEPresenter implements RefreshMenuHandler, ViewOpenedHandler, ViewClosedHandler,
   ViewVisibilityChangedHandler, ClosingViewHandler
{

   public interface Display
   {

      Menu getMenu();

      Toolbar getToolbar();

      Toolbar getStatusbar();

      Perspective getPerspective();

   }

   private Display display;

   private ControlsRegistration controlsRegistration;

   public IDEPresenter(Display display, final ControlsRegistration controlsRegistration)
   {
      this.display = display;
      this.controlsRegistration = controlsRegistration;

      IDE.addHandler(RefreshMenuEvent.TYPE, this);

      display.getPerspective().addViewOpenedHandler(this);
      display.getPerspective().addClosingViewHandler(this);
      display.getPerspective().addViewClosedHandler(this);
      display.getPerspective().addViewVisibilityChangedHandler(this);

      new ToolbarBuilder(IDE.eventBus(), display.getToolbar(), display.getStatusbar());
      new VirtualFileSystemSwitcher();
      new CloseViewsOnEscapePressedHandler();

      new Timer()
      {
         @Override
         public void run()
         {
            // activate default view
            // eventBus.fireEvent(new ActivateViewEvent(BrowserPanel.ID));
            new IDEConfigurationInitializer(controlsRegistration).loadConfiguration();
         }
      }.schedule(500);
   }

   public void openView(View view)
   {
      display.getPerspective().openView(view);
   }

   public void closeView(String viewId)
   {
      display.getPerspective().closeView(viewId);
   }

   @Override
   public void onRefreshMenu(RefreshMenuEvent event)
   {
      display.getMenu().refresh(controlsRegistration.getRegisteredControls());
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      IDE.fireEvent(event);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      IDE.fireEvent(event);
   }

   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      IDE.fireEvent(event);
   }

   @Override
   public void onClosingView(ClosingViewEvent event)
   {
      IDE.fireEvent(event);
   }

}