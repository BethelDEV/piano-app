package funs.games.page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.piano.AppConfigTrigger;
import com.piano.view.PianoCanvasView;
import com.piano.Preferences;

import java.util.Arrays;
import java.util.List;

import funs.games.R;
import funs.common.pages.CBaseActivity;
import funs.common.tools.CLogger;
import funs.common.tools.CUtils;
import funs.games.GfThreadPool;
import funs.games.MelodyHelper;
import funs.games.PianoConst;
import funs.games.PianoSoundHelper;
import funs.games.bean.SongFile;
import funs.games.BuildConfig;

public class PianoActivity extends CBaseActivity implements AppConfigTrigger.AppConfigCallback {
    private static final String TAG = "PianoActivity";

    private PianoCanvasView piano_canvas = null;
//    private DrawerLayout drawerLayout;
    private HorizontalScrollView pianoScrollView;
    private SeekBar seekBar;
    private TextView titleTv;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImmersive();

        // init piano sounds
        GfThreadPool.execute(()-> PianoSoundHelper.initSoundPool(getApplication()));

        // Set a bunch of flags to make it full screen. If any of the features are
        // not available, ignore them

        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) { /* Ignore, the app can survive without fancy UI options */ }

        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) { /* Ignore, the app can survive without fancy UI options */ }

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } catch (Exception e) { /* Ignore, the app can survive without fancy UI options */ }

        setContentView(R.layout.home_page_layout); // activity_main

        pianoScrollView = findViewById(R.id.piano_scrollView);
        piano_canvas = new PianoCanvasView(this);
        pianoScrollView.addView(piano_canvas);
//        pianoScrollView.postDelayed(() -> pianoScrollView.addView(piano_canvas), 10);

        try {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        } catch (Exception e) { /* Ignore, the app can survive without fancy UI options */ }

        View.OnClickListener selectSongListener = v -> SongsActivity.startMe(this);
        findViewById(R.id.songs_btn).setOnClickListener(selectSongListener);

        findViewById(R.id.settings_btn).setOnClickListener(v -> GfSettingsActivity.startMe(this));

        pianoScrollView.setOnTouchListener((view, motionEvent) -> {
            return true; // 禁止滑动
        });
        seekBar = findViewById(R.id.seekbar);
        CUtils.startUiAnimation(seekBar, R.anim.alpha_in);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CLogger.d(TAG, "SeekBar onProgressChanged: " + i);

                int totalX = getPianoViewScrollWidth();
                int destinationX = i * totalX / 1000;
                if (i >= 998) destinationX = totalX;
                if (i < 5) destinationX = 0;
                pianoScrollView.scrollTo(destinationX, 0);
//                piano_canvas.scrollTo(-destinationX, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //  根据 音符定位
        seekBar.postDelayed(() -> {
            if (Preferences.areMelodiesEnabled(getApplication())) {
                scrollToMelody();
            } else {
                seekBar.setProgress(580);
            }
            pianoScrollView.setVisibility(View.VISIBLE);
//            CUtils.startUiAnimation(pianoScrollView, R.anim.alpha_in);
        }, 300);

        pianoScrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            CLogger.i(TAG, "pianoScrollView onScrollChange: (%d, %d) -> (%d, %d)", oldScrollX, oldScrollY, scrollX, scrollY);
        });

        titleTv = findViewById(R.id.song_title);
        titleTv.setOnClickListener(selectSongListener);
        initSongTitle();
        // 注册广播，监听
        registerBroadcastReceiver();

        initAds();
    }

    private void initSongTitle() {
        if (!Preferences.areMelodiesEnabled(getApplication())) {
            if (null!=titleTv) titleTv.setText("");
           return;
        }
        String selectedSongId = Preferences.getSelectedSongId(getApplication());
        if (selectedSongId != null && selectedSongId.startsWith(MelodyHelper.LOCAL_SONG_PATH)) {
            SongFile song = MelodyHelper.getLocalSong(selectedSongId);
            updateSongTitle(song);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(PianoConst.ACTION_SELECTED_SONG_CHANGED);
        filter.addAction(PianoConst.ACTION_KEYBOARD_STYLE_CHANGED);
        filter.addAction(PianoConst.ACTION_MELODY_GUIDE_MODE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter);
    }

    private void scrollToMelody() {
        if (piano_canvas == null || isFinishing()) {
            return;
        }
        final int[] keyRangeOfMelody = piano_canvas.getKeyRangeOfMelody();
        if (keyRangeOfMelody == null || keyRangeOfMelody.length < 1) {
            return;
        }
        int keyIndex = keyRangeOfMelody[0];
        if (keyIndex < 1) return;
        // index -> scroll distance
        int keySum = PianoConst.RANGE.length;
        int pageKeySum = pianoScrollView.getWidth() / piano_canvas.getKeyWidth();

        if (keySum == pageKeySum) return;
        int progress = 1000 * (keyIndex / 2) / (keySum - pageKeySum);
        progress -= 7;
        if (progress < 0) progress = 0;
//        int progress = 1000* (keyIndex / 2 - 1)/(keySum-pageKeySum);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(progress, true);
        } else {
            seekBar.setProgress(progress);
        }
    }

    private int getPianoViewScrollWidth() {
        if (piano_canvas == null) {
            return 0;
        }
        return piano_canvas.getWidth() - pianoScrollView.getWidth();
    }

    void lock_app() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask();
        }
    }

    void unlock_app() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stopLockTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        piano_canvas.selectSoundset(this, Preferences.selectedSoundSet(this));
