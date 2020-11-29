package com.tt.handsomeman.util;

import android.content.SharedPreferences;

import com.tt.handsomeman.HandymanApp;

import javax.inject.Inject;

import dagger.Module;

@Module
public class SharedPreferencesUtils {

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesUtils(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public <T> void put(String key,
                        T data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, HandymanApp.getInstance().getGSon().toJson(data));
        }
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key,
                     Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) sharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(sharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(sharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(sharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(sharedPreferences.getLong(key, 0));
        } else {
            return HandymanApp.getInstance().getGSon().fromJson(sharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
