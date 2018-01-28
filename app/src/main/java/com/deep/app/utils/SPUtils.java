package com.deep.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangfei on 2018/1/10.
 */

public class SPUtils {
    private static SharedPreferences sharedPreferences = null;
    private static final String name = "umcheck";
    private static final String INSTALL = "install";
    private static void createSharedPreferences(Context context){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences( name, Context.MODE_PRIVATE);
        }
    }
    public static void saveIsInstall(Context context,boolean isInstall){
        createSharedPreferences(context);
        sharedPreferences.edit().putBoolean(INSTALL,isInstall).apply();
    }
    public static boolean getIsInstall(Context context){
        createSharedPreferences(context);
        return sharedPreferences.getBoolean(INSTALL,false);
    }
}
