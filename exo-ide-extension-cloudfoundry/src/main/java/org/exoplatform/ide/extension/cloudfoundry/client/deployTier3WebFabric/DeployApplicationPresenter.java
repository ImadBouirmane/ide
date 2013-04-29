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
package org.exoplatform.ide.extension.cloudfoundry.client.deployTier3WebFabric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryRESTfulRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployApplicationPresenter.java Apr 24, 2013 3:14:13 PM azatsarynnyy $
 */
public class DeployApplicationPresenter implements ProjectBuiltHandler, HasPaaSActions, VfsChangedHandler {
    interface Display {
        HasValue<String> getNameField();

        HasValue<String> getUrlField();

        HasValue<String> getServerField();

        /**
         * Set the list of servers to ServerSelectField.
         * 
         * @param servers
         */
        void setServerValues(String[] servers);

        Composite getView();

    }

    private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

    private VirtualFileSystemInfo                         vfs;

    private Display                                       display;

    private String                                        server;

    private String                                        name;

    private String                                        url;

    /** Public url to war file of application. */
    private String                                        warUrl;

    private String                                        projectName;

    private ProjectModel                                  project;

    private DeployResultHandler                           deployResultHandler;

    public DeployApplicationPresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                name = event.getValue();
            }
        });

        display.getUrlField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                url = event.getValue();
            }
        });

        display.getServerField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                server = display.getServerField().getValue();
                // if url set automatically, than try to create url using server and name
                String target = display.getServerField().getValue();
                String sufix = target.substring(target.indexOf("."));
                String oldUrl = display.getUrlField().getValue();
                String prefix = "<name>";
                if (!oldUrl.isEmpty() && oldUrl.contains(".")) {
                    prefix = oldUrl.substring(0, oldUrl.indexOf("."));
                }
                String url = prefix + sufix;
                display.getUrlField().setValue(url);
            }
        });
    }

    /**
     * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven
     *      .client.event.ProjectBuiltEvent)
     */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication();
        }
    }

    // ----Implementation------------------------

    private void buildApplication() {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent(project));
    }

    /** Create application on Tier3 Web Fabric by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn(String server) {
                if (server != null) {
                    DeployApplicationPresenter.this.server = server;
                }
                createApplication();
            }
        };
        JobManager.get().showJobSeparated();
        AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                                                                    CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
        AutoBeanUnmarshallerWS<CloudFoundryApplication> unmarshaller =
                                                                       new AutoBeanUnmarshallerWS<CloudFoundryApplication>(
                                                                                                                           cloudFoundryApplication);

        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            CloudFoundryClientService.getInstance()
                                     .createWS(
                                               server,
                                               name,
                                               null,
                                               url,
                                               0,
                                               0,
                                               noStart,
                                               vfs.getId(),
                                               project.getId(),
                                               warUrl,
                                               WEB_FABRIC,
                                               new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                               loggedInHandler,
                                                                                                               null,
                                                                                                               server, WEB_FABRIC) {
                                                   @Override
                                                   protected void onSuccess(CloudFoundryApplication result) {
                                                       onAppCreatedSuccess(result);
                                                   }

                                                   @Override
                                                   protected void onFailure(Throwable exception) {
                                                       deployResultHandler.onDeployFinished(false);
                                                       IDE.fireEvent(
                                                          new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
                                                       super.onFailure(exception);
                                                   }
                                               });
        } catch (WebSocketException e) {
            createApplicationREST(loggedInHandler);
        }
    }

    /**
     * Create application on Tier3 Web Fabric by sending request over HTTP.
     * 
     * @param loggedInHandler handler that should be called after success login
     */
    private void createApplicationREST(LoggedInHandler loggedInHandler) {
        AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                                                                    CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
        AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                                                                     new AutoBeanUnmarshaller<CloudFoundryApplication>(
                                                                                                                       cloudFoundryApplication);

        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            CloudFoundryClientService.getInstance().create(server,
                                                           name,
                                                           null,
                                                           url,
                                                           0,
                                                           0,
                                                           noStart,
                                                           vfs.getId(),
                                                           project.getId(),
                                                           warUrl,
                                                           WEB_FABRIC,
                                                           new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                                         loggedInHandler,
                                                                                                                         null, server,
                                                                                                                         WEB_FABRIC) {
                                                               @Override
                                                               protected void onSuccess(CloudFoundryApplication result) {
                                                                   onAppCreatedSuccess(result);
                                                               }

                                                               @Override
                                                               protected void onFailure(Throwable exception) {
                                                                   deployResultHandler.onDeployFinished(false);
                                                                   IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(),
                                                                                                 OutputMessage.Type.INFO));
                                                                   super.onFailure(exception);
                                                               }
                                                           });
        } catch (RequestException e) {
            deployResultHandler.onDeployFinished(false);
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Performs action when application successfully created.
     * 
     * @param app
     * @link CloudFoundryApplication} which is created
     */
    private void onAppCreatedSuccess(CloudFoundryApplication app) {
        warUrl = null;
        String msg = lb.applicationCreatedSuccessfully(app.getName());
        if ("STARTED".equals(app.getState())) {
            if (app.getUris().isEmpty()) {
                msg += "<br>" + lb.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + lb.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
        }
        deployResultHandler.onDeployFinished(true);
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        IDE.fireEvent(new RefreshBrowserEvent(project));
    }

    /**
     * Returns application URLs as string.
     * 
     * @param application {@link CloudFoundryApplication Cloud Foundry application}
     * @return application URLs
     */
    private String getAppUrlsAsString(CloudFoundryApplication application) {
        String appUris = "";
        for (String uri : application.getUris()) {
            if (!uri.startsWith("http")) {
                uri = "http://" + uri;
            }
            appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        }
        if (!appUris.isEmpty()) {
            // crop unnecessary symbols
            appUris = appUris.substring(2);
        }
        return appUris;
    }

    private void fillFields() {
        display.getNameField().setValue(projectName);
        name = projectName;
        server = display.getServerField().getValue();
        // String urlSufix = server.substring(server.indexOf("."));
        // display.getUrlField().setValue(name + urlSufix);
        url = display.getUrlField().getValue();
    }

    public void performValidation() {
        LoggedInHandler validateHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn(String server) {
                performValidation();
            }
        };

        try {
            CloudFoundryClientService.getInstance().validateAction("create",
                                                                   server,
                                                                   name,
                                                                   null,
                                                                   url,
                                                                   vfs.getId(),
                                                                   null,
                                                                   WEB_FABRIC,
                                                                   0,
                                                                   0,
                                                                   true,
                                                                   new CloudFoundryAsyncRequestCallback<String>(null, validateHandler,
                                                                                                                null,
                                                                                                                server, WEB_FABRIC) {
                                                                       @Override
                                                                       protected void onSuccess(String result) {
                                                                           beforeDeploy();
                                                                       }
                                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        // TODO validate
        createProject(projectTemplate);
    }

    private void beforeDeploy() {
        try {
            VirtualFileSystem.getInstance()
                             .getChildren(project,
                                          new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                              @Override
                                              protected void onSuccess(List<Item> result) {
                                                  project.getChildren().setItems(result);
                                                  for (Item i : result) {
                                                      if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName())) {
                                                          buildApplication();
                                                          return;
                                                      }
                                                  }
                                                  createApplication();
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                         "Can't receive project children "
                                                                                             + project.getName()));
                                              }
                                          });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        this.projectName = projectName;
        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();
        fillFields();

        return display.getView();
    }

    private void createProject(ProjectTemplate projectTemplate) {
        final Loader loader = new GWTLoader();
        loader.setMessage(lb.creatingProject());
        loader.show();
        try {
            TemplateService.getInstance()
                           .createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
                                                      projectTemplate.getName(),
                                                      new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel())) {

                                                          @Override
                                                          protected void onSuccess(ProjectModel result) {
                                                              loader.hide();
                                                              project = result;
                                                              deployResultHandler.onProjectCreated(project);
                                                              beforeDeploy();
                                                          }

                                                          @Override
                                                          protected void onFailure(Throwable exception) {
                                                              loader.hide();
                                                              IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                          }
                                                      });
        } catch (RequestException e) {
            loader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void deploy(ProjectModel project, DeployResultHandler deployResultHandler) {
        this.project = project;
        this.deployResultHandler = deployResultHandler;
        beforeDeploy();
    }

    /** @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate() */
    @Override
    public boolean validate() {
        return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
               && display.getUrlField().getValue() != null && !display.getUrlField().getValue().isEmpty();
    }

    @Override
    public void deployFirstTime(final String projectName, final ProjectTemplate projectTemplate,
                                final DeployResultHandler deployResultHandler) {

        this.deployResultHandler = deployResultHandler;
        this.projectName = projectName + "-" + rand();

        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();

        display.getNameField().setValue(projectName);

        try {
            TemplateService.getInstance()
                           .createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
                                                      projectTemplate.getName(),
                                                      new AsyncRequestCallback<ProjectModel>(
                                                                                             new ProjectUnmarshaller(new ProjectModel())) {

                                                          @Override
                                                          protected void onSuccess(ProjectModel result) {
                                                              fillFields();
                                                              project = result;
                                                              deployResultHandler.onProjectCreated(project);
                                                              Scheduler.get()
                                                                       .scheduleDeferred(new Scheduler.ScheduledCommand() {


                                                                           @Override
                                                                           public void execute() {
                                                                               beforeDeploy();
                                                                           }
                                                                       });
                                                          }

                                                          @Override
                                                          protected void onFailure(Throwable exception) {
                                                              IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                          }
                                                      });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private int rand() {
        return (int)(Math.floor(Math.random() * 999 - 100) + 100);
    }
}
