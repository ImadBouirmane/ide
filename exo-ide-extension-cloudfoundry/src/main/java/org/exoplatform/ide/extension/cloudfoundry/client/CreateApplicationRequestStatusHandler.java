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
package org.exoplatform.ide.extension.cloudfoundry.client;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.LOCALIZATION_CONSTANT;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 9:35:47 AM anya $
 */
public class CreateApplicationRequestStatusHandler implements RequestStatusHandler {
    private String applicationName;

    private PAAS_PROVIDER paasProvider;

    public CreateApplicationRequestStatusHandler(String applicationName, PAAS_PROVIDER paasProvider) {
        this.applicationName = applicationName;
        this.paasProvider = paasProvider;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        if (paasProvider == WEB_FABRIC) {
            job.setStartMessage(LOCALIZATION_CONSTANT.createApplicationStartedWebFabric(applicationName));
        } else {
            job.setStartMessage(LOCALIZATION_CONSTANT.createApplicationStartedCloudFoundry(applicationName));
        }
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        if (paasProvider == WEB_FABRIC) {
            job.setFinishMessage(LOCALIZATION_CONSTANT.createApplicationFinishedWebFabric(applicationName));
        } else {
            job.setFinishMessage(LOCALIZATION_CONSTANT.createApplicationFinishedCloudFoundry(applicationName));
        }
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable) */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, JobStatus.ERROR);
        job.setError(exception);
        IDE.fireEvent(new JobChangeEvent(job));
    }
}
