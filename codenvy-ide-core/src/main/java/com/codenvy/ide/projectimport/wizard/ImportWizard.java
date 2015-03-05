/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projectimport.wizard;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.ImportResponse;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.vfs.gwt.client.VfsServiceClient;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projectimport.wizard.ImportProjectNotificationSubscriber;
import com.codenvy.ide.api.wizard.AbstractWizard;
import com.codenvy.ide.commons.exception.JobNotFoundException;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Project import wizard used for importing a project.
 *
 * @author Artem Zatsarynnyy
 */
public class ImportWizard extends AbstractWizard<ImportProject> {

    private final ProjectServiceClient     projectServiceClient;
    private final VfsServiceClient         vfsServiceClient;
    private final RunnerServiceClient      runnerServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final DtoFactory               dtoFactory;
    private final EventBus                 eventBus;
    private final DialogFactory            dialogFactory;
    private final CoreLocalizationConstant localizationConstant;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;

    /**
     * Creates project wizard.
     *
     * @param dataObject
     *         wizard's data-object
     * @param projectServiceClient
     *         GWT-client for Project service
     * @param vfsServiceClient
     *         GWT-client for VFS service
     * @param runnerServiceClient
     *         GWT-client for Runner
     * @param dtoUnmarshallerFactory
     *         {@link DtoUnmarshallerFactory} instance
     * @param dtoFactory
     *         {@link DtoFactory} instance
     * @param eventBus
     *         {@link EventBus} instance
     * @param dialogFactory
     *         {@link DialogFactory} instance
     * @param localizationConstant
     *         {@link CoreLocalizationConstant} instance
     */
    @Inject
    public ImportWizard(@Assisted ImportProject dataObject,
                        ProjectServiceClient projectServiceClient,
                        VfsServiceClient vfsServiceClient,
                        RunnerServiceClient runnerServiceClient,
                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                        DtoFactory dtoFactory,
                        EventBus eventBus,
                        DialogFactory dialogFactory,
                        CoreLocalizationConstant localizationConstant,
                        ImportProjectNotificationSubscriber importProjectNotificationSubscriber) {
        super(dataObject);
        this.projectServiceClient = projectServiceClient;
        this.vfsServiceClient = vfsServiceClient;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
    }

    /** {@inheritDoc} */
    @Override
    public void complete(@Nonnull CompleteCallback callback) {
        checkFolderExistenceAndImport(callback);
    }

    private void checkFolderExistenceAndImport(final CompleteCallback callback) {
        // check on VFS because need to check whether the folder with the same name already exists in the root of workspace
        final String projectName = dataObject.getProject().getName();
        vfsServiceClient.getItemByPath(projectName, new AsyncRequestCallback<Item>() {
            @Override
            protected void onSuccess(Item result) {
                callback.onFailure(new Exception(localizationConstant.createProjectFromTemplateProjectExists(projectName)));
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProject(callback);
            }
        });
    }

    private void importProject(final CompleteCallback callback) {
        final String projectName = dataObject.getProject().getName();
        importProjectNotificationSubscriber.subscribe(projectName);
        final Unmarshallable<ImportResponse> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ImportResponse.class);
        projectServiceClient.importProject(
                projectName, false, dataObject, new AsyncRequestCallback<ImportResponse>(unmarshaller) {
                    @Override
                    protected void onSuccess(final ImportResponse result) {
                        importProjectNotificationSubscriber.onSuccess();
                        callback.onCompleted();

                        // propose user to get more RAM and open project
                        checkRam(result.getProjectDescriptor(), new ConfirmCallback() {
                            @Override
                            public void accepted() {
                                openProject(result.getProjectDescriptor());
                            }
                        });
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        importProjectNotificationSubscriber.onFailure(exception.getMessage());
                        deleteProject(projectName);
                        callback.onFailure(new Exception(getImportErrorMessage(exception)));
                    }
                });
    }

    private void openProject(ProjectDescriptor project) {
        eventBus.fireEvent(new OpenProjectEvent(project.getName()));
        //if (!project.getProblems().isEmpty()) {
        //    eventBus.fireEvent(new ConfigureProjectEvent(project));
        //}
    }

    private String getImportErrorMessage(Throwable exception) {
        if (exception instanceof JobNotFoundException) {
            return "Project import failed";
        } else if (exception instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedException = (UnauthorizedException)exception;
            ServiceError serverError = dtoFactory.createDtoFromJson(unauthorizedException.getResponse().getText(),
                                                                    ServiceError.class);
            return serverError.getMessage();
        } else {
            return dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class).getMessage();
        }
    }

    private void deleteProject(final String name) {
        projectServiceClient.delete(name, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.info(ImportWizard.class, "Project " + name + " deleted.");
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ImportWizard.class, exception);
            }
        });
    }

    private void checkRam(final ProjectDescriptor projectDescriptor, final ConfirmCallback callback) {
        int requiredRAM = 0;
        final RunnersDescriptor runners = projectDescriptor.getRunners();
        if (runners != null) {
            final RunnerConfiguration defaultRunnerConf = runners.getConfigs().get(runners.getDefault());
            if (defaultRunnerConf != null) {
                requiredRAM = defaultRunnerConf.getRam();
            }
        }
        if (requiredRAM <= 0) {
            callback.accepted();
            return;
        }

        final int finalRequiredRAM = requiredRAM;
        final Unmarshallable<ResourcesDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class);
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ResourcesDescriptor result) {
                final int workspaceRAM = Integer.valueOf(result.getTotalMemory());
                if (workspaceRAM < finalRequiredRAM) {
                    dialogFactory.createMessageDialog(localizationConstant.createProjectWarningTitle(),
                                                      localizationConstant.getMoreRam(finalRequiredRAM, workspaceRAM),
                                                      callback).show();
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.accepted();
                Log.error(ImportWizard.class, exception.getMessage());
            }
        });
    }
}