package ir.batna.neda.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NedaUtils {

    public static final String NOTIFICATION_CHANNEL_ID = "Neda";
    public static final int SOCKET_RETRY_INTERVAL = 5000;
    public static final String NOTIFICATION_TEXT_CONNECTING = "Connecting to Neda . . .";
    public static final String NOTIFICATION_TEXT_CLOSED = "Neda disconnected";
    public static final String NOTIFICATION_TEXT_RECONNECTING = "Reconnecting in " +
            SOCKET_RETRY_INTERVAL + " seconds . . .";
    public static final String NOTIFICATION_TEXT = "Listening for push notification . . .";
    public static final int NOTIFICATION_ID = 270;
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Channel for Neda app";
    public static final String TYPE = "type";
    public static final String REGISTER_APP = "registerApp";
    public static final String APP = "app";
    public static final String NEDA_CLIENT_SERVICE = "MyIntentService";
    public static final String PUSH = "push";
    public static final String TOKEN = "token";
    public static final String DATA = "data";
    public static final String START_NEDA_SERVICE = "startNedaService";
    public static final String SIGNATURE = "signature";
    public static final String NOT_REGISTERED = "not_registered";
    public static final String REGISTERED = "registered";
    public static final String CLIENT_SERVICE_COMPONENT = ".NedaClientService";
    public static final String REGISTER = "register";
    public static final String RESULT = "result";
    public static final String SUCCESS = "success";
    public static final String DEVICE_ID = "deviceId";
    public static final String MESSAGE_HANDLE_SERVICE = "MessageHandleService";
    public static final String MESSAGE_HANDLE = "messageHandle";
    public static final String PACKAGE_NAME = "packageName";
    public static final String CLIENT_REGISTER_SERVICE = "ClientRegisterService";

    public enum NedaMode {
        LEGACY,
        FOREGROUND,
        SYSTEM
    }


    public static String getFormatttedDate(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getCurrentDateInFormat() {

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        String formattedDate = getFormatttedDate(currentDate);
        return formattedDate;
    }

    public static String getJsonFormat(String packageName, String token) {

        String format = "{\"type\":\"registerApp\",\"app\":\"" + packageName + "\", \"token\":\"" + token + "\"}";
        return format;
    }

    public static String getDeviceIdInJason(String deviceId) {

        String format = "{\"type\":\"register\",\"deviceId\":\"" + deviceId + "\"}";
        return format;
    }

    public static boolean isAppInstalled(Context context, String packageName, String signature) {

        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                if (Build.VERSION.SDK_INT >= 28) {

                    PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                    if (applicationInfo.packageName.equalsIgnoreCase(packageName)) {
                        Signature[] installedSignatures = packageInfo.signingInfo.getApkContentsSigners();
                        String signatureDigest = NedaUtils.getSha256(installedSignatures[0].toCharsString());
                        if (signatureDigest.equalsIgnoreCase(signature)) {
                            return true;
                        }
                    }

                } else {

                    PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNATURES);
                    if (applicationInfo.packageName.equalsIgnoreCase(packageName)) {
                        Signature[] installedSignatures = packageInfo.signatures;
                        String signatureDigest = NedaUtils.getSha256(installedSignatures[0].toCharsString());
                        if (signatureDigest.equalsIgnoreCase(signature)) {
                            return true;
                        }
                    }

                }
            } catch (PackageManager.NameNotFoundException e) {
                log(e.getMessage());
                e.printStackTrace();
            }

        }
        return false;
    }


    public static boolean isSystemPackage(PackageInfo pkgInfo) {

        return (((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) ? true : false;
    }

    public static boolean isInstalledAsSystemApp(Context context) {

        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_META_DATA);
            return (((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) ? true : false;
        } catch (PackageManager.NameNotFoundException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static String getSha256(String input) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            byte[] byteData = digest.digest(input.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < byteData.length; i++){
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void log(String text) {

        Log.v("NEDA ", "==> " + text);
    }
}
