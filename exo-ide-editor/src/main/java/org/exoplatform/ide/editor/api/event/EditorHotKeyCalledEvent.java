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

package org.exoplatform.ide.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires just after some key or mouse event have been happened in editor.
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorHotKeyCalledEvent extends GwtEvent<EditorHotKeyCalledHandler>
{

   public static final GwtEvent.Type<EditorHotKeyCalledHandler> TYPE = new GwtEvent.Type<EditorHotKeyCalledHandler>();

   private String hotKey;
   
   public EditorHotKeyCalledEvent(String hotKey)
   {
      this.hotKey = hotKey;
   }

   public String getHotKey()
   {
      return hotKey;
   }
   
   @Override
   protected void dispatch(EditorHotKeyCalledHandler handler)
   {
      handler.onEditorHotKeyCalled(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorHotKeyCalledHandler> getAssociatedType()
   {
      return TYPE;
   }

}
