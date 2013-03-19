/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.command;

import com.codenvy.ide.core.expressions.Expression;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter;
import com.codenvy.ide.menu.ExtendedCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Command for "PaaS/CloudFoudry/Create Application" action.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowCreateApplicationCommand implements ExtendedCommand
{
   private final CreateApplicationPresenter presenter;

   private final CloudFoundryResources resources;

   private final EventBus eventBus;

   /**
    * Create command.
    * 
    * @param presenter
    * @param resources
    */
   @Inject
   public ShowCreateApplicationCommand(CreateApplicationPresenter presenter, CloudFoundryResources resources,
      EventBus eventBus)
   {
      this.presenter = presenter;
      this.resources = resources;
      this.eventBus = eventBus;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      presenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getIcon()
   {
      return resources.createApp();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return "Create new application on cloudfoundry.com";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression inContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression canExecute()
   {
      // TODO Auto-generated method stub
      return null;
   }
}