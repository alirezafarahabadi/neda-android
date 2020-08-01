package ir.batna.neda.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import static ir.batna.neda.utils.NedaUtils.log;

public class Neda extends Application {

    public static Context nedaContext;

    @Override
    public void onCreate() {
        super.onCreate();
        nedaContext = this;
        if (Build.VERSION.SDK_INT >= 24) {
            nedaContext = this.createDeviceProtectedStorageContext();
            log("Created protected Context!");
        }
    }
}
