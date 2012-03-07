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
package org.exoplatform.ide;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.conversationstate.RestConversationState;
import org.exoplatform.ide.discovery.RepositoryDiscoveryService;
import org.exoplatform.ide.template.TemplatesRestService;
import org.exoplatform.ide.upload.LoopbackContentService;
import org.exoplatform.ide.upload.UploadServiceExceptionMapper;
import org.exoplatform.ide.utils.ExoConfigurationHelper;
import org.exoplatform.ide.vfs.server.RequestContextResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.services.jcr.RepositoryService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Jan 12, 2011 5:24:37 PM evgen $
 * 
 */
public class IDEServiceApplication extends Application
{

   private final Set<Class<?>> classes = new HashSet<Class<?>>();

   private final Set<Object> objects = new HashSet<Object>();

   public IDEServiceApplication(RepositoryService repositoryService, VirtualFileSystemRegistry vfsRegistry,
      InitParams initParams)
   {
      String entryPoint = ExoConfigurationHelper.readValueParam(initParams, "defaultEntryPoint");
      boolean discoverable = Boolean.parseBoolean(ExoConfigurationHelper.readValueParam(initParams, "discoverable"));
      String workspace = ExoConfigurationHelper.readValueParam(initParams, "workspace");
      String config = ExoConfigurationHelper.readValueParam(initParams, "config");
      String templateConfig = ExoConfigurationHelper.readValueParam(initParams, "template-config");

      objects.add(new RepositoryDiscoveryService(repositoryService, entryPoint, discoverable));
      objects.add(new UploadServiceExceptionMapper());
      objects.add(new IDEConfigurationService(vfsRegistry, entryPoint, discoverable, workspace, config));
      objects.add(new TemplatesRestService(workspace, templateConfig, vfsRegistry));

      classes.add(LoopbackContentService.class);
      classes.add(RequestContextResolver.class);
      classes.add(RestConversationState.class);
   }

   /**
    * @see javax.ws.rs.core.Application#getClasses()
    */
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   /**
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons()
   {
      return objects;
   }
}
