package ir.batna.neda.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ir.batna.neda.service.NedaService;

import static ir.batna.neda.application.Neda.nedaContext;
import static ir.batna.neda.utils.NedaUtils.log;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {

            case Intent.ACTION_BOOT_COMPLETED : log("Startup receiver: " + Intent.ACTION_BOOT_COMPLETED); break;
            case Intent.ACTION_LOCKED_BOOT_COMPLETED: log("Startup receiver: " + Intent.ACTION_LOCKED_BOOT_COMPLETED); break;
        }

        log("Starting StartupReceiver");
        NedaService.initialize(nedaContext);
    }
}
