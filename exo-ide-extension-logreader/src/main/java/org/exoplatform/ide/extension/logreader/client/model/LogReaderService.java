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
package org.exoplatform.ide.extension.logreader.client.model;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * This service provides access to information stored in the logs created on current tenant.
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class LogReaderService
{

   private String restContext;

   private Loader loader;

   private static LogReaderService instance;

   public static LogReaderService get()
   {
      return instance;
   }

   /**
    * @param restContext
    * @param loader
    */
   public LogReaderService(String restContext, Loader loader)
   {
      super();
      this.restContext = restContext;
      this.loader = loader;
      instance = this;
   }

   /**
    * Get last log file
    * 
    * @param callback
    * @throws RequestException 
    */
   public void getLastLog(AsyncRequestCallback<LogEntry> callback) throws RequestException
   {
      String url = restContext + "/log-reader-service/last-log";
      sendRequest(url, callback);
   }

   /**
    * Get previous log file for current log file
    * 
    * @param token of current log
    * @param callback
    * @throws RequestException 
    */
   public void getPrevLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException
   {
      String url = restContext + "/log-reader-service/prev-log?token=" + token;
      sendRequest(url, callback);
   }

   /**
    * Get next log file for current log
    * 
    * @param token of current log
    * @param callback
    * @throws RequestException 
    */
   public void getNextLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException
   {
      String url = restContext + "/log-reader-service/next-log?token=" + token;
      sendRequest(url, callback);
   }

   /**
    * Update log
    * 
    * @param token of log
    * @param callback
    * @throws RequestException 
    */
   public void getLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException
   {
      String url = restContext + "/log-reader-service/log?token=" + token;
      sendRequest(url, callback);
   }

   private void sendRequest(String url, AsyncRequestCallback<LogEntry> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

}