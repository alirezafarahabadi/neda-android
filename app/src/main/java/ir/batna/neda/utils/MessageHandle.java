package ir.batna.neda.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static ir.batna.neda.utils.ClientApp.registerAppTimer;

public class MessageHandle {

    public void handleReceivedMessages(Context context, String text) {

        try {

            JSONObject jsonObject = new JSONObject(text);
            String type = jsonObject.getString("type");
            switch (type) {

                case "register":
                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {

                        Log.i("client registration", "success");
                    } else Log.i("client registration", "failure");

                    break;

                case "registerApp":
                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {

                        Log.i("app registration", "success");
                        String app = jsonObject.getString("app");
//                        sharedPref.saveData(app + "IsRegistered", "true");
                        registerAppTimer.cancel();
                    } else Log.i("app registration", "failure");

                    break;

                case "push":

                    String app = jsonObject.getString("app");
                    Intent intent = new Intent();
                    intent.putExtra("type", "push");
                    intent.putExtra("data", jsonObject.getString("data"));
                    intent.setComponent(new ComponentName(app, app + ".SabaClientService"));
                    if (Build.VERSION.SDK_INT >= 26) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }

                    //send ack back to server
                    String ack = "{" +
                            "\"type\":\"push\"," +
                            "\"token\":\"" + new SharedPref(context).loadData(app + "Token") +
                            "\",\"data\":\"" + jsonObject.getString("data") + "\"" +
                            "\"result\":\"success\"" +
                            "}";
//                    ws.send(ack);
                    break;
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }
}
