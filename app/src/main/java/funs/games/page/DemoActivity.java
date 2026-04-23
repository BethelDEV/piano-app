package funs.games.page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.piano.view.PianoCanvasView;
import com.piano.Preferences;

import funs.games.R;
import funs.games.PianoConst;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        final ViewGroup layout =findViewById(R.id.group_layout);
        layout.postDelayed(()->{
            PianoCanvasView piano_canvas = new PianoCanvasView(this);
            layout.addView(piano_canvas);
        }, 1000);
    }

    private int minKeyNum, keyStyle;
    private String songId;

    @Override
    protected void onStart() {
        super.onStart();
        minKeyNum = Preferences.getMinNumberOfKeys(this);
        songId = Preferences.getSelectedSongId(this);
        keyStyle = Preferences.getKeyboardStyle(this);

        Preferences.setMinNumberOfKeys(this, PianoConst.RANGE.length);
        Preferences.setSelectedSongId(this, "");
        Preferences.setKeyboardStyle(this, Preferences.KEYBOARD_STYLE_CLASSICAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (minKeyNum> 0) Preferences.setMinNumberOfKeys(this, minKeyNum);
        if (!TextUtils.isEmpty(songId)) Preferences.setSelectedSongId(this, songId);

        Preferences.setKeyboardStyle(this, keyStyle);
    }

    public static void startMe(Context context) {
        context.startActivity(new Intent(context, DemoActivity.class));
    }
}