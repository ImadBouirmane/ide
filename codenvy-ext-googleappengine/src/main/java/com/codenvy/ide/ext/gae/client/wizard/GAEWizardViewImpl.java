package com.codenvy.ide.ext.gae.client.wizard;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class GAEWizardViewImpl extends Composite implements GAEWizardView {
    interface GAEPageViewImplUiBinder extends UiBinder<Widget, GAEWizardViewImpl> {}

    private static GAEPageViewImplUiBinder uiBinder = GWT.create(GAEPageViewImplUiBinder.class);

    @UiField
    TextBox existedAppId;

    @UiField
    CheckBox requiredAppId;

    @UiField(provided = true)
    GAELocalization constant;

    private ActionDelegate delegate;

    @Inject
    public GAEWizardViewImpl(GAELocalization constant) {
        this.constant = constant;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public String getApplicationId() {
        return existedAppId.getText();
    }

    @Override
    public void setApplicationId(String applicationId) {
        existedAppId.setText(applicationId);
    }

    @Override
    public boolean getAppIdRequired() {
        return requiredAppId.getValue();
    }

    @Override
    public void setAppIdRequired(boolean enabled) {
        requiredAppId.setEnabled(enabled);
    }

    @Override
    public void enableApplicationIdField(boolean enable) {
        existedAppId.setEnabled(enable);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("requiredAppId")
    public void onRequiredAppIdClicked(ClickEvent event) {
        delegate.onAppIdRequiredClicked();
    }

    @UiHandler("existedAppId")
    public void onExistedAppIdChanged(KeyUpEvent event) {
        delegate.onApplicationIdChanged();
    }
}
