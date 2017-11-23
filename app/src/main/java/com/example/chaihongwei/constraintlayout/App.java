package com.example.chaihongwei.constraintlayout;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ChaiHongwei on 2017/4/13 20:05.
 * 通过反射获取到的App对象
 */
public class App {
    private static final String TAG = App.class.getSimpleName();

    public static final Application INSTANCE;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Log.e(TAG, "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Log.e(TAG, "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            INSTANCE = app;
        }
    }

    private static Activity findActivityFrom(final Context context) {
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof Application || context instanceof Service) return null;
        if (!(context instanceof ContextWrapper)) return null;
        final Context base_context = ((ContextWrapper) context).getBaseContext();
        if (base_context == context) return null;
        return findActivityFrom(base_context);
    }

    public static void startActivity(final Context context, final Intent intent) {
        final Activity activity = findActivityFrom(context);
        if (activity != null) activity.startActivity(intent);
        else context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}