/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.core;

import elemental.client.Browser;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.CloseCurrentProjectEvent;
import com.codenvy.ide.api.event.CloseCurrentProjectHandler;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.OpenProjectHandler;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedHandler;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;

/**
 * Class that does some preliminary operations before opening/closing projects.
 * <p/>
 * E.g.:
 * <ul>
 * <li>closing already opened project before opening another one;</li>
 * <li>setting Browser tab's title;</li>
 * <li>rewriting URL in Browser's address bar;</li>
 * <li>setting {@link CurrentProject} to {@link AppContext};</li>
 * <li>etc.</li>
 * </ul>
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectStateHandler implements OpenProjectHandler, CloseCurrentProjectHandler, ProjectDescriptorChangedHandler {
    private EventBus                 eventBus;
    private AppContext               appContext;
    private ProjectServiceClient     projectServiceClient;
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private CoreLocalizationConstant coreLocalizationConstant;

    @Inject
    public ProjectStateHandler(AppContext appContext, EventBus eventBus, ProjectServiceClient projectServiceClient,
                               CoreLocalizationConstant coreLocalizationConstant, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.coreLocalizationConstant = coreLocalizationConstant;

        eventBus.addHandler(OpenProjectEvent.TYPE, this);
        eventBus.addHandler(CloseCurrentProjectEvent.TYPE, this);
        eventBus.addHandler(ProjectDescriptorChangedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenProject(final OpenProjectEvent event) {
        // previously opened project should be correctly closed
        if (appContext.getCurrentProject() != null) {
            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(appContext.getCurrentProject().getRootProject()));
        }

        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.getProject(event.getProjectName(), new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor projectDescriptor) {
                appContext.setCurrentProject(new CurrentProject(projectDescriptor));

                Document.get().setTitle(coreLocalizationConstant.codenvyTabTitle(projectDescriptor.getName()));
                rewriteBrowserHistory(event.getProjectName());

                // notify all listeners about opening project
                eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(projectDescriptor));
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(AppContext.class, throwable);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseCurrentProject(CloseCurrentProjectEvent event) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            ProjectDescriptor closedProject = currentProject.getRootProject();
            // Note: currentProject must be null BEFORE firing ProjectClosedEvent
            appContext.setCurrentProject(null);

            Document.get().setTitle(coreLocalizationConstant.codenvyTabTitle());
            rewriteBrowserHistory(null);

            // notify all listeners about closing project
            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(closedProject));
        }
    }

    private void rewriteBrowserHistory(@Nullable String projectName) {
        String url = Config.getContext() + "/" + Config.getWorkspaceName();
        if (projectName != null) {
            url += "/" + projectName;
        }
        Browser.getWindow().getHistory().replaceState(null, Window.getTitle(), url);
    }

    @Override
    public void onProjectDescriptorChanged(ProjectDescriptorChangedEvent event) {
        String path = event.getProjectDescriptor().getPath();
        if (appContext.getCurrentProject().getProjectDescription().getPath().equals(path)) {
            appContext.getCurrentProject().setProjectDescription(event.getProjectDescriptor());
        }
    }
}