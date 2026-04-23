package funs.games.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import funs.common.pages.CBaseActivity;
import funs.common.tools.CLogger;
import funs.common.tools.CUtils;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games.page
 * @ClassName: ShareActivity
 * @Description: share
 */
public class ShareActivity extends CBaseActivity {
    private static final String TAG = "ShareActivity";

//    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImmersive();

        View view = new View(this);
        view.setPadding(1, 1, 1, 1);
        view.setBackgroundColor(Color.TRANSPARENT);
        setContentView(view);

        view.setOnClickListener(v -> finish());

        CLogger.d(TAG, "onCreate()");
        // todo test
        CUtils.appShareText(this, "GF piano", null);

//        if (null!=view) view.postDelayed(() -> {
//            if (!isFinishing()) finish();
//        }, 2000);
    }

    public static void startMe(Activity context) {
        context.startActivity(new Intent(context, ShareActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        CLogger.d(TAG, "onStop()");

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        CLogger.d(TAG, "onDestroy()");
    }
}
