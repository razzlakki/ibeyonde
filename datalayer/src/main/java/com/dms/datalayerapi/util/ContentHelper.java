package com.dms.datalayerapi.util;

import android.content.ContentValues;

/**
 * Created by Raja.p on 09-09-2016.
 */
public class ContentHelper {

    ContentValues contentValues;

    public static ContentHelper init() {
        return new ContentHelper();
    }

    public ContentHelper add(String key, Integer value) {
        this.contentValues.put(key, value);
        return this;
    }

    public ContentHelper add(String key, String value) {
        this.contentValues.put(key, value);
        return this;
    }

    public ContentHelper add(String key, Double value) {
        this.contentValues.put(key, value);
        return this;
    }

    public ContentHelper add(String key, Float value) {
        this.contentValues.put(key, value);
        return this;
    }

    private ContentHelper() {
        contentValues = new ContentValues();
    }

    public ContentValues get() {
        return contentValues;
    }
}
