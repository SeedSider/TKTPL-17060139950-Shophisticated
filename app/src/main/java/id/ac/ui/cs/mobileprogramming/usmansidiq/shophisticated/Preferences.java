package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    static final String KEY_INCOME_T0DAY = "income";

    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setIncomeToday(Context context, int income){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(KEY_INCOME_T0DAY, income);
        editor.apply();
    }

    public static int getIncomeToday(Context context){
        return getSharedPreference(context).getInt(KEY_INCOME_T0DAY,0);
    }

    public static void clearIncomeToday (Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_INCOME_T0DAY);
        editor.apply();
    }
}
