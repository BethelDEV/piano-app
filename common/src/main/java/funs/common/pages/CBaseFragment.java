package funs.common.pages;

import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

/**
 * @ProjectName: HopeV2rayNG
 * @Package: funs.common.pages
 * @ClassName: CBaseFragment
 * @Description: 基类
 */
public class CBaseFragment extends Fragment {

    protected void runOnUiThread(Runnable task) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(task);
        }
    }

    protected void postDelayed(Runnable task, long delayMillis) {
        View view = getView();
        if (view != null) {
            view.postDelayed(task, delayMillis);
        }
    }

    public boolean onKeyDownPage(int keyCode, KeyEvent event) {
        return false;
    }
    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }
}
