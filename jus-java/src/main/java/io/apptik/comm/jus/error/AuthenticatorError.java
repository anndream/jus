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

package io.apptik.comm.jus.error;

/**
 * Error indicating that there was an authentication failure when performing a Request.
 */
@SuppressWarnings("serial")
public class AuthenticatorError extends JusError  {

    public AuthenticatorError(Throwable cause) {
        super(cause);
    }

    public AuthenticatorError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public AuthenticatorError(String exceptionMessage) {
        super(exceptionMessage);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
