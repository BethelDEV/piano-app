package funs.common.tools;

import android.util.Log;

import funs.common.BuildConfig;

/**
 * @ProjectName: common
 * @Package: funs.common.tools
 * @ClassName: CLogger
 * @Description:
 */
public final class CLogger {
//    private static final boolean DEBUG = BuildConfig.DEBUG;
private static final boolean debug = BuildConfig.DEBUG;

    public static void i(String tag, String format, Object... args) {
        if (debug) {
            String msg = String.format(format, args);
            i(tag, msg);
        }
    }


    public static void i(String tag, String msg) {
        if (debug) Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (debug) Log.d(tag, msg);
    }
    public static void e(String tag, String msg) {
        if (debug) Log.e(tag, msg);
    }
}
