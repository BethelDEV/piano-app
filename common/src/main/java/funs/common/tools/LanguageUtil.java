package funs.common.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

//import thzbook.BuildConfig;
//import androidx.multidex.BuildConfig;

import java.util.Locale;


public class LanguageUtil {
    private static final String TAG = "LanguageUtil";

    //------------------------------------------------
    public static void setChineseSimpleLanguage(Context context) {
        // ("zh","CN")
        changeLanguage(context, Locale.CHINESE.getLanguage(), Locale.CHINESE.getCountry());
    }

    public static void setEnglishUS(Context context) {
        // ("en","US")
        changeLanguage(context, "en", "");
    }
//    public static void setConfigLanguage(Context context) {
//        changeLanguage(context, BuildConfig.LanguageCode, "");
//
//        Logger.i(TAG, "%s , %s ", BuildConfig.LanguageCode, BuildConfig.APPLICATION_ID);
//    }

    //------------------------------------------------
    public static void changeLanguage(Context context, String language, String country) {
        if (context == null || TextUtils.isEmpty(language)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, null);
        CLogger.i("LanguageUtil", "changeLanguage: %s   %s", language, country);
//        SPUtil.put(Constant.CURRENT_LANGUAGE, language);
//        SPUtil.put(Constant.CURRENT_COUNTRY, country);
//        SPUtil.put(Constant.IS_FOLLOW_SYSTEM, false);
    }

    public static void followSystemLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = Locale.getDefault();
        resources.updateConfiguration(config, null);
//        SPUtil.put(Constant.IS_FOLLOW_SYSTEM, true);
    }

}
