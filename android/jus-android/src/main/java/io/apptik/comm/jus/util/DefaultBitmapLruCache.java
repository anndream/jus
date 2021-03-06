/*
 * Copyright (C) 2015 AppTik Project
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

package io.apptik.comm.jus.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.apptik.comm.jus.ui.ImageLoader;

/**
 * Bitmap LRU cache with option to save (soft) and reuse Bitmaps when removed from cache.
 * As {@link #reusableBitmaps} is a {@link SoftReference} means that we might not have really big
 * pool thus re-usability and performance due to less GC will be not that high.
 *
 * For true pooled option use {@link PooledBitmapLruCache}.
 */

public class DefaultBitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader
        .ImageCache, BitmapPool {

    Set<SoftReference<Bitmap>> reusableBitmaps = Collections.synchronizedSet(new
            HashSet<SoftReference<Bitmap>>());

    public DefaultBitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    public DefaultBitmapLruCache(int maxSize) {
        super(maxSize);
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() );
        //or ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass()
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    /**
     * Notify the removed entry that is no longer being cached
     */
    @Override
    protected void entryRemoved(boolean evicted, String key,
                                Bitmap oldValue, Bitmap newValue) {
        if (Utils.hasHoneycomb()) {
            // We're running on Honeycomb or later, so add the bitmap
            // to the pool set for possible use with inBitmap later
            addToPool(oldValue);
        } else {
        }

    }

    /**
     * Measure item size in kilobytes rather than units which is more practical
     * for a bitmap cache
     */
    @Override
    protected int sizeOf(String key, Bitmap value) {
        final int bitmapSize = BitmapLruPool.getBitmapSize(value);
        return bitmapSize == 0 ? 1 : bitmapSize;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    /////

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    public Bitmap getReusableBitmap(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (reusableBitmaps != null && !reusableBitmaps.isEmpty()) {
            synchronized (reusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = reusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && BitmapLruPool.canBePooled(item)) {
                        // Check to see it the item can be used for inBitmap
                        if (BitmapLruPool.canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    @Override
    public void addToPool(Bitmap bitmap) {
        if (BitmapLruPool.canBePooled(bitmap)) {
            reusableBitmaps.add(new SoftReference<>(bitmap));
        }
    }




}

