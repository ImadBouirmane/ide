/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.extensions.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.extensions.client.UnzipTemplateClientService;
import com.codenvy.ide.ext.extensions.client.UnzipTemplateClientServiceImpl;
import com.codenvy.ide.ext.extensions.client.template.CreateSampleCodenvyExtensionPageView;
import com.codenvy.ide.ext.extensions.client.template.CreateSampleExtensionPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * GIN module for 'Codenvy Extensions Runtime' extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeGinModule.java Jul 2, 2013 4:44:09 PM azatsarynnyy $
 */
@ExtensionGinModule
public class ExtRuntimeGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(UnzipTemplateClientService.class).to(UnzipTemplateClientServiceImpl.class).in(Singleton.class);
        bind(CreateSampleCodenvyExtensionPageView.class).to(CreateSampleExtensionPageViewImpl.class).in(Singleton.class);
    }
}
