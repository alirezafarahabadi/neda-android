
package ir.batna.neda.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

import ir.batna.neda.R;
import ir.batna.neda.database.ClientApp;
import ir.batna.neda.database.ClientAppDatabase;
import ir.batna.neda.utils.NedaSecureRandom;
import ir.batna.neda.utils.NedaSharedPref;
import ir.batna.neda.utils.NedaUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static ir.batna.neda.utils.NedaUtils.SIGNATURE;
import static ir.batna.neda.utils.NedaUtils.getDeviceIdInJason;
import static ir.batna.neda.utils.NedaUtils.getJsonFormat;
import static ir.batna.neda.utils.NedaUtils.log;

public class NedaService extends Service {

    public static WebSocket ws;
    private static Handler handler = new Handler();
    private static Context context;
    public static NedaUtils.NedaMode nedaMode = NedaUtils.NedaMode.LEGACY;

    public static void initialize(Context context) {

        log("Initializing ...");
        if (NedaUtils.isInstalledAsSystemApp(context)) {
            nedaMode = NedaUtils.NedaMode.SYSTEM;
        } else if (Build.VERSION.SDK_INT >= 26) {
            nedaMode = NedaUtils.NedaMode.FOREGROUND;
        }
        log("nedaMode is " + nedaMode);
        Intent serviceIntent = new Intent(context, NedaService.class);
        serviceIntent.putExtra(NedaUtils.TYPE, NedaUtils.START_NEDA_SERVICE);
        if (nedaMode.toString().equalsIgnoreCase(NedaUtils.NedaMode.SYSTEM.toString())) {
            context.startService(serviceIntent);
        } else {
            if (nedaMode.toString().equalsIgnoreCase(NedaUtils.NedaMode.FOREGROUND.toString())) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        log("NedaService started");
        if (context == null) context = this;
        Bundle bundle = intent.getExtras();
        String type = bundle.getString(NedaUtils.TYPE);
        log("Intent type is " + type);
        switch (type) {

            case NedaUtils.START_NEDA_SERVICE:
                if (ws == null) startNeda(context);
                break;
            case NedaUtils.REGISTER_APP:
                registerClientApp(bundle, context);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startWebSocket(String deviceRegister) {
        log("Starting websocket ...");
        log("Device register: " + deviceRegister);
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("ws://push.batna.ir:8010/").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                log("Websocket opened");
                if (nedaMode.toString().equalsIgnoreCase(NedaUtils.NedaMode.FOREGROUND.toString())) {
                    configureForegroundService(context, NedaUtils.NOTIFICATION_TEXT);
                }
                webSocket.send(deviceRegister);
                registerUnregisteredApps(context);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                log("Received message: " + text);
                Intent intent = new Intent(context, MessageHandleService.class);
                intent.putExtra(NedaUtils.TYPE, NedaUtils.MESSAGE_HANDLE);
                intent.putExtra(NedaUtils.DATA, text);
                context.startService(intent);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                log("Websocket closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                if (nedaMode.toString().equalsIgnoreCase(NedaUtils.NedaMode.FOREGROUND.toString())) {
                    configureForegroundService(context, NedaUtils.NOTIFICATION_TEXT_CLOSED);
                }
                log("Websocket closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                if (nedaMode.toString().equalsIgnoreCase(NedaUtils.NedaMode.FOREGROUND.toString())) {
                    configureForegroundService(context, NedaUtils.NOTIFICATION_TEXT_RECONNECTING);
                }
                log("Websocket failed, reconnecting in " + NedaUtils.SOCKET_RETRY_INTERVAL + "ms");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Thread socketThread =  new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startWebSocket(deviceRegister);
                            }
                        });
                        socketThread.start();
                    }
                }, NedaUtils.SOCKET_RETRY_INTERVAL);
            }
        };
        ws = client.newWebSocket(request, listener);
    }

    private void startNeda(Context context) {

        if (nedaMode.equals(NedaUtils.NedaMode.FOREGROUND)) {
            configureForegroundService(context, NedaUtils.NOTIFICATION_TEXT_CONNECTING);
        }
        NedaSharedPref nedaSharedPref = new NedaSharedPref(context);
        String deviceId = nedaSharedPref.loadStringData(NedaUtils.DEVICE_ID);
        if (deviceId.equalsIgnoreCase("")) {
            log("Creating deviceId ... ");
            deviceId = NedaSecureRandom.generateSecureRandomToken();
            nedaSharedPref.saveData(NedaUtils.DEVICE_ID, deviceId);
        } else {
            log("deviceId exists: " + deviceId);
        }
        String deviceRegister = getDeviceIdInJason(deviceId);
        Thread socketThread =  new Thread(new Runnable() {
            @Override
            public void run() {
                startWebSocket(deviceRegister);
            }
        });
        socketThread.start();
    }

    private void configureForegroundService(Context context, String text) {
        createNotificationChannel(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NedaUtils.NOTIFICATION_CHANNEL_ID)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        startForeground(NedaUtils.NOTIFICATION_ID, notification.build());
        log("Foreground service started");
    }

    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= 26) {
            log("Creating notification channel");
            CharSequence name = NedaUtils.NOTIFICATION_CHANNEL_ID;
            String description = NedaUtils.NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(NedaUtils.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void registerClientApp(Bundle bundle, Context context) {

        String packageName = bundle.getString(NedaUtils.APP);
        String signature = bundle.getString(SIGNATURE);
        long installDate = bundle.getLong(NedaUtils.INSTALL_DATE);
        log("Registering app, packageName: " + packageName + ", signature: " + signature + " installDate: " + installDate);
        Intent clientRegisterIntent = new Intent(context, ClientRegisterService.class);
        clientRegisterIntent.putExtra(NedaUtils.PACKAGE_NAME, packageName);
        clientRegisterIntent.putExtra(SIGNATURE, signature);
        clientRegisterIntent.putExtra(NedaUtils.INSTALL_DATE, installDate);
        context.startService(clientRegisterIntent);
    }

    private void registerUnregisteredApps(Context context) {

        Thread registerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log("Registering unregistered apps in push server");
                ClientAppDatabase database = ClientAppDatabase.getDatabase(context);
                List<ClientApp> unregisteredClientApps = database.clientAppDao().loadAllDataByStatus(NedaUtils.NOT_REGISTERED);
                for (ClientApp clientApp : unregisteredClientApps) {
                    log("Registering package: " + clientApp.packageName + " with signature: " + clientApp.signature + " in push server");
                    String json = getJsonFormat(clientApp.packageName, clientApp.token);
                    ws.send(json);
                }
            }
        });
        registerThread.start();
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:

                    break;
                case WifiManager.WIFI_STATE_DISABLED:

                    break;
            }
        }
    };
}
