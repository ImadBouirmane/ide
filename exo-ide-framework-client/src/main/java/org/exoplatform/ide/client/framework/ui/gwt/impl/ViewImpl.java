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
package org.exoplatform.ide.client.framework.ui.gwt.impl;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewImpl extends FlowPanel implements ViewEx, ViewDisplay, HasChangeViewTitleHandler,
   HasChangeViewIconHandler, HasSetViewVisibleHandler, HasActivateViewHandler
{

   private String id;

   private String type;

   private String tiltle;

   private Image icon;

   private boolean hasCloseButton = true;

   protected int defaultWidth = 300;

   protected int defaultHeight = 200;

   private boolean canResize = true;

   private boolean activated = false;

   private List<ChangeViewTitleHandler> changeViewTitleHandlers = new ArrayList<ChangeViewTitleHandler>();

   private List<ChangeViewIconHandler> changeViewIconHandlers = new ArrayList<ChangeViewIconHandler>();

   private List<SetViewVisibleHandler> setViewVisibleHandlers = new ArrayList<SetViewVisibleHandler>();

   public ViewImpl(String id, String type, String title)
   {
      this(id, type, title, null);
   }

   public ViewImpl(String id, String type, String title, Image icon)
   {
      this(id, type, title, icon, 300, 200);
   }

   public ViewImpl(String id, String type, String title, Image icon, int defaultWidth, int defaultHeight)
   {
      this.id = id;
      this.type = type;
      this.tiltle = title;
      this.icon = icon;
      this.defaultWidth = defaultWidth;
      this.defaultHeight = defaultHeight;

      wrapper = new Wrapper(3);
      wrapper.setSize("100%", "100%");
      super.add((Widget)wrapper);

      sinkEvents(Event.ONMOUSEDOWN);
   }

   @Override
   public void onBrowserEvent(Event event)
   {
      switch (DOM.eventGetType(event))
      {
         case Event.ONMOUSEDOWN :
            if (event.getButton() != Event.BUTTON_LEFT)
            {
               return;
            }

            onMouseDown();
            break;
      }
   }

   protected void onMouseDown()
   {
      ViewHighlightManager.getInstance().selectView(this);
   }

   private Wrapper wrapper;

   @Override
   public void add(Widget w)
   {
      //super.add(w);
      wrapper.add(w);
   }

   @Override
   public ViewEx getView()
   {
      return this;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public String getTitle()
   {
      return tiltle;
   }

   @Override
   public void setTitle(String title)
   {
      this.tiltle = title;

      ChangeViewTitleEvent changeViewTitleEvent = new ChangeViewTitleEvent(getId(), title);
      for (ChangeViewTitleHandler changeViewTitleHandler : changeViewTitleHandlers)
      {
         changeViewTitleHandler.onChangeViewTitle(changeViewTitleEvent);
      }
   }

   @Override
   public Image getIcon()
   {
      return icon;
   }

   @Override
   public void setIcon(Image icon)
   {
      this.icon = icon;

      ChangeViewIconEvent changeViewIconEvent = new ChangeViewIconEvent(getId(), icon);
      for (ChangeViewIconHandler changeViewIconHandler : changeViewIconHandlers)
      {
         changeViewIconHandler.onChangeViewIcon(changeViewIconEvent);
      }
   }

   @Override
   public boolean hasCloseButton()
   {
      return hasCloseButton;
   }

   @Override
   public boolean canResize()
   {
      return canResize;
   }

   @Override
   public void activate()
   {
      if (isViewVisible())
      {
         setViewVisible();
      }

      ViewHighlightManager.getInstance().selectView(this);

      //      ActivateViewEvent event = new ActivateViewEvent(this);
      //      for (ActivateViewHandler activateViewHandler : activateViewHandlers)
      //      {
      //         activateViewHandler.onActivateView(event);
      //      }
   }

   public void setActivated(boolean activated)
   {
      this.activated = activated;
      wrapper.setHighlited(activated);
   }

   @Override
   public int getDefaultWidth()
   {
      return defaultWidth;
   }

   @Override
   public int getDefaultHeight()
   {
      return defaultHeight;
   }

   @Override
   public boolean isViewVisible()
   {
      return isVisible();
   }

   @Override
   public void setActive()
   {
      ViewHighlightManager.getInstance().selectView(this);
   }

   @Override
   public HandlerRegistration addSetViewVisibleHandler(SetViewVisibleHandler setViewVisibleHandler)
   {
      setViewVisibleHandlers.add(setViewVisibleHandler);
      return new SetViewVisibleHandlerRegistration(setViewVisibleHandler);
   }

   private class SetViewVisibleHandlerRegistration implements HandlerRegistration
   {

      private SetViewVisibleHandler handler;

      public SetViewVisibleHandlerRegistration(SetViewVisibleHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         setViewVisibleHandlers.remove(handler);
      }

   }

   @Override
   public HandlerRegistration addChangeViewIconHandler(ChangeViewIconHandler changeViewIconHandler)
   {
      changeViewIconHandlers.add(changeViewIconHandler);
      return new ChangeViewIconHandlerRegistration(changeViewIconHandler);
   }

   private class ChangeViewIconHandlerRegistration implements HandlerRegistration
   {

      private ChangeViewIconHandler handler;

      public ChangeViewIconHandlerRegistration(ChangeViewIconHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         changeViewIconHandlers.remove(handler);
      }

   }

   @Override
   public HandlerRegistration addChangeViewTitleHandler(ChangeViewTitleHandler changeViewTitleHandler)
   {
      changeViewTitleHandlers.add(changeViewTitleHandler);
      return new ChangeViewTitleHandlerRegistration(changeViewTitleHandler);
   }

   private class ChangeViewTitleHandlerRegistration implements HandlerRegistration
   {

      private ChangeViewTitleHandler handler;

      public ChangeViewTitleHandlerRegistration(ChangeViewTitleHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         changeViewTitleHandlers.remove(handler);
      }

   }

   private List<ActivateViewHandler> activateViewHandlers = new ArrayList<ActivateViewHandler>();

   @Override
   public HandlerRegistration addActivateViewHandler(ActivateViewHandler activateViewHandler)
   {
      activateViewHandlers.add(activateViewHandler);
      return new ActivateViewHandlerRegistration(activateViewHandler);
   }

   private class ActivateViewHandlerRegistration implements HandlerRegistration
   {

      private ActivateViewHandler handler;

      public ActivateViewHandlerRegistration(ActivateViewHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         activateViewHandlers.remove(handler);
      }
   }

   @Override
   public boolean setViewVisible()
   {
      SetViewVisibleEvent event = new SetViewVisibleEvent(getId());
      for (SetViewVisibleHandler setViewVisibleHandler : setViewVisibleHandlers)
      {
         setViewVisibleHandler.onSetViewVisible(event);
      }

      return false;
   }

}