//        lock_app();
    }

    public void quitApp() {
        unlock_app();
//        this.startActivity(new Intent(this, PianoActivity.class));
//        moveTaskToBack(true);
    }

//    private static final int REQUEST_CONFIG = 1;

    @Override
    public void onConfigOpenRequested() {
        // If you've done the dance to press multiple specific buttons at once, no need to keep the screen locked.
        // It will be a minor inconvenience when returning from settings, because it will prompt the user again
        // to lock the app. However the expectation is that the options are not used very often, and the benefit
        // of having a settings screen work like a more typical Android app probably outweigh the negatives from a
        // child accidentally getting to the settings screen.
        unlock_app();

//        startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SongsActivity.SONG_SELECT_CODE && resultCode == RESULT_OK) {
            onSelectedSongChanged();
        }
    }

    private void onMelodyGuideModeChanged() {
        if (!Preferences.areMelodiesEnabled(getApplication())) {
            if (null!=titleTv) titleTv.setText("");
            piano_canvas.updateMelodyFromAssets(null);
        } else {
            onSelectedSongChanged();
        }
    }
    private void onSelectedSongChanged() {
        String selectedSongId = Preferences.getSelectedSongId(getApplication());
        if (selectedSongId != null && selectedSongId.startsWith(MelodyHelper.LOCAL_SONG_PATH)) {
            SongFile song = MelodyHelper.getLocalSong(selectedSongId);
            if (song != null && null != pianoScrollView) {
                pianoScrollView.post(() -> {
                    if (!isFinishing() && null != piano_canvas) {
                        piano_canvas.updateMelodyFromAssets(song.getFile());

                        pianoScrollView.postDelayed(() -> scrollToMelody(), 80);
                    }
                });

                updateSongTitle(song);
            }
        }
    }

    private void updateSongTitle(SongFile song) {
        titleTv.setText(R.string.song_emoji);
        if (song != null && null != song.getName()) {
            titleTv.append("  ");
            titleTv.append(song.getName());
        }
    }

    @Override
    public void onShowConfigTooltip() {
//        Toast toast = Toast.makeText(getApplicationContext(), R.string.config_tooltip, Toast.LENGTH_LONG);
//        toast.show();
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing()) return;
            final String action = intent.getAction();
            if (PianoConst.ACTION_SELECTED_SONG_CHANGED.equals(action)) {
                onSelectedSongChanged();
            } else if (PianoConst.ACTION_KEYBOARD_STYLE_CHANGED.equals(action)) {
                if (null != piano_canvas) piano_canvas.updateKeyboardStyle();
            } else if (PianoConst.ACTION_MELODY_GUIDE_MODE_CHANGED.equals(action)) {
                if (null != piano_canvas) onMelodyGuideModeChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        PianoSoundHelper.releaseSoundPool();
        super.onDestroy();
    }


    private void initAds() {
        MobileAds.initialize(getApplicationContext());

        RequestConfiguration.Builder builder =  MobileAds.getRequestConfiguration().toBuilder();
        if (BuildConfig.DEBUG) {
            // 添加测试设备 3718358B23CF898D6E3997B7721AC0D1  28CCFA9D827FB834AC99B02F8947FF3D
            List<String> testDeviceIds = Arrays.asList("3718358B23CF898D6E3997B7721AC0D1");
            builder.setTestDeviceIds(testDeviceIds);
        }
        // 符合 Google Play 家庭政策的广告，适合所有用户（包括儿童和家人）
        RequestConfiguration requestConfiguration =
                builder.setTagForChildDirectedTreatment(
                        RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build();

        MobileAds.setRequestConfiguration(requestConfiguration);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        CLogger.i(TAG, "initAds(), AdUnitId: %s" , mAdView.getAdUnitId());
    }

}
