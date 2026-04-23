package funs.common.pages;

import android.os.Build;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * @ProjectName: HopeV2rayNG
 * @Package: funs.common.pages
 * @ClassName: CBaseActivity
 * @Description: 基类
 */
public class CBaseActivity extends AppCompatActivity {

    protected void replaceFragment(@IdRes int layoutId, @NonNull Fragment page) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(layoutId, page);
        ft.commit();
    }

    /**
     * 隐藏底部导航栏
     *
     * 弹出dialog框的时候，隐藏底部导航栏
     * https://blog.csdn.net/dami_lixm/article/details/82700569
     */
    protected void fullScreenImmersive() {
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }
}
