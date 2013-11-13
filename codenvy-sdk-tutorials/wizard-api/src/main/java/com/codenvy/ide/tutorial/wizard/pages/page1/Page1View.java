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
package com.codenvy.ide.tutorial.wizard.pages.page1;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link Page1Presenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface Page1View extends View<Page1View.ActionDelegate> {
    /** Required for delegating functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's choosing page 2. */
        void onPage2Chosen();

        /** Performs some actions in response to a user's choosing page 3. */
        void onPage3Chosen();

        /** Performs some actions in response to a user's clicking show page 4. */
        void onPage4Clicked();
    }

    /**
     * Returns whether the page 2 is next.
     *
     * @return <code>true</code> if the page 2 is next, and <code>false</code> if it's not
     */
    boolean isPage2Next();

    /**
     * Change state of the visual component of view. This component provides logical about next page.
     *
     * @param page2Next
     *         need to choose page 2 item or item page 3
     */
    void setPage2Next(boolean page2Next);

    /**
     * Returns whether the page 4 need to show.
     *
     * @return <code>true</code> if the page 4 need to show, and <code>false</code> if it's not
     */
    boolean isPage4Show();

    /**
     * Change state of the visual component of view. This component provides logical about showing page 4.
     *
     * @param skip
     *         <code>true</code> if the page 4 isn't shown, and <code>false</code> otherwise
     */
    void setPage4Show(boolean skip);
}