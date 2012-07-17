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
package org.exoplatform.ide.git.client.init;

import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketExceptionMessage;
import org.exoplatform.ide.client.framework.websocket.WebSocketMessage;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for Init Repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 * 
 */
public class InitRepositoryPresenter extends GitPresenter implements InitRepositoryHandler, WebSocketMessageHandler
{
   public interface Display extends IsView
   {
      /**
       * Get's bare field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getBareValue();

      /**
       * Get's working directory field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirValue();

      /**
       * Gets initialize repository button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getInitButton();

      /**
       * Gets cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();
   }

   private Display display;

   private RequestStatusHandler statusHandler;

   /**
    * @param eventBus
    */
   public InitRepositoryPresenter()
   {
      IDE.addHandler(InitRepositoryEvent.TYPE, this);
      IDE.addHandler(WebSocketMessageEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getInitButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            initRepository();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryHandler#onInitRepository(org.exoplatform.ide.git.client.init.InitRepositoryEvent)
    */
   @Override
   public void onInitRepository(InitRepositoryEvent event)
   {
      if (makeSelectionCheck())
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);
         display.getWorkDirValue().setValue(((ItemContext)selectedItems.get(0)).getProject().getPath(), true);
      }
   }

   /**
    * Get the values of the necessary parameters for initialization of the repository.
    */
   public void initRepository()
   {
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      String projectName = ((ItemContext)selectedItems.get(0)).getProject().getName();
      boolean bare = display.getBareValue().getValue();
      try
      {
         String sessionId = null;
         WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            sessionId = ws.getSessionId();
            statusHandler = new InitRequestStatusHandler(projectName);
            statusHandler.requestInProgress(projectId);
         }
         final String webSocketSessionId = sessionId;

         GitClientService.getInstance().init(vfs.getId(), projectId, projectName, bare, webSocketSessionId,
            new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  if (webSocketSessionId == null)
                  {
                     IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
                     IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler#onWebSocketMessage(org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent)
    */
   @Override
   public void onWebSocketMessage(WebSocketMessageEvent event)
   {
      String message = event.getMessage();

      WebSocketMessage webSocketMessage =
         AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketMessage.class, message).as();
      if (!webSocketMessage.getEvent().equals("gitRepoInitialized"))
      {
         return;
      }

      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
      if (!project.getId().equals(webSocketMessage.getData().asString()))
      {
         return;
      }

      WebSocketExceptionMessage webSocketException = webSocketMessage.getException();
      if (webSocketException == null)
      {
         statusHandler.requestFinished(project.getId());
         IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
         IDE.fireEvent(new RefreshBrowserEvent(project));
         return;
      }

      String exceptionMessage = null;
      if (webSocketException.getMessage() != null && webSocketException.getMessage().length() > 0)
      {
         exceptionMessage = webSocketException.getMessage();
      }

      statusHandler.requestError(project.getId(), new Exception(exceptionMessage));
      String errorMessage = (exceptionMessage != null) ? exceptionMessage : GitExtension.MESSAGES.initFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

}
