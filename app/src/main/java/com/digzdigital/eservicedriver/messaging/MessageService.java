package com.digzdigital.eservicedriver.messaging;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.digzdigital.eservicedriver.MainActivity;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

/**
 * Created by Digz on 04/02/2016.
 */
public class MessageService extends Service implements SinchClientListener {


    private static final String APP_KEY = "ae92ce1d-2070-4bf1-a127-dd182cc35654";
    private static final String APP_SECRET = "peYGBqKF7ky5pPEIUIp93A==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    private String currentUserId;
    private SharedPreferences sharedPref;
    private String regId;
    private Intent broadcastIntent = new Intent("com.digzdigital.eservice.MainActivity");
    private LocalBroadcastManager broadcaster;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        broadcaster = LocalBroadcastManager.getInstance(this);

//        regId = findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString("id", null));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        regId = sharedPref.getString(MainActivity.KEY_DEVICE, "");
        currentUserId = sharedPref.getString(MainActivity.KEY_DEVICE, "");
        if (currentUserId != null && !isSinchClientStarted()) {
            startSinchClient(currentUserId);
        }


        return super.onStartCommand(intent, flags, startId);
    }


    public void startSinchClient(String username) {

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        //this client listener requires that you define
        //a few methods below
        sinchClient.addSinchClientListener(this);
        //messaging is "turned-on", but calling is not
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.checkManifest();
        sinchClient.setSupportPushNotifications(true);
        sinchClient.start();
        sinchClient.registerPushNotificationData(regId.getBytes());
    }


    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    //The next 5 methods are for the sinch client listener
    @Override
    public void onClientFailed(SinchClient client, SinchError error) {

        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);
        sinchClient = null;
    }


    @Override
    public void onClientStarted(SinchClient client) {

        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);
    }


    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
    }

    @Override
    public void onLogMessage(int level, String area, String message) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        try {
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
        } catch (NullPointerException e) {
            Log.d("DIGZ", "error", e);
        }

    }

    //public interface for ListUsersActivity & MessagingActivity
    public class MessageServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            MessageService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return MessageService.this.isSinchClientStarted();
        }
    }


}
