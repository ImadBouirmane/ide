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
package org.exoplatform.ide.client.framework.settings;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsReceivedEvent extends ServerExceptionEvent<ApplicationSettingsReceivedHandler>
{

   public static final GwtEvent.Type<ApplicationSettingsReceivedHandler> TYPE =
      new GwtEvent.Type<ApplicationSettingsReceivedHandler>();

   private ApplicationSettings applicationSettings;

   public ApplicationSettingsReceivedEvent(ApplicationSettings applicationSettings)
   {
      super(null);
      this.applicationSettings = applicationSettings;
   }

   @Override
   protected void dispatch(ApplicationSettingsReceivedHandler handler)
   {
      handler.onApplicationSettingsReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ApplicationSettingsReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public ApplicationSettings getApplicationSettings()
   {
      return applicationSettings;
   }

}