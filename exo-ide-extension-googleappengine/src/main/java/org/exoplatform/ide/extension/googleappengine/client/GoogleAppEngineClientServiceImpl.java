/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendsStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployRequestStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.model.Backend;
import org.exoplatform.ide.extension.googleappengine.client.model.Credentials;
import org.exoplatform.ide.extension.googleappengine.client.model.CronEntry;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link GoogleAppEngineClientService}.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:23:28 PM anya $
 * 
 */
public class GoogleAppEngineClientServiceImpl extends GoogleAppEngineClientService
{
   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   private final String APP_ENGINE = "/ide/appengine/";

   private final String BACKEND_CONFIGURE = APP_ENGINE + "backend/configure";

   private final String CRON_INFO = APP_ENGINE + "cron/info";

   private final String BACKEND_DELETE = APP_ENGINE + "backend/delete";

   private final String RESOURCE_LIMITS = APP_ENGINE + "resource_limits";

   private final String BACKENDS_LIST = APP_ENGINE + "backends/list";

   private final String LOGS = APP_ENGINE + "logs";

   private final String ROLLBACK = APP_ENGINE + "rollback";

   private final String BACKEND_ROLLBACK = APP_ENGINE + "backend/rollback";

   private final String BACKENDS_ROLLBACK = APP_ENGINE + "backends/rollback";

   private final String BACKEND_UPDATE = APP_ENGINE + "backend/update";

   private final String BACKENDS_UPDATE_ALL = APP_ENGINE + "backends/update_all";

   private final String BACKEND_SET_STATE = APP_ENGINE + "backend/set_state";

   private final String UPDATE = APP_ENGINE + "update";

   private final String CRON_UPDATE = APP_ENGINE + "cron/update";

   private final String DOS_UPDATE = APP_ENGINE + "dos/update";

   private final String INDEXES_UPDATE = APP_ENGINE + "indexes/update";

   private final String PAGE_SPEED_UPDATE = APP_ENGINE + "pagespeed/update";

   private final String QUEUES_UPDATE = APP_ENGINE + "queues/update";

   private final String VACUUM_INDEXES = APP_ENGINE + "vacuum_indexes";

   /**
    * @param restServiceContext REST service context
    * @param loader loader to be displayed on request
    */
   public GoogleAppEngineClientServiceImpl(String restServiceContext, Loader loader)
   {
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#configureBackend(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void configureBackend(String vfsId, String projectId, String backendName, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKEND_CONFIGURE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
         .append(backendName);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#cronInfo(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void cronInfo(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<List<CronEntry>> callback) throws RequestException
   {
      String url = restServiceContext + CRON_INFO;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#deleteBackend(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteBackend(String vfsId, String projectId, String backendName, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKEND_DELETE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
         .append(backendName);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#getResourceLimits(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getResourceLimits(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Map<String, Long>> callback) throws RequestException
   {
      String url = restServiceContext + RESOURCE_LIMITS;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#listBackends(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void listBackends(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<List<Backend>> callback) throws RequestException
   {
      String url = restServiceContext + BACKENDS_LIST;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#requestLogs(java.lang.String,
    *      java.lang.String, int, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void requestLogs(String vfsId, String projectId, int numDays, String logSeverity, String email,
      String password, GoogleAppEngineAsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url = restServiceContext + LOGS;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&num_days=")
         .append(numDays).append("&log_severity=").append(logSeverity);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
         .send(callback);

   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollback(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void rollback(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + ROLLBACK;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params, true).loader(loader)
         .data(getCredentialsData(email, password)).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollbackBackend(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void rollbackBackend(String vfsId, String projectId, String backendName, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKEND_ROLLBACK;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
         .append(backendName);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollbackAllBackends(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void rollbackAllBackends(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKENDS_ROLLBACK;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#setBackendState(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void setBackendState(String vfsId, String projectId, String backendName, String backendState, String email,
      String password, GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKEND_SET_STATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
         .append(backendName).append("&backend_state=").append(backendState);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#update(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void update(String vfsId, ProjectModel project, String bin, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(project.getId());
      if (bin != null && !bin.isEmpty())
      {
         params.append("&bin=").append(bin);
      }

      AsyncRequest.build(RequestBuilder.POST, url + params, true).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).delay(2000)
         .requestStatusHandler(new DeployRequestStatusHandler(project.getName())).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateAllBackends(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateAllBackends(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKENDS_UPDATE_ALL;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params, true).delay(2000)
         .requestStatusHandler(new UpdateBackendsStatusHandler()).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateBackend(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateBackend(String vfsId, String projectId, String backendName, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + BACKEND_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
         .append(backendName);

      AsyncRequest.build(RequestBuilder.POST, url + params, true).delay(2000)
         .requestStatusHandler(new UpdateBackendStatusHandler(backendName)).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateCron(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateCron(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + CRON_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateDos(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateDos(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + DOS_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateIndexes(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateIndexes(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + INDEXES_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updatePagespeed(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updatePagespeed(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + PAGE_SPEED_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateQueues(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void updateQueues(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + QUEUES_UPDATE;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#vacuumIndexes(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void vacuumIndexes(String vfsId, String projectId, String email, String password,
      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + VACUUM_INDEXES;

      StringBuilder params = new StringBuilder("?");
      params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).data(getCredentialsData(email, password))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * Returns credentials data to be posted.
    * 
    * @param email user's email
    * @param password user's password
    * @return {@link String} credentials data to be posted
    */
   private String getCredentialsData(String email, String password)
   {
      Credentials credentials = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.credentials().as();
      credentials.setEmail(email);
      credentials.setPassword(password);
      return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(credentials)).getPayload();
   }
}
