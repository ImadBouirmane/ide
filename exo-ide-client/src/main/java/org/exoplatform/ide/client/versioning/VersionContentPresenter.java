//TODO: need rework according new VFS
///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client.versioning;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.exoplatform.ide.client.IDE;
//import org.exoplatform.ide.client.framework.ui.api.IsView;
//import org.exoplatform.ide.client.framework.vfs.Version;
//import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
//import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;
//
//import com.google.gwt.event.shared.GwtEvent;
//import com.google.gwt.event.shared.HandlerRegistration;
//
///**
// * 
// * Created by The eXo Platform SAS .
// * 
// * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
// * @version $
// */
//
//public class VersionContentPresenter implements ShowVersionContentHandler
//{
//
//   public interface Display extends IsView
//   {
//
//      String getEditorId();
//
//      void setVersionContent(String content);
//
//      /**
//       * @param title
//       */
//      void setTitle(String title);
//
//   }
//   
//   private static final String VERSIONS_PREFIX = IDE.VERSIONS_CONSTANT.versionsDisplayPrefix();
//
//   private Display display;
//
//   /**
//    * Used to remove handlers when they are no longer needed.
//    */
//   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
//      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();
//
//   private Version version;
//
//   public VersionContentPresenter()
//   {
//      handlerRegistrations.put(ShowVersionContentEvent.TYPE, IDE.addHandler(ShowVersionContentEvent.TYPE, this));
//   }
//
//   public void bindDisplay(Display d)
//   {
//      display = d;
//   }
//
//   /**
//    * Remove handlers, that are no longer needed.
//    */
//   public void destroy()
//   {
//      //TODO: such method is not very convenient.
//      //If gwt mvp framework will be used , it will be good to use
//      //ResettableEventBus class
//      for (HandlerRegistration h : handlerRegistrations.values())
//      {
//         h.removeHandler();
//      }
//      handlerRegistrations.clear();
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler#onShowVersionContent(org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent)
//    */
//   public void onShowVersionContent(ShowVersionContentEvent event)
//   {
//      version = event.getVersion();
//      display.setTitle(getTitle());
//      display.setVersionContent(event.getVersion().getContent());
//   }
//
//   private String getTitle()
//   {
//      String hint = "title=\"" + version.getHref() + "\"";
//      String title = "<span " + hint + ">" + VERSIONS_PREFIX + " " + version.getDisplayName() + "</span>";
//      return title;
//   }
// }