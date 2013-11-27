/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2013] Codenvy, S.A. 
 * All Rights Reserved.
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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Provider;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link TemplateAgentImpl} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@GwtModule("com.codenvy.ide.Core")
public class TemplateAgentImplTest extends GwtTestWithMockito {
    @Mock
    private NewProjectWizard  newProjectWizard;
    private TemplateAgentImpl agent;

    @Before
    public void setUp() {
        agent = new TemplateAgentImpl(newProjectWizard);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegister() throws Exception {
        assertEquals(agent.getTemplatesForProjectType("primaryNature", JsonCollections.createArray("secondaryNature")).size(), 0);

        Provider<? extends AbstractTemplatePage> pageProvider = mock(Provider.class);
        JsonArray<Provider<? extends AbstractTemplatePage>> pages = JsonCollections.createArray(pageProvider, pageProvider);

        agent.register("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"), pages);

        assertEquals(agent.getTemplatesForProjectType("primaryNature", JsonCollections.createArray("secondaryNature")).size(), 1);
        verify(newProjectWizard, times(pages.size())).addPageAfterChooseTemplate(eq(pageProvider));
    }

    @Test
    public void testRegisterWhenTemplateWithGivenIdIsExist() throws Exception {
        assertEquals(agent.getTemplatesForProjectType("primaryNature", JsonCollections.createArray("secondaryNature")).size(), 0);

        agent.register("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"),
                       JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray());

        assertEquals(agent.getTemplatesForProjectType("primaryNature", JsonCollections.createArray("secondaryNature")).size(), 1);

        agent.register("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"),
                       JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray());

        assertEquals(agent.getTemplatesForProjectType("primaryNature", JsonCollections.createArray("secondaryNature")).size(), 1);
    }
}