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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketEventHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.EvaluateExpressionEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeHandler;
import org.exoplatform.ide.extension.java.jdi.client.ui.DebuggerView;
import org.exoplatform.ide.extension.java.jdi.client.ui.RunDebuggerView;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.StepEvent;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerPresenter implements DebuggerConnectedHandler, DebuggerDisconnectedHandler, ViewClosedHandler,
   BreakPointsUpdatedHandler, RunAppHandler, DebugAppHandler, ProjectBuiltHandler, StopAppHandler, AppStopedHandler,
   ProjectClosedHandler, ProjectOpenedHandler, EditorActiveFileChangedHandler, UpdateVariableValueInTreeHandler
{

   private Display display;

   private DebuggerInfo debuggerInfo;

   private CurrentEditorBreakPoint currentBreakPoint;

   private ApplicationInstance runningApp;

   private BreakpointsManager breakpointsManager;

   private boolean startDebugger;

   private FileModel activeFile;

   private ProjectModel project;

   private RunningAppStatusHandler runStatusHandler;

   private long DEFAULT_APPLICATION_PROLONG_TIME = 10 * 60 * 1000; // 10 minutes

   public interface Display extends IsView
   {

      HasClickHandlers getResumeButton();

      HasClickHandlers getRemoveAllBreakpointsButton();

      HasClickHandlers getDisconnectButton();

      HasClickHandlers getStepIntoButton();

      HasClickHandlers getStepOverButton();

      HasClickHandlers getStepReturnButton();

      HasClickHandlers getChangeValueButton();

      HasClickHandlers getEvaluateExpressionButton();

      Variable getSelectedVariable();

      List<Variable> getVariables();

      void setBreakPoints(List<BreakPoint> breakPoints);

      void setVariables(List<Variable> variables);

      void setEnableResumeButton(boolean isEnable);

      void setRemoveAllBreakpointsButton(boolean isEnable);

      void setDisconnectButton(boolean isEnable);

      void setStepIntoButton(boolean isEnable);

      void setStepOverButton(boolean isEnable);

      void setStepReturnButton(boolean isEnable);

      void setChangeValueButtonEnable(boolean isEnable);

      void setEvaluateExpressionButtonEnable(boolean isEnable);
   }

   public DebuggerPresenter(BreakpointsManager breakpointsManager)
   {
      this.breakpointsManager = breakpointsManager;
   }

   void bindDisplay(Display d)
   {
      this.display = d;

      display.getResumeButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doResume();
         }

      });

      display.getStepIntoButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doStepInto();
         }
      });

      display.getStepOverButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doStepOver();
         }
      });

      display.getStepReturnButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doStepReturn();
         }
      });

      display.getDisconnectButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doDisconnectDebugger();
            doStopApp();
         }
      });

      display.getRemoveAllBreakpointsButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doRemoveAllBreakPoints();
         }
      });

      display.getChangeValueButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ChangeValueEvent(debuggerInfo, display.getSelectedVariable()));
         }
      });

      display.getEvaluateExpressionButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new EvaluateExpressionEvent(debuggerInfo));
         }
      });

      disableButtons();
   }

   private void doResume()
   {
      disableButtons();
      try
      {
         DebuggerClientService.getInstance().resume(debuggerInfo.getId(), new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               resetStates();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }

         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void doStepInto()
   {
      disableButtons();
      try
      {
         DebuggerClientService.getInstance().stepInto(debuggerInfo.getId(), new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               resetStates();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }

         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void doStepOver()
   {
      disableButtons();
      try
      {
         DebuggerClientService.getInstance().stepOver(debuggerInfo.getId(), new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               resetStates();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }

         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void doStepReturn()
   {
      disableButtons();
      try
      {
         DebuggerClientService.getInstance().stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               resetStates();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }

         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void doDisconnectDebugger()
   {
      if (debuggerInfo != null)
      {
         try
         {
            DebuggerClientService.getInstance().disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  checkDebugEventsTimer.cancel();
                  disableButtons();
                  debuggerInfo = null;
                  breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
                  currentBreakPoint = null;
                  IDE.eventBus().fireEvent(new DebuggerDisconnectedEvent());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });

         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   }

   private void doGetDump()
   {
      AutoBean<StackFrameDump> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(StackFrameDump.class);
      AutoBeanUnmarshaller<StackFrameDump> unmarshaller = new AutoBeanUnmarshaller<StackFrameDump>(autoBean);
      try
      {
         DebuggerClientService.getInstance().dump(debuggerInfo.getId(),
            new AsyncRequestCallback<StackFrameDump>(unmarshaller)
            {

               @Override
               protected void onSuccess(StackFrameDump result)
               {
                  List<Variable> variables = new ArrayList<Variable>(result.getFields());
                  if (result.getLocalVariables() != null)
                     variables.addAll(result.getLocalVariables());
                  display.setVariables(variables);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public void onDebuggerConnected(DebuggerConnectedEvent event)
   {
      if (display == null)
      {
         debuggerInfo = event.getDebuggerInfo();
         display = new DebuggerView(debuggerInfo);
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());

         WebSocket ws = WebSocket.getInstance();
         if (ws == null || WebSocket.ReadyState.OPEN != ws.getReadyState())
         {
            checkDebugEventsTimer.scheduleRepeating(3000);
         }
      }
   }

   private void disableButtons()
   {
      display.setEnableResumeButton(false);
      display.setStepIntoButton(false);
      display.setStepOverButton(false);
      display.setStepReturnButton(false);
      display.setEvaluateExpressionButtonEnable(false);
   }

   private void enabelButtons()
   {
      display.setEnableResumeButton(true);
      display.setStepIntoButton(true);
      display.setStepOverButton(true);
      display.setStepReturnButton(true);
      display.setEvaluateExpressionButtonEnable(true);
   }

   /**
    * A timer for checking events
    */
   private Timer checkDebugEventsTimer = new Timer()
   {
      @Override
      public void run()
      {
         AutoBean<DebuggerEventList> debuggerEventList =
            DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerEventList.class);
         DebuggerEventListUnmarshaller unmarshaller = new DebuggerEventListUnmarshaller(debuggerEventList.as());
         try
         {
            DebuggerClientService.getInstance().checkEvents(debuggerInfo.getId(),
               new AsyncRequestCallback<DebuggerEventList>(unmarshaller)
               {
                  @Override
                  protected void onSuccess(DebuggerEventList result)
                  {
                     eventListReceived(result);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     cancel();
                     IDE.getInstance().closeView(display.asView().getId());
                     if (runningApp != null)
                     {
                        if (exception instanceof ServerException)
                        {
                           ServerException serverException = (ServerException)exception;
                           if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                              && serverException.getMessage() != null
                              && serverException.getMessage().contains("not found"))
                           {
                              IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT
                                 .debuggeDisconnected(), Type.WARNING));
                              IDE.fireEvent(new AppStopedEvent(runningApp.getName(), false));
                              return;
                           }
                        }
                        IDE.fireEvent(new ExceptionThrownEvent(exception));
                     }
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   /**
    * Performs actions when event list was received.
    * 
    * @param eventList debugger event list
    */
   private void eventListReceived(DebuggerEventList eventList)
   {
      String filePath = null;
      if (eventList != null && eventList.getEvents().size() > 0)
      {
         Location location;
         for (DebuggerEvent event : eventList.getEvents())
         {
            if (event instanceof StepEvent)
            {
               StepEvent stepEvent = (StepEvent)event;
               location = stepEvent.getLocation();
               filePath = resolveFilePath(location);
               if (!filePath.equalsIgnoreCase(activeFile.getPath()))
                  openFile(location);
               currentBreakPoint = new CurrentEditorBreakPoint(location.getLineNumber(), "BreakPoint", filePath);
            }
            else if (event instanceof BreakPointEvent)
            {
               BreakPointEvent breakPointEvent = (BreakPointEvent)event;
               location = breakPointEvent.getBreakPoint().getLocation();
               filePath = resolveFilePath(location);
               if (!filePath.equalsIgnoreCase(activeFile.getPath()))
                  openFile(location);
               currentBreakPoint = new CurrentEditorBreakPoint(location.getLineNumber(), "BreakPoint", filePath);
            }
            doGetDump();
            enabelButtons();
         }
         if (filePath != null && filePath.equalsIgnoreCase(activeFile.getPath()))
            breakpointsManager.markCurrentBreakPoint(currentBreakPoint);
      }
   }

   private void openFile(final Location location)
   {
      FileModel fileModel = breakpointsManager.getFileWithBreakPoints().get(location.getClassName());
      if (fileModel == null)
      {
         String path = resolveFilePath(location);
         try
         {
            VirtualFileSystem.getInstance().getItemByPath(path,
               new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FileModel())))
               {

                  @Override
                  protected void onSuccess(ItemWrapper result)
                  {
                     IDE.eventBus().fireEvent(
                        new OpenFileEvent((FileModel)result.getItem(), new CursorPosition(location.getLineNumber())));
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     Dialogs.getInstance().showInfo("Source not found",
                        "Can't load source of the " + location.getClassName() + " class.");
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }

      }
      else
      {
         IDE.eventBus().fireEvent(new OpenFileEvent(fileModel, new CursorPosition(location.getLineNumber())));
      }
   }

   private String resolveFilePath(final Location location)
   {
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : "src/main/java";
      String path = project.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";
      return path;
   }

   public void reLaunchDebugger(ApplicationInstance debugApplicationInstance)
   {
      ReLaunchDebuggerPresenter runDebuggerPresenter =
         new ReLaunchDebuggerPresenter(debugApplicationInstance, debuggerEventHandler);
      RunDebuggerView view = new RunDebuggerView();
      runDebuggerPresenter.bindDisplay(view);
      IDE.getInstance().openView(view.asView());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onDebuggerDisconnected(DebuggerDisconnectedEvent event)
   {
      IDE.getInstance().closeView(display.asView().getId());
      WebSocket.getInstance().messageBus().unsubscribe(Channels.DEBUGGER_EVENT.toString(), debuggerEventHandler);
   }

   @Override
   public void onBreakPointsUpdated(BreakPointsUpdatedEvent event)
   {
      if (event.getBreakPoints() != null)
      {
         List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
         Collection<Set<EditorBreakPoint>> values = event.getBreakPoints().values();
         for (Set<EditorBreakPoint> ebps : values)
         {
            for (EditorBreakPoint editorBreakPoint : ebps)
            {
               breakPoints.add(editorBreakPoint.getBreakPoint());
            }
         }
         display.setBreakPoints(breakPoints);
      }
   }

   @Override
   public void onRunApp(RunAppEvent event)
   {
      if (!IDE.eventBus().isEventHandled(ProjectBuiltEvent.TYPE))
         IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      startDebugger = false;
      IDE.fireEvent(new BuildProjectEvent());
   }

   @Override
   public void onDebugApp(DebugAppEvent event)
   {
      if (!IDE.eventBus().isEventHandled(ProjectBuiltEvent.TYPE))
         IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      startDebugger = true;
      IDE.fireEvent(new BuildProjectEvent());
   }

   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      BuildStatus buildStatus = event.getBuildStatus();
      if (buildStatus.getStatus().equals(BuildStatus.Status.SUCCESSFUL))
      {
         IDE.eventBus().fireEvent(
            new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarting(), Type.INFO));
         startApplication(buildStatus.getDownloadUrl());
      }
   }

   private void debugApplication(String warUrl)
   {
      AutoBean<ApplicationInstance> debugApplicationInstance =
         DebuggerExtension.AUTO_BEAN_FACTORY.debugApplicationInstance();

      AutoBeanUnmarshaller<ApplicationInstance> unmarshaller =
         new AutoBeanUnmarshaller<ApplicationInstance>(debugApplicationInstance);
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            runStatusHandler = new RunningAppStatusHandler(project.getName());
            runStatusHandler.requestInProgress(project.getId());
            ws.messageBus().subscribe(Channels.DEBUGGER_STARTED, debugStartedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         ApplicationRunnerClientService.getInstance().debugApplication(project.getName(), warUrl, useWebSocket,
            new AsyncRequestCallback<ApplicationInstance>(unmarshaller)
            {
               @Override
               protected void onSuccess(ApplicationInstance result)
               {
                  if (!useWebSocket)
                  {
                     onDebugStarted(result);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed()
                     + " : " + exception.getMessage(), OutputMessage.Type.ERROR));
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.DEBUGGER_STARTED, debugStartedHandler);
                     runStatusHandler.requestError(project.getId(), exception);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
      catch (WebSocketException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
   }

   protected void doRunDebugger(final ApplicationInstance debugApplicationInstance)
   {
      AutoBean<DebuggerInfo> debuggerInfo = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerInfo.class);
      AutoBeanUnmarshaller<DebuggerInfo> unmarshaller = new AutoBeanUnmarshaller<DebuggerInfo>(debuggerInfo);
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = null;//WebSocket.getInstance(); TODO: temporary disable web-sockets
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            ws.messageBus().subscribe(Channels.DEBUGGER_EVENT, debuggerEventHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         DebuggerClientService.getInstance().create(debugApplicationInstance.getDebugHost(),
            debugApplicationInstance.getDebugPort(), useWebSocket, new AsyncRequestCallback<DebuggerInfo>(unmarshaller)
            {
               @Override
               public void onSuccess(DebuggerInfo result)
               {
                  IDE.eventBus().fireEvent(new DebuggerConnectedEvent(result));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  reLaunchDebugger(debugApplicationInstance);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.DEBUGGER_EVENT, debuggerEventHandler);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
      catch (WebSocketException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void runApplication(String warUrl)
   {
      AutoBean<ApplicationInstance> applicationInstance = DebuggerExtension.AUTO_BEAN_FACTORY.applicationInstance();
      AutoBeanUnmarshaller<ApplicationInstance> unmarshaller =
         new AutoBeanUnmarshaller<ApplicationInstance>(applicationInstance);
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = null;//WebSocket.getInstance(); TODO: temporary disable web-sockets
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            runStatusHandler = new RunningAppStatusHandler(project.getName());
            runStatusHandler.requestInProgress(project.getId());
            ws.messageBus().subscribe(Channels.APP_STARTED, appStartedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         ApplicationRunnerClientService.getInstance().runApplication(project.getName(), warUrl, useWebSocket,
            new AsyncRequestCallback<ApplicationInstance>(unmarshaller)
            {
               @Override
               protected void onSuccess(ApplicationInstance result)
               {
                  if (!useWebSocket)
                  {
                     onApplicationStarted(result);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.APP_STARTED, appStartedHandler);
                     runStatusHandler.requestError(project.getId(), exception);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
      catch (WebSocketException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
   }

   private void onApplicationStarted(ApplicationInstance app)
   {
      String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(app.getName());
      msg +=
         "<br>"
            + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
      IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
      IDE.fireEvent(new AppStartedEvent(app));
      runningApp = app;
   }

   private void onDebugStarted(ApplicationInstance result)
   {
      String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(result.getName());
      msg +=
         "<br>"
            + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(),
               getAppUrlsAsString(result));
      IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
      doRunDebugger(result);
      IDE.fireEvent(new AppStartedEvent(result));
      runningApp = result;
   }

   private void handleError(Throwable exception)
   {
      exception.printStackTrace();
      IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed() + " : "
         + exception.getMessage(), OutputMessage.Type.ERROR));
   }

   private String getAppUrlsAsString(ApplicationInstance application)
   {
      String appUris = "";
      UrlBuilder builder = new UrlBuilder();
      String uri = builder.setProtocol("http").setHost(application.getHost()).buildString();
      appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
      return appUris;
   }

   @Override
   public void onStopApp(StopAppEvent event)
   {
      doDisconnectDebugger();
      doStopApp();
   }

   private void doStopApp()
   {
      if (runningApp != null)
      {
         try
         {
            DebuggerClientService.getInstance().stopApplication(runningApp, new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new AppStopedEvent(runningApp.getName(), true));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String message =
                     (exception.getMessage() != null) ? exception.getMessage()
                        : DebuggerExtension.LOCALIZATION_CONSTANT.stopApplicationFailed();
                  IDE.fireEvent(new OutputEvent(message, OutputMessage.Type.WARNING));

                  if (exception instanceof ServerException)
                  {
                     ServerException serverException = (ServerException)exception;
                     if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                        && serverException.getMessage() != null && serverException.getMessage().contains("not found"))
                     {
                        IDE.fireEvent(new AppStopedEvent(runningApp.getName(), false));
                     }
                  }
               }
            });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   }

   @Override
   public void onAppStoped(AppStopedEvent appStopedEvent)
   {
      if (appStopedEvent.isManually())
      {
         String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStoped(appStopedEvent.getAppName());
         IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
      }
      runningApp = null;
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      project = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      if (activeFile == null)
      {
         return;
      }

      String path = event.getFile().getPath();
      if (currentBreakPoint != null && currentBreakPoint.getFilePath().equals(path))
      {
         breakpointsManager.markCurrentBreakPoint(currentBreakPoint);
      }
   }

   private void resetStates()
   {
      display.setVariables(Collections.<Variable> emptyList());
      breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
      currentBreakPoint = null;
   }

   private void doRemoveAllBreakPoints()
   {
      try
      {
         DebuggerClientService.getInstance().deleteAllBreakPoint(debuggerInfo.getId(),
            new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new BreakPointsUpdatedEvent(Collections.<String, Set<EditorBreakPoint>> emptyMap()));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }

            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeHandler#onUpdateVariableValueInTree(org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent)
    */
   @Override
   public void onUpdateVariableValueInTree(UpdateVariableValueInTreeEvent event)
   {
      Variable variable = event.getVariable();
      String value = event.getValue();

      List<Variable> list = display.getVariables();
      variable.setValue(value);
      int index = list.lastIndexOf(variable);
      list.set(index, variable);
      display.setVariables(list);
   }

   private void startApplication(final String url)
   {
      if (IDE.eventBus().isEventHandled(ProjectBuiltEvent.TYPE))
         IDE.eventBus().removeHandler(ProjectBuiltEvent.TYPE, this);
      if (startDebugger)
         debugApplication(url);
      else
         runApplication(url);
   }

   /**
    * Prolong the expiration time of the application.
    */
   private void prolongExpirationTime()
   {
      try
      {
         ApplicationRunnerClientService.getInstance().prolongExpirationTime(runningApp.getName(),
            DEFAULT_APPLICATION_PROLONG_TIME, new AsyncRequestCallback<Object>()
            {
               @Override
               protected void onSuccess(Object result)
               {
                  // nothing to do
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  Dialogs.getInstance()
                     .showError(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeFailed());
               }
            });
      }
      catch (RequestException e)
      {
         Dialogs.getInstance().showError(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeFailed());
      }
   }

   /**
    * Performs actions after the debugger was started.
    */
   private WebSocketEventHandler debugStartedHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage message)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.DEBUGGER_STARTED.toString(), this);

         AutoBean<ApplicationInstance> debugApp =
            AutoBeanCodex.decode(DebuggerExtension.AUTO_BEAN_FACTORY, ApplicationInstance.class,
               message.getPayload());
         onDebugStarted(debugApp.as());
         runStatusHandler.requestFinished(project.getId());

         try
         {
            WebSocket.getInstance().messageBus().subscribe(Channels.DEBUGGER_EXPIRE_SOON_APPS, debugExpireAppsHandler);
         }
         catch (WebSocketException e)
         {
            // do nothing
         }
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.DEBUGGER_STARTED.toString(), this);

         exception.printStackTrace();
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed() + " : "
            + exception.getMessage(), OutputMessage.Type.ERROR));
         runStatusHandler.requestError(project.getId(), exception);
      }
   };

   /**
    * Performs actions after the debugger event was received.
    */
   private WebSocketEventHandler debuggerEventHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage event)
      {
         DebuggerEventList debuggerEventList = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerEventList.class).as();
         parseDebuggerEvents(debuggerEventList, event.getPayload().getPayload());
         eventListReceived(debuggerEventList);
      }

      @Override
      public void onError(Exception exception)
      {
         IDE.getInstance().closeView(display.asView().getId());
         if (runningApp != null)
         {
            if (exception.getMessage() != null && exception.getMessage().contains("not found"))
            {
               IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.debuggeDisconnected(),
                  Type.WARNING));
               IDE.fireEvent(new AppStopedEvent(runningApp.getName(), false));
            }
         }
      }
   };

   /**
    * Performs actions after the application was started.
    */
   private WebSocketEventHandler appStartedHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage message)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.APP_STARTED, this);

         AutoBean<ApplicationInstance> app =
            AutoBeanCodex.decode(DebuggerExtension.AUTO_BEAN_FACTORY, ApplicationInstance.class, message.getPayload());
         onApplicationStarted(app.as());
         runStatusHandler.requestFinished(project.getId());
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.APP_STARTED, this);

         handleError(exception);
         runStatusHandler.requestError(project.getId(), exception);
      }
   };

   private WebSocketEventHandler debugExpireAppsHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage message)
      {
         String[] apps = new StringArrayUnmarshaller(message.getPayload().getPayload()).unmarshal();
         for (String appName : apps)
         {
            if (runningApp.getName().equals(appName))
            {
               Dialogs.getInstance().ask(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeTitle(),
                  DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeQuestion(),
                  new BooleanValueReceivedHandler()
                  {
                     @Override
                     public void booleanValueReceived(Boolean value)
                     {
                        if (value == true)
                        {
                           prolongExpirationTime();
                        }
                        else
                        {
                           WebSocket.getInstance().messageBus().unsubscribe(Channels.DEBUGGER_EXPIRE_SOON_APPS, debugExpireAppsHandler);
                        }
                     }
                  });
               return;
            }
         }
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.DEBUGGER_EXPIRE_SOON_APPS, this);
      }
   };

   /**
    * Deserializes data in JSON format to {@link DebuggerEventList} object.
    * 
    * @param debuggerEventList {@link DebuggerEventList} object
    * @param jsonData data in JSON format
    */
   private void parseDebuggerEvents(DebuggerEventList debuggerEventList, String jsonData)
   {
      JSONObject jObj = JSONParser.parseStrict(jsonData).isObject();
      if (jObj == null)
      {
         return;
      }

      List<DebuggerEvent> eventList = new ArrayList<DebuggerEvent>();
      debuggerEventList.setEvents(eventList);

      if (jObj.containsKey("events"))
      {
         JSONArray jEvent = jObj.get("events").isArray();
         for (int i = 0; i < jEvent.size(); i++)
         {
            JSONObject je = jEvent.get(i).isObject();
            if (je.containsKey("type"))
            {
               int type = (int)je.get("type").isNumber().doubleValue();
               if (type == DebuggerEvent.BREAKPOINT)
               {
                  AutoBean<BreakPointEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoinEvent();
                  Splittable data = StringQuoter.split(je.toString());
                  AutoBeanCodex.decodeInto(data, bean);
                  eventList.add(bean.as());
               }
               else if (type == DebuggerEvent.STEP)
               {
                  AutoBean<StepEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.stepEvent();
                  Splittable data = StringQuoter.split(je.toString());
                  AutoBeanCodex.decodeInto(data, bean);
                  eventList.add(bean.as());
               }
            }
         }
      }
   }

}