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
package com.codenvy.ide.ui.dialogs.askValue;

import elemental.events.KeyboardEvent.KeyCode;

import com.codenvy.ide.ui.Locale;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Window for asking user to enter any value.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
public class AskValueDialog extends Window {

    @UiField
    Label message;

    @UiField
    TextBox value;

    private AskValueCallback callback;

    interface AskUiBinder extends UiBinder<Widget, AskValueDialog> {
    }

    private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

    private Locale locale = GWT.create(Locale.class);

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(String title, String message, final AskValueCallback callback) {
        this(title, message, null, callback);
    }

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param defaultValue
     *         default value for input field
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(final String title, final String message, final String defaultValue, final AskValueCallback callback) {
        this(title, message, defaultValue, 0, 0, callback);
    }

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param defaultValue
     *         default value for input field
     * @param selectionStartIndex
     *         indicates the start position of selection
     * @param selectionLength
     *         indicates length of selection
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(final String title, final String message, final String defaultValue,
                          final int selectionStartIndex, final int selectionLength, final AskValueCallback callback) {
        this.callback = callback;
        Widget widget = uiBinder.createAndBindUi(this);
        setTitle(title);
        this.message.setText(message);
        setWidget(widget);
        Button ok = createButton(locale.ok(), "askValue-dialog-ok", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onOk(value.getValue());
                onClose();
            }
        });
        Button cancel = createButton(locale.cancel(), "askValue-dialog-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onCancel();
                onClose();
            }
        });
        getFooter().add(cancel);
        getFooter().add(ok);

        if (defaultValue != null && !defaultValue.isEmpty()) {
            value.setText(defaultValue);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    //value.setSelectionRange(0, defaultValue.lastIndexOf('.'));
                    value.setSelectionRange(selectionStartIndex, selectionLength);
                }
            });
        }
        this.ensureDebugId("askValueDialog-window");
        this.value.ensureDebugId("askValueDialog-textBox");
    }

    @UiHandler("value")
    void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCode.ENTER && callback != null) {
            callback.onOk(value.getValue());
            onClose();
        }
    }

    @Override
    public void show() {
        new Timer() {
            @Override
            public void run() {
                value.setFocus(true);
            }
        }.schedule(300);
        super.show();
    }

    @Override
    protected void onClose() {
        hide();
    }
}
