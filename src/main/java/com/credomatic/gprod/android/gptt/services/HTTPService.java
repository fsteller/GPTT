package com.credomatic.gprod.android.gptt.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.credomatic.gprod.android.gptt.R;
import com.credomatic.gprod.android.gptt.app.AppGPTT;
import com.credomatic.gprod.android.gptt.webserver.WebServer;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public class HTTPService extends Service {

    private static final String TAG = HTTPService.class.getSimpleName();
    private static final int NOTIFICATION_STARTED_ID = 1;

    private WebServer server = null;
    private AppGPTT context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Creating http service");
        context = (AppGPTT) getApplication();
        server = new WebServer(context);
    }

    @Override
    public void onDestroy() {
        server.stopThread();
        context.cancelNotification(NOTIFICATION_STARTED_ID);
        super.onDestroy();
        Log.i(TAG, "Http service destroyed");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        server.startThread();
        final String notificationText = getResources().getString(R.string.notification_started_text);
        context.sendNotificationMessage(notificationText, NOTIFICATION_STARTED_ID, Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR);
        Log.i(TAG, "Http service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}

