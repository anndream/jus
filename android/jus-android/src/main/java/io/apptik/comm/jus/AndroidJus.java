/*
 * Copyright (C) 2012 The Android Open Source Project
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

package io.apptik.comm.jus;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

import io.apptik.comm.jus.stack.HttpStack;
import io.apptik.comm.jus.stack.HurlStack;
import io.apptik.comm.jus.toolbox.DiskBasedCache;
import io.apptik.comm.jus.toolbox.HttpNetwork;

public class AndroidJus {

    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "jus";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        JusLog.log = new ALog();
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        String userAgent = "jus/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            stack = new HurlStack();
        }

        Network network = new HttpNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network,
                RequestQueue.DEFAULT_NETWORK_THREAD_POOL_SIZE,
                new AndroidExecutorDelivery(new Handler(Looper.getMainLooper())));
        queue.withCacheDispatcher(
                new AndroidCacheDispatcher(
                        queue.cacheQueue, queue.networkQueue, queue.cache,
                        queue.delivery))
                .withNetworkDispatcherFactory(
                        new AndroidNetworkDispatcher.NetworkDispatcherFactory(
                                queue.networkQueue, queue.network,
                                queue.cache, queue.delivery));
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }
}
