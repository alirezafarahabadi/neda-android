package ir.batna.neda.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ClientApp {

    //Creates two timers that are going to send registration Tokens to push server until a successful result is
    //returned which sets the "IsregisteredMarket/Riot" to true and stops timers. That code is in Message Handle class.
    public static Timer registerAppTimer = new Timer();
    private Context context;
    String packageName;

    public ClientApp(Context context, String packageName) {

        this.context = context;
        this.packageName = packageName;
    }

    public void registerClientApp() {


        //checks to see if the app is running for the first time, if so, it will generate and
        //save the token
//        if (sharedPref.loadData(packageName + "Token").equals("empty")) {
//            //Build token
//            String appToken = new SabaSecureRandom().generateSecureRandom();
//            Log.i("creating token", "for " + packageName + " for the first time");
//            sharedPref.saveData(packageName + "Token", appToken);
//            sharedPref.saveData(packageName + "IsRegistered", "false");
//        }

        //send Token back to app
        Intent intent = new Intent();
        intent.putExtra("type", "registerApp");
        intent.putExtra("app", packageName);
//        intent.putExtra("token", sharedPref.loadData(packageName + "Token"));
        intent.setComponent(new ComponentName(packageName, packageName + ".SabaClientService"));
        if (Build.VERSION.SDK_INT >= 26) {
            Log.i("sending token", "back to " + packageName);
            context.startForegroundService(intent);
        } else context.startService(intent);

        //send Token to push server
//        String registerApp = "{\"type\":\"registerApp\",\"app\":\"" + packageName + "\", \"token\":\"" + sharedPref.loadData(packageName + "Token") + "\"}";
        Log.i("after websocket", "sent registerApp");
//        Log.i("token value", sharedPref.loadData(packageName + "IsRegistered"));
//        ws.send(registerApp);
//        if (sharedPref.loadData(packageName + "IsRegistered").equalsIgnoreCase("false")) {
//
//            registerAppTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (sharedPref.loadData(packageName + "IsRegistered").equalsIgnoreCase("false")) {
//                        ws.send(registerApp);
//                        Log.i("retrying", "to send token to server");
//                    }
//                }
//            }, 60000, 60000);
//        }


    }

}
