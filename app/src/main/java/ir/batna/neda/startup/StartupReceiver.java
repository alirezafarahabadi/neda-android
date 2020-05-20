package ir.batna.neda.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ir.batna.neda.service.NedaService;

import static ir.batna.neda.utils.NedaUtils.log;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        log("Starting StartupReceiver");
        if (Build.VERSION.SDK_INT >= 24) {
            context = context.createDeviceProtectedStorageContext();
            log("Created protected Context!");
        }
        NedaService.initialize(context);
    }
}
