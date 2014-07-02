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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to clear Runner console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ClearConsoleAction extends Action {

    private final RunnerConsolePresenter presenter;
    private final ResourceProvider       resourceProvider;

    @Inject
    public ClearConsoleAction(RunnerConsolePresenter presenter,
                              ResourceProvider resourceProvider,
                              RunnerResources resources,
                              RunnerLocalizationConstant localizationConstant) {
        super(localizationConstant.clearConsoleControlTitle(), localizationConstant.clearConsoleControlDescription(), null,
              resources.clear());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        e.getPresentation().setEnabledAndVisible(activeProject != null);
    }
}
