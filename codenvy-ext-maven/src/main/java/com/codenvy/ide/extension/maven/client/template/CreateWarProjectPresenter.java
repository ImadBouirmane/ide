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
package com.codenvy.ide.extension.maven.client.template;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.marshal.ProjectModelProviderAdapter;
import com.codenvy.ide.resources.marshal.ProjectModelUnmarshaller;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class CreateWarProjectPresenter implements CreateProjectProvider {
    private String                     projectName;
    private CreateProjectClientService service;
    private ResourceProvider           resourceProvider;

    @Inject
    public CreateWarProjectPresenter(CreateProjectClientService service, ResourceProvider resourceProvider) {
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** {@inheritDoc} */
    @Override
    public void create(final AsyncCallback<Project> callback) {
        JsonArray<Property> properties = JsonCollections
                .<Property>createArray(new Property("vfs:mimeType", "text/vnd.ideproject+directory"),
                                       new Property("vfs:projectType", "Servlet/JSP"),
                                       new Property("exoide:projectDescription", "Java Web project."),
                                       new Property("exoide:target",
                                                    JsonCollections.createArray("CloudBees", "CloudFoundry", "AWS", "AppFog")));

        ProjectModelProviderAdapter adapter = new ProjectModelProviderAdapter(resourceProvider);
        ProjectModelUnmarshaller unmarshaller = new ProjectModelUnmarshaller(adapter);

        try {
            service.createWarProject(projectName, properties,
                                     new AsyncRequestCallback<ProjectModelProviderAdapter>(unmarshaller) {
                                         @Override
                                         protected void onSuccess(ProjectModelProviderAdapter result) {
                                             resourceProvider.getProject(result.getProject().getName(), new AsyncCallback<Project>() {
                                                 @Override
                                                 public void onSuccess(Project result) {
                                                     callback.onSuccess(result);
                                                 }

                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     callback.onFailure(caught);
                                                 }
                                             });
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             callback.onFailure(exception);
                                         }
                                     });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }
}