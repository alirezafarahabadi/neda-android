package ir.batna.neda.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences sharedPreferences;
    private Context context;

    public SharedPref(Context context) {

        sharedPreferences = context.getSharedPreferences("NetworkUsageData", Context.MODE_PRIVATE);
        this.context = context;
    }

    public void saveData(String key, String value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String loadData(String key) {

        String result = sharedPreferences.getString(key, "empty");

        return result;
    }
}
