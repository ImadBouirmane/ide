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
package org.exoplatform.ide.extension.samples.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.List;
import java.util.Map;


/**
 * Client service for Samples.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientService.java Sep 2, 2011 12:34:16 PM vereshchaka $
 *
 */
public abstract class SamplesClientService
{
   
   private static SamplesClientService instance;
   
   public static SamplesClientService getInstance()
   {
      return instance;
   }
   
   protected SamplesClientService()
   {
      instance = this;
   }
   
   /**
    * Get the list of available public repositories from GitHub
    * with sample applications.
    * 
    * @param callback the callback client has to implement
    */
   public abstract void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback);
   
   /************ CloudBees operations ************/
   
   /**
    * Initialize application.
    * 
    * @param appId
    * @param warFile
    * @param message
    * @param callback
    */
   public abstract void createCloudBeesApplication(String appId, String warFile, String message, String workDir,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);
   
   /**
    * Get the domains.
    * 
    * @param callback - callback that client has to implement
    */
   public abstract void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback);
   
   public abstract void loginToCloudBees(String email, String password, AsyncRequestCallback<String> callback);
   
}
