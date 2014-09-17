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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.keybinding.KeyBuilder;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.extension.runner.client.actions.ImageAction;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Listens for opening/closing a project and adds
 * an appropriate actions to the 'Run' -> 'Custom Images' menu.
 *
 * @author Artem Zatsarynnyy
 */
public class ImageActionManager implements ProjectActionHandler {

    /** Project-relative path to the custom Docker-scripts folder. */
    private static final String SCRIPTS_FOLDER_REL_LOCATION = "/src/main";
    private final RunnerLocalizationConstant localizationConstants;
    private final ActionManager              actionManager;
    private final KeyBindingAgent            keyBindingAgent;
    private final RunnerResources            resources;
    private final ProjectServiceClient       projectServiceClient;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final StringSet                  actions;

    @Inject
    public ImageActionManager(RunnerLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              KeyBindingAgent keyBindingAgent,
                              RunnerResources resources,
                              EventBus eventBus,
                              ProjectServiceClient projectServiceClient,
                              DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.localizationConstants = localizationConstants;
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        this.resources = resources;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        actions = Collections.createStringSet();
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(event.getProject().getPath() + SCRIPTS_FOLDER_REL_LOCATION,
                                         new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                                             @Override
                                             protected void onSuccess(Array<ItemReference> result) {
                                                 int i = 1;
                                                 for (ItemReference item : result.asIterable()) {
                                                     addImageAction(i++);
                                                 }
                                             }

                                             @Override
                                             protected void onFailure(Throwable ignore) {
                                                 // no docker-scripts are found
                                             }
                                         });
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        removeAllActions();
    }

    private void addImageAction(int imageNum) {
        // register action
        final ImageAction imageAction = new ImageAction(localizationConstants.imageActionText(imageNum),
                                                        localizationConstants.imageActionDescription(imageNum),
                                                        resources.launchApp());
        final String actionId = localizationConstants.imageActionId(imageNum);
        actions.add(actionId);
        actionManager.registerAction(actionId, imageAction);

        // add actions in 'Custom Images' menu group
        DefaultActionGroup customImagesGroup = (DefaultActionGroup)actionManager.getAction(RunnerExtension.GROUP_CUSTOM_IMAGES);
        customImagesGroup.add(imageAction);

        // bind hot-key to the action
        if (imageNum < 10) {
            keyBindingAgent.getGlobal().addKey(new KeyBuilder().action().alt().charCode(imageNum + 48).build(), actionId);
        }
    }

    /** Remove and unregister all previously added 'Image' actions. */
    private void removeAllActions() {
        actions.iterate(new StringSet.IterationCallback() {
            @Override
            public void onIteration(String key) {
                actionManager.unregisterAction(key);
            }
        });
        DefaultActionGroup customImagesGroup = (DefaultActionGroup)actionManager.getAction(RunnerExtension.GROUP_CUSTOM_IMAGES);
        customImagesGroup.removeAll();
    }
}
