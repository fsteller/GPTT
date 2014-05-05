package com.credomatic.gprod.android.gptt.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.services.HTTPService;
import com.credomatic.gprod.android.gptt.utilities.net.NetUtilities;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class AppGPTT extends Application {

    private static final String TAG = AppGPTT.class.getSimpleName();
    private static final long[] VIBRATE = {0, 100, 200, 200, 100, 300};

    //<editor-fold desc="Variables">

    private int httpServicePort = -1;
    private boolean isHttpServiceRunning = false;
    private NotificationManager notificationManager;
    private SharedPreferences pref;

    //</editor-fold>

    //<editor-fold desc="Public Methods">

    //<editor-fold desc="Http Service Methods">

    public void startHttpServer() {
        Log.i(TAG, "Starting Http Server");
        final Intent httpServiceIntent = new Intent(this, HTTPService.class);
        startService(httpServiceIntent);
        isHttpServiceRunning = true;
    }

    public void stopHttpServer() {
        Log.i(TAG, "Stopping Http Server");
        final Intent httpServiceIntent = new Intent(this, HTTPService.class);
        stopService(httpServiceIntent);
        isHttpServiceRunning = false;
    }

    public boolean isHttpServiceRunning() {
        return isHttpServiceRunning;
    }

    public int getHttpServerPort() {
        if (httpServicePort == -1)
            httpServicePort = getSharedPreferences().
                    getInt(Constants.PREF_SERVER_PORT, Constants.DEFAULT_SERVER_PORT);

        return httpServicePort;
    }

    public String getHttpServerAddress() {

        return String.format("http://%s:%s%s",
                NetUtilities.getLocalIpV4Address(),
                getHttpServerPort(),
                getResources().getString(R.string.trxResponsePattern));
    }

    //</editor-fold>
    //<editor-fold desc="Notification Methods">

    public void sendNotificationMessage(final String message, final int id, final int flags) {

        getNotificationManager();
        final boolean isVibrate = getBooleanPreference(Constants.PREF_VIBRATE, true);
        final boolean isPlaySound = getBooleanPreference(Constants.PREF_PLAYSOUND, true);
        final String notificationSound = getStringPreference(Constants.PREF_RINGTONE, "");

        final Intent startIntent = new Intent(this, AppGPTT.class);
        final PendingIntent intent = PendingIntent.getActivity(this, 0, startIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra(Constants.GPTT_MESSAGE, message);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            displayMessage_JELLY_BEAN(this, notificationManager, id, flags, intent, message, isVibrate, isPlaySound, notificationSound);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB) {
            displayMessage_HONEYCOMB(this, notificationManager, id, flags, intent, message, isVibrate, isPlaySound, notificationSound);
        } else
            displayMessage_OLDER(this, notificationManager, intent, message, isVibrate, isPlaySound, notificationSound);
    }

    public void cancelNotification(final int id) {
        if (notificationManager != null) {
            notificationManager.cancel(id);
            notificationManager = null;
        }
    }

    //</editor-fold>
    //<editor-fold desc="Preferences Methods">

    public String getStringPreference(String preferenceKey, String defaultValue) {
        return getSharedPreferences().getString(preferenceKey, defaultValue);
    }

    public Boolean getBooleanPreference(String preferenceKey, boolean defaultValue) {
        return getSharedPreferences().getBoolean(preferenceKey, defaultValue);
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return notificationManager;
    }

    //</editor-fold>

    //</editor-fold>
    //<editor-fold desc="Private Methods">

    private SharedPreferences getSharedPreferences() {
        if (pref == null)
            pref = PreferenceManager.getDefaultSharedPreferences(this);

        return pref;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void displayMessage_JELLY_BEAN(final Context context, final NotificationManager notifyManager, final int id, final int flags, final PendingIntent intent, final String message, final boolean isVibrate, final boolean isPlaysound, final String notificationSound) {

        final String title = context.getString(R.string.message_title);
        final Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(intent)
                .build();

        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.flags |= flags;

        if (isVibrate)
            notification.vibrate = VIBRATE;

        if (isPlaysound && notificationSound.length() > 0)
            notification.sound = Uri.parse(notificationSound);

        notifyManager.notify(id, notification);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void displayMessage_HONEYCOMB(final Context context, final NotificationManager notifyManager, final int id, final int flags, final PendingIntent intent, final String message, final boolean isVibrate, final boolean isPlaysound, final String notificationSound) {

        final String title = context.getString(R.string.message_title);
        final Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(intent)
                .getNotification();


        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.flags |= flags;

        if (isVibrate)
            notification.vibrate = VIBRATE;

        if (isPlaysound && !notificationSound.isEmpty())
            notification.sound = Uri.parse(notificationSound);

        notifyManager.notify(id, notification);
    }

    private static void displayMessage_OLDER(final Context context, final NotificationManager notifyManager, final PendingIntent intent, final String message, final boolean isVibrate, final boolean isPlaysound, final String notificationSound) {

    }

    //</editor-fold>
}

