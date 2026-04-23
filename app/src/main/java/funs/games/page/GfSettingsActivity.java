package funs.games.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import funs.games.BuildConfig;
import funs.games.R;
import funs.common.pages.CBaseActivity;
import funs.common.tools.CUtils;

public class GfSettingsActivity extends CBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImmersive();

        setContentView(R.layout.activity_gf_settings);

        TextView nameTv = findViewById(R.id.app_name_tv);
        String cmp = getString(R.string.app_name_cmp);
        if (!TextUtils.equals(cmp, nameTv.getText())) {
            nameTv.append("\n");
            nameTv.append(cmp);
        }

        if (BuildConfig.DEBUG) {
            nameTv.append("\n\n");
            nameTv.append(getChannel(this));
        }

        View closeBtn = findViewById(R.id.close_btn);
        if (null != closeBtn) closeBtn.setOnClickListener(v -> finish());
        // todo
        String APP_STORE_LINK = BuildConfig.appStoreUrl;// share
        String shareMsg = getString(R.string.app_share_tips);
        View shareBtn = findViewById(R.id.share_lv);
        if (null != shareBtn) {
            shareBtn.setOnClickListener(v ->
                    CUtils.appShareText(this, String.format("%s \n %s", shareMsg, APP_STORE_LINK), null));
        }
    }

    private String getChannel(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString("CHANNEL_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    public static void startMe(Activity context) {
        context.startActivity(new Intent(context, GfSettingsActivity.class));
    }

}