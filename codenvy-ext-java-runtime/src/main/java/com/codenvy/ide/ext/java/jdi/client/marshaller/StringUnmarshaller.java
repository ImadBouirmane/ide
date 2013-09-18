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
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Deserializer for response's body.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EvaluateExpressionPresenter.java May 7, 2012 13:29:01 PM azatsarynnyy $
 */
public class StringUnmarshaller implements Unmarshallable<String> {
    private String payload;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) {
        payload = response.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getPayload() {
        return payload;
    }
}