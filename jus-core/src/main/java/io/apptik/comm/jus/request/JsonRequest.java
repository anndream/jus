/*
 * Copyright (C) 2015 Apptik Project
 * Copyright (C) 2014 Kalin Maldzhanski
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apptik.comm.jus.request;

import java.io.UnsupportedEncodingException;

import io.apptik.comm.jus.JusLog;
import io.apptik.comm.jus.NetworkResponse;
import io.apptik.comm.jus.Request;
import io.apptik.comm.jus.Response;
import io.apptik.comm.jus.Response.ErrorListener;
import io.apptik.comm.jus.Response.Listener;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public abstract class JsonRequest<T> extends Request<T> {
    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    protected final Listener<T> mListener;
    protected final String mRequestBody;

    public JsonRequest(int method, String url, String requestBody, Listener<T> listener,
            ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mRequestBody = requestBody;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public abstract Response<T> parseNetworkResponse(NetworkResponse response);

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            JusLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
