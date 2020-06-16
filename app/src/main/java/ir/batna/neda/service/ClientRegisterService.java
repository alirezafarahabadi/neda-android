package ir.batna.neda.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;

import ir.batna.neda.database.ClientApp;
import ir.batna.neda.database.ClientAppDatabase;
import ir.batna.neda.utils.NedaSecureRandom;
import ir.batna.neda.utils.NedaUtils;

import static ir.batna.neda.service.NedaService.ws;
import static ir.batna.neda.utils.NedaUtils.APP;
import static ir.batna.neda.utils.NedaUtils.NOT_REGISTERED;
import static ir.batna.neda.utils.NedaUtils.getFormatttedDate;
import static ir.batna.neda.utils.NedaUtils.getJsonFormat;
import static ir.batna.neda.utils.NedaUtils.log;

public class ClientRegisterService extends IntentService {

    public ClientRegisterService() {
        super(NedaUtils.CLIENT_REGISTER_SERVICE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        log("ClientRegisterService started");
        Bundle bundle = intent.getExtras();
        String packageName = bundle.getString(NedaUtils.PACKAGE_NAME);
        String signature = bundle.getString(NedaUtils.SIGNATURE);
        long installDate = bundle.getLong(NedaUtils.INSTALL_DATE);
        registerApp(this, packageName, signature, installDate);
    }

    private static void registerApp(Context context, String packageName, String signature, long installDate) {

        log("Reading database for packageName: " + packageName + " and signature: " + signature);
        ClientAppDatabase database = ClientAppDatabase.getDatabase(context);
        ClientApp clientApp = database.clientAppDao().findByPackageInfo(packageName, signature, String.valueOf(installDate));
        if (clientApp == null) {

            // saving app as a new client database
            log("No such package in database, registering as a new client");
            String token = NedaSecureRandom.generateSecureRandomToken();
            log("Token generated: " + token);
            String date = getFormatttedDate(Calendar.getInstance().getTime());
            log("Date is: " + date);
            ClientApp newClientApp = new ClientApp(packageName, signature, token, NOT_REGISTERED, String.valueOf(installDate), date, date);
            database.clientAppDao().insert(newClientApp);
            log("app saved in database");

            // sending token back to the requesting app
            log("Sending token back to the requesting app");
            Intent intent = new Intent();
            intent.putExtra(NedaUtils.TYPE, NedaUtils.REGISTER_APP);
            intent.putExtra(APP, packageName);
            intent.putExtra(NedaUtils.TOKEN, token);
            intent.setComponent(new ComponentName(packageName, packageName + NedaUtils.CLIENT_SERVICE_COMPONENT));
            Log.i("sending token", " back to " + packageName);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }


            // registering app on the server
            log("Sending token push server");
            String json = getJsonFormat(packageName, token);
            ws.send(json);
        } else {

            // sending token back to the requesting app
            log("package: " + packageName + " exists in database");
            String token = clientApp.token;
            Intent intent = new Intent();
            intent.putExtra(NedaUtils.TYPE, NedaUtils.REGISTER_APP);
            intent.putExtra(APP, packageName);
            intent.putExtra(NedaUtils.TOKEN, token);
            intent.setComponent(new ComponentName(packageName, packageName + NedaUtils.CLIENT_SERVICE_COMPONENT));
            log("Sending token back to " + packageName);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }

            // registering app on the server
            if (clientApp.status.equalsIgnoreCase(NOT_REGISTERED)) {
                log("Sending token push server");
                String json = getJsonFormat(packageName, token);
                ws.send(json);
            } else {
                log("package: " + packageName + " was registered in push server before");
            }
        }
    }
}
