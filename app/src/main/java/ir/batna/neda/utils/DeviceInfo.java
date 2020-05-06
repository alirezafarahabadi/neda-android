package ir.batna.neda.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.sabaos.core.SabaUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

public class DeviceInfo {

    private static final String applicationVersion = "3.0";
    private Context context;

    public DeviceInfo(Context context) {

        this.context = context;
    }

    public String getApplicationVersion() {

        return applicationVersion;
    }

    public String getIMEI() {

        ArrayList<String> IMEI = new ArrayList<String>();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {

            if (Build.VERSION.SDK_INT >= 26) {

                int phoneSlots = tm.getPhoneCount();

                for (int i = 0; i <= phoneSlots; i++) {

                    IMEI.add(tm.getImei(i));
                }
            } else {

                int phoneSlots = tm.getPhoneCount();

                for (int i = 0; i <= phoneSlots; i++) {

                    IMEI.add(tm.getDeviceId(i));
                }
            }
        }

        String phoneIMEI = "";
        for (int i = 0; i < IMEI.size(); i++) {

            phoneIMEI += "&imei[" + String.valueOf(i) + "]" + "=" + IMEI.get(i);

        }

        return phoneIMEI;
    }

    public String getfirstIMEI() {

        String phoneIMEI = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {

            if (Build.VERSION.SDK_INT >= 26) {

                phoneIMEI = tm.getImei();

            } else {

                phoneIMEI = tm.getDeviceId();
            }
        }
        return phoneIMEI;
    }

    public String getPhoneSerialNumber() {

        String phoneSerialNumber = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);

            phoneSerialNumber = (String) get.invoke(c, "sys.serialnumber", "error");
            if (phoneSerialNumber.equals("error")) {
                phoneSerialNumber = (String) get.invoke(c, "ril.serialnumber", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phoneSerialNumber;
    }

    public String getHWSerialNumber() {

        String hwSerialNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hwSerialNumber = Build.getSerial(); // Requires permission READ_PHONE_STATE
        } else {
            hwSerialNumber = Build.SERIAL; // Will return 'unknown' for device >= Build.VERSION_CODES.O
        }
        return hwSerialNumber;
    }

    public String getOsName() {

        return Build.VERSION.RELEASE;
    }

    public String getPhoneModel() {

        return Build.MANUFACTURER
                + " " + Build.MODEL;
    }

    public String getOsSecurityLevel() {

        SabaUtils SecurityUtils = new SabaUtils();
        return SecurityUtils.getSecurityLevel(context);
    }

    public String showUsedMemory() {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        double allMemory = ((new Long(memoryInfo.totalMem)).doubleValue()) / (1000 * 1000 * 1000);
        double usedMemory = ((new Long(memoryInfo.totalMem - memoryInfo.availMem)).doubleValue()) / (1000 * 1000 * 1000);
        BigDecimal bd1 = new BigDecimal(allMemory);
        BigDecimal bd2 = new BigDecimal(usedMemory);
        BigDecimal allMemoryRounded = bd1.round(new MathContext(1, RoundingMode.HALF_UP));
        BigDecimal usedMemoryRounded = bd2.round(new MathContext(2, RoundingMode.HALF_UP));
        String memoryStatus = String.valueOf(usedMemoryRounded) + "GB" + " / " + String.valueOf(allMemoryRounded) + "GB";
        return memoryStatus;
    }

    public int showProgressValue() {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        double allMemory = ((new Long(memoryInfo.totalMem)).doubleValue()) / (1000 * 1000 * 1000);
        double usedMemory = ((new Long(memoryInfo.totalMem - memoryInfo.availMem)).doubleValue()) / (1000 * 1000 * 1000);
        double progressValueDouble = (usedMemory / allMemory) * 100;
        int progress = new Double(progressValueDouble).intValue();
        return progress;
    }
}
