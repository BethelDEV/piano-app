package funs.common.tools;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * @ProjectName: HopeV2rayNG
 * @Package: funs.common.tools
 * @ClassName: CUtils
 * @Description:
 */
public final class CUtils {
    /**
     * 系统调用 分享文字
     *
     * @param context
     * @param text
     */
    public static void appShareText(@NonNull Context context, @NonNull CharSequence text, CharSequence title) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        Intent its = TextUtils.isEmpty(title) ? intent : Intent.createChooser(intent, title);
        context.startActivity(its);
    }

    public static void startUiAnimation(@NonNull View view, @AnimRes int anim) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), anim);
        view.startAnimation(animation);
    }

    private static String getProcessNameReflect() {
        String processName = null;
        try {
            final Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, new Object[0]);
            if (invoke instanceof String) {
                processName = (String) invoke;
            }
        } catch (Throwable ignored) {
        }
        return processName;
    }

    public static String getCurrentProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        } else {
            return getProcessNameReflect();
        }
    }

    /**
     * 判断当前是否有网络连接,但是如果该连接的网络无法上网，也会返回true
     *
     * @param context
     * @return
     */
    private static boolean isNetConnection(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();// activeNetworkInfo
            boolean connected = null != networkInfo && networkInfo.isConnected();
            if (connected) {
                return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * 判断当前网络是否可用(6.0以上版本)
     * 实时
     *
     * @param context
     * @return
     */
    public static boolean isNetSystemUsable(Context context) {
        boolean isNetUsable = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            if (networkCapabilities != null) {
                isNetUsable =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        } else {
            isNetUsable = isNetConnection(context);
        }
        return isNetUsable;
    }


    public static void toast(Context context, CharSequence str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceType")
    public static void toast(Context context, @IdRes int strId) {
        Toast.makeText(context, strId, Toast.LENGTH_SHORT).show();
    }
}
