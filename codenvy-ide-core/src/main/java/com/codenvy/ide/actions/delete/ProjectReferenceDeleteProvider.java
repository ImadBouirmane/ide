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
package com.codenvy.ide.actions.delete;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Delete provider for deleting {@link ProjectReference}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectReferenceDeleteProvider implements DeleteProvider<ProjectReference> {
    private CoreLocalizationConstant localizationConstant;
    private ProjectServiceClient     projectServiceClient;
    private EventBus                 eventBus;
    private NotificationManager      notificationManager;

    @Inject
    public ProjectReferenceDeleteProvider(CoreLocalizationConstant localizationConstant, ProjectServiceClient projectServiceClient,
                                          EventBus eventBus, NotificationManager notificationManager) {
        this.localizationConstant = localizationConstant;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteItem(final ProjectReference item) {
        new Ask(localizationConstant.deleteProjectDialogTitle(), localizationConstant.deleteProjectDialogQuestion(item.getName()), new AskHandler() {
            @Override
            public void onOk() {
                projectServiceClient.delete(item.getName(), new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
                    }
                });
            }
        }).show();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canDelete(Object item) {
        return item instanceof ProjectReference;
    }

}