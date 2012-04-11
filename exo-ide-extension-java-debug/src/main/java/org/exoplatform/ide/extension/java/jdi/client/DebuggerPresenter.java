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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.ui.DebuggerView;
import org.exoplatform.ide.extension.java.jdi.client.ui.RunDebuggerView;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;
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
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerPresenter implements DebuggerConnectedHandler, DebuggerDisconnectedHandler, ViewClosedHandler,
   BreakPointsUpdatedHandler, RunAppHandler, DebugAppHandler, ProjectBuiltHandler, StopAppHandler, AppStopedHandler
{
   private Display display;

   private DebuggerInfo debuggerInfo;

   private CurrentEditorBreakPoint currentBreakPoint = new CurrentEditorBreakPoint();

   private ApplicationInstance runningApp;

   private BreakpointsManager breakpointsManager;

   private String warUrl;

   public interface Display extends IsView
   {

      HasClickHandlers getResumeButton();

      HasClickHandlers getRemoveAllBreakpointsButton();

      HasClickHandlers getDisconnectButton();

      HasClickHandlers getStepIntoButton();

      HasClickHandlers getStepOverButton();

      HasClickHandlers getStepReturnButton();

      void setBreakPoints(List<BreakPoint> breakPoints);

      void setVariebels(List<Variable> variables);

      void setEnabelResumeButton(boolean isEnabel);

      void setRemoveAllBreakpointsButton(boolean isEnabel);

      void setDisconnectButton(boolean isEnabel);

      void setStepIntoButton(boolean isEnabel);

      void setStepOverButton(boolean isEnabel);

      void setStepReturnButton(boolean isEnabel);

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
            disabelButtons();
            try
            {
               DebuggerClientService.getInstance().resume(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     display.setVariebels(Collections.<Variable> emptyList());
                     breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
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

      });

      display.getStepIntoButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            disabelButtons();
            try
            {
               DebuggerClientService.getInstance().stepInto(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     display.setVariebels(Collections.<Variable> emptyList());
                     breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
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
      });

      display.getStepOverButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            disabelButtons();
            try
            {
               DebuggerClientService.getInstance().stepOver(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     display.setVariebels(Collections.<Variable> emptyList());
                     breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
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
      });

      display.getStepReturnButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            disabelButtons();
            try
            {
               DebuggerClientService.getInstance().stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     display.setVariebels(Collections.<Variable> emptyList());
                     breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
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
            try
            {
               DebuggerClientService.getInstance().deleteAllBreakPoint(debuggerInfo.getId(),
                  new AsyncRequestCallback<String>()
                  {

                     @Override
                     protected void onSuccess(String result)
                     {
                        IDE.fireEvent(new BreakPointsUpdatedEvent(Collections
                           .<String, Set<EditorBreakPoint>> emptyMap()));
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
      });

      disabelButtons();
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
                  disabelButtons();
                  IDE.eventBus().fireEvent(new DebuggerDisconnectedEvent());
                  debuggerInfo = null;
                  checkDebugEventsTimer.cancel();
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
                  variables.addAll(result.getLocalVariables());
                  display.setVariebels(variables);
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
         checkDebugEventsTimer.scheduleRepeating(3000);
         breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
      }
   }

   private void disabelButtons()
   {
      display.setEnabelResumeButton(false);
      display.setStepIntoButton(false);
      display.setStepOverButton(false);
      display.setStepReturnButton(false);
   }

   private void enabelButtons()
   {
      display.setEnabelResumeButton(true);
      display.setStepIntoButton(true);
      display.setStepOverButton(true);
      display.setStepReturnButton(true);
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
                     if (result != null && result.getEvents().size() > 0)
                     {
                        for (DebuggerEvent event : result.getEvents())
                        {
                           if (event instanceof StepEvent)
                           {
                              StepEvent stepEvent = (StepEvent)event;
                              openFile(stepEvent.getLocation());
                              currentBreakPoint.setLine(stepEvent.getLocation().getLineNumber());
                              currentBreakPoint.setMessage("BreakPoint");
                           }
                           else if (event instanceof BreakPointEvent)
                           {
                              BreakPointEvent breakPointEvent = (BreakPointEvent)event;
                              openFile(breakPointEvent.getBreakPoint().getLocation());
                              currentBreakPoint.setLine(breakPointEvent.getBreakPoint().getLocation().getLineNumber());
                              currentBreakPoint.setMessage("BreakPoint");
                           }
                           doGetDump();
                           enabelButtons();
                        }
                        breakpointsManager.markCurrentBreakPoint(currentBreakPoint);
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     cancel();
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   private boolean startDebugger;

   private void openFile(Location location)
   {
      FileModel fileModel = breakpointsManager.getFileWithBreakPoints().get(location.getClassName());
      IDE.eventBus().fireEvent(new OpenFileEvent(fileModel, new CursorPosition(location.getLineNumber())));
   }

   public void reLaunchDebugger(DebugApplicationInstance debugApplicationInstance)
   {
      ReLaunchDebuggerPresenter runDebuggerPresenter = new ReLaunchDebuggerPresenter(debugApplicationInstance);
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
      startDebugger = false;
      buildApplication();
   }

   @Override
   public void onDebugApp(DebugAppEvent event)
   {
      startDebugger = true;
      buildApplication();
   }

   private void buildApplication()
   {
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent());
   }

   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         warUrl = event.getBuildStatus().getDownloadUrl();
         if (startDebugger)
            debugApplication();
         else
            runApplication();

      }
   }

   private void debugApplication()
   {
      try
      {
         AutoBean<DebugApplicationInstance> debugApplicationInstance =
            DebuggerExtension.AUTO_BEAN_FACTORY.debugApplicationInstance();

         AutoBeanUnmarshaller<DebugApplicationInstance> unmarshaller =
            new AutoBeanUnmarshaller<DebugApplicationInstance>(debugApplicationInstance);
         ApplicationRunnerClientService.getInstance().debugApplication(warUrl,
            new AsyncRequestCallback<DebugApplicationInstance>(unmarshaller)
            {
               @Override
               protected void onSuccess(DebugApplicationInstance result)
               {
                  warUrl = null;
                  String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(result.getName());
                  msg +=
                     "<br>"
                        + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(),
                           getAppUrlsAsString(result));
                  IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
                  doRunDebugger(result);
                  IDE.fireEvent(new AppStartedEvent());
                  runningApp = result;
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
                     OutputMessage.Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
   }

   protected void doRunDebugger(final DebugApplicationInstance debugApplicationInstance)
   {
      AutoBean<DebuggerInfo> debuggerInfo = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerInfo.class);
      AutoBeanUnmarshaller<DebuggerInfo> unmarshaller = new AutoBeanUnmarshaller<DebuggerInfo>(debuggerInfo);
      try
      {
         DebuggerClientService.getInstance().create(debugApplicationInstance.getDebugHost(),
            debugApplicationInstance.getDebugPort(), new AsyncRequestCallback<DebuggerInfo>(unmarshaller)
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
               }
            });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }

   }

   private void runApplication()
   {
      try
      {
         AutoBean<ApplicationInstance> applicationInstance = DebuggerExtension.AUTO_BEAN_FACTORY.applicationInstance();

         AutoBeanUnmarshaller<ApplicationInstance> unmarshaller =
            new AutoBeanUnmarshaller<ApplicationInstance>(applicationInstance);
         ApplicationRunnerClientService.getInstance().runApplication(warUrl,
            new AsyncRequestCallback<ApplicationInstance>(unmarshaller)
            {
               @Override
               protected void onSuccess(ApplicationInstance result)
               {
                  warUrl = null;
                  String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(result.getName());
                  msg +=
                     "<br>"
                        + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(),
                           getAppUrlsAsString(result));
                  IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
                  IDE.fireEvent(new AppStartedEvent());
                  runningApp = result;
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
                     OutputMessage.Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed(),
            OutputMessage.Type.INFO));
      }
   }

   private String getAppUrlsAsString(ApplicationInstance application)
   {
      String appUris = "";
      String uri = application.getWebURL();
      if (!uri.startsWith("http"))
      {
         uri = "http://" + uri;
      }
      appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
      if (!appUris.isEmpty())
      {
         // crop unnecessary symbols
         appUris = appUris.substring(2);
      }
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
                  IDE.fireEvent(new AppStopedEvent(runningApp.getName()));
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

   @Override
   public void onAppStoped(AppStopedEvent appStopedEvent)
   {
      String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStoped(appStopedEvent.getAppName());
      IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
      runningApp = null;
   }

}
