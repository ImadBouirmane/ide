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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.MimeType.TEXT_PLAIN;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.GET;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link DebuggerClientService}.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DebuggerClientServiceImpl implements DebuggerClientService {
    /** REST-service context. */
    private final String     baseUrl;
    private final Loader     loader;
    private final DtoFactory dtoFactory;

    /**
     * Create client service.
     *
     * @param baseUrl
     *         REST-service context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected DebuggerClientServiceImpl(@Named("restContext") String baseUrl, Loader loader, DtoFactory dtoFactory) {
        this.loader = loader;
        this.baseUrl = baseUrl + "/debug-java/" + Utils.getWorkspaceId();
        this.dtoFactory = dtoFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/connect";
        final String params = "?host=" + host + "&port=" + port;
        AsyncRequest.build(GET, requestUrl + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = baseUrl + "/disconnect/" + id;
        loader.setMessage("Disconnecting... ");
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = baseUrl + "/breakpoints/add/" + id;
        final String json = dtoFactory.toJson(breakPoint);
        AsyncRequest.build(POST, requestUrl).data(json).header(CONTENT_TYPE, APPLICATION_JSON).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/breakpoints/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = baseUrl + "/breakpoints/delete/" + id;
        final String json = dtoFactory.toJson(breakPoint);
        AsyncRequest.build(POST, requestUrl).data(json).header(CONTENT_TYPE, APPLICATION_JSON).loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/breakpoints/delete_all/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<DebuggerEventList> callback) throws RequestException {
        final String requestUrl = baseUrl + "/events/" + id;
        AsyncRequest.build(GET, requestUrl).loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStackFrameDump(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/dump/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void resume(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = baseUrl + "/resume/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        final String requestUrl = baseUrl + "/value/get/" + id;
        final String json = dtoFactory.toJson(var.getVariablePath());
        AsyncRequest.build(POST, requestUrl).data(json).header(CONTENT_TYPE, APPLICATION_JSON).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = baseUrl + "/value/set/" + id;
        final String json = dtoFactory.toJson(request);
        AsyncRequest.build(POST, requestUrl).data(json).header(CONTENT_TYPE, APPLICATION_JSON).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepInto(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = baseUrl + "/step/into/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepOver(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = baseUrl + "/step/over/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepReturn(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = baseUrl + "/step/out/" + id;
        AsyncRequest.build(GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        final String requestUrl = baseUrl + "/expression/" + id;
        AsyncRequest.build(POST, requestUrl).data(expression).header(ACCEPT, TEXT_PLAIN).header(CONTENTTYPE, TEXT_PLAIN)
                    .loader(new EmptyLoader()).send(callback);
    }
}