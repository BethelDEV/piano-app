package funs.games.page;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.piano.Preferences;

import funs.games.R;
import funs.common.pages.CBaseActivity;
import funs.games.PianoConst;
import funs.games.adapter.SongsAdapter;

public class SongsActivity extends CBaseActivity {
    public static final int SONG_SELECT_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImmersive();

        setContentView(R.layout.activity_songs);
//        if (getSupportActionBar()!=null) {
//            getSupportActionBar().hide();
//        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final String selectedSongId = Preferences.areMelodiesEnabled(this) ?
                Preferences.getSelectedSongId(this) : null;
        SongsAdapter adapter = new SongsAdapter(this, PianoConst.localSongs, selectedSongId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        adapter.setItemClickListener((songId) -> {
            Preferences.setSelectedSongId(this, songId);
            Preferences.setMelodiesEnabled(this, true); // 选择了乐曲，就默认打开了弹奏引导模式
//            setResult(RESULT_OK);
            recyclerView.postDelayed(() -> {
                if (!isFinishing()) finish();
            }, 150);

            LocalBroadcastManager.getInstance(getApplication())
                    .sendBroadcast(new Intent(PianoConst.ACTION_SELECTED_SONG_CHANGED));
        });

//        if (!TextUtils.isEmpty(selectedSongId))
//            Preferences.setMelodiesEnabled(this, true); // 选择了乐曲，就默认打开了弹奏引导模式
    }

    public static void startMe(Activity context) {
//        startMe(context, SONG_SELECT_CODE);
        context.startActivity(new Intent(context, SongsActivity.class));
    }

    public static void startMe4Result(Activity context, int req) {
        context.startActivityForResult(new Intent(context, SongsActivity.class), req);
    }
}