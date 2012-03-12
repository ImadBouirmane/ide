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
//package org.exoplatform.ide.client.versioning.event;
//
//import org.exoplatform.ide.client.framework.vfs.Version;
//
//import java.util.List;
//
//import com.google.gwt.event.shared.GwtEvent;
//
///**
// * Event is fired, when user asks to open view of versions in separate panel.
// * 
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id: Sep 27, 2010 $
// *
// */
//public class OpenVersionEvent extends GwtEvent<OpenVersionHandler>
//{
//
//   public static final GwtEvent.Type<OpenVersionHandler> TYPE = new GwtEvent.Type<OpenVersionHandler>();
//
//   private boolean showVersionHistory = false;
//   
//   private Version version;
//   
//   private List<Version> versionHistory;
//
//   public OpenVersionEvent(boolean showVersionHistory)
//   {
//      this.showVersionHistory = showVersionHistory;
//   }
//
//   public OpenVersionEvent(Version version, List<Version> versionHistory)
//   {
//      this.version = version;
//      this.versionHistory = versionHistory;
//   }
//   
//   /**
//    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
//    */
//   @Override
//   public com.google.gwt.event.shared.GwtEvent.Type<OpenVersionHandler> getAssociatedType()
//   {
//      return TYPE;
//   }
//
//   /**
//    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
//    */
//   @Override
//   protected void dispatch(OpenVersionHandler handler)
//   {
//      handler.onOpenVersion(this);
//   }
//
//   public boolean isShowVersionHistory()
//   {
//      return showVersionHistory;
//   }
//
//   public Version getVersion()
//   {
//      return version;
//   }
//
//   public List<Version> getVersionHistory()
//   {
//      return versionHistory;
//   }
//   
// }