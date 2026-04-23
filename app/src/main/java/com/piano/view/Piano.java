package com.piano.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.piano.Preferences;
import com.piano.melodies.Melody;
import com.piano.melodies.MelodyPlayer;
import com.piano.melodies.SingleSongMelodyPlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import funs.common.tools.CLogger;
import funs.games.GfThreadPool;
import funs.games.MelodyHelper;
import funs.games.PianoConst;
import funs.games.PianoSoundHelper;
import funs.games.bean.SongFile;

public class Piano {
    private static final String TAG = "_Piano";
    private static final double KEYS_FLAT_HEIGHT_RATIO = 0.5;// 0.55;

    /**
     * Width of a flat key in relation to a regular key.
     */
    private static final double KEYS_FLAT_WIDTH_RATIO = 0.6;

//    static final int MIN_NUMBER_OF_KEYS = 14;//8; //13; //7;
    private final int keys_width;
    private final int keys_flat_width;
    private final int keys_height;
    private final int keys_flats_height;
    private final int keys_count;
    private final int screen_width; // x
    private final boolean[] key_pressed;

    private MelodyPlayer melody = null;
    private final int[] keyRangeOfMelody = {-1,-1}; // {min_index, max_index}

    Piano(final Context context, int screen_size_x, int screen_size_y, final String soundset) {
        this(context, screen_size_x, screen_size_y, 200, soundset);
    }

//    Piano(final Context context, int screen_size_x, int screen_size_y, int white_key_width) {
//        this(context, screen_size_x, screen_size_y, screen_size_y, white_key_width, "");
//    }
    Piano(final Context context, int screen_size_x, int screen_size_y, int white_key_width, final String soundset) {
        screen_width = screen_size_x;
        keys_height = screen_size_y;
        keys_flats_height = (int) (screen_size_y * KEYS_FLAT_HEIGHT_RATIO);

        keys_width = white_key_width;
//        keys_width = Math.min(screen_size_x / MIN_NUMBER_OF_KEYS, 200); // 至少得放得下 7 个按键
        keys_flat_width = (int) (keys_width * KEYS_FLAT_WIDTH_RATIO);

        // Round up for possible half-key display
//        int big_keys = MIN_NUMBER_OF_KEYS; // (screen_size_x / keys_width);
//        final int big_keys = 1 + (screen_size_x / keys_width);
        // Count flats too
//        keys_count = (big_keys * 2) + 1;
        keys_count = 104; //  钢琴通常有88个键盘，其中黑键占据了36个，白键则有52个。

        key_pressed = new boolean[keys_count];
        Arrays.fill(key_pressed, false);
        initSoundSet(context);

        updateMelodyBySP(context);
        CLogger.d(TAG, "Piano()");
    }

    private void updateMelodyBySP(Context context) {
        if (!Preferences.areMelodiesEnabled(context)) {
            updateMelodyPlayer(null);
            return;
        }
        // todo
        String selectedSongId = Preferences.getSelectedSongId(context);
        if (selectedSongId != null && selectedSongId.startsWith(MelodyHelper.LOCAL_SONG_PATH)) {
            SongFile song = MelodyHelper.getLocalSong(selectedSongId);
            if (song != null && null != song.getFile()) {
                updateMelodyFromAssets(context, song.getFile());
            }
        }
    }
    public void updateMelodyFromAssets(Context context, String fileName) {
        if (null==context || TextUtils.isEmpty(fileName)) {
            updateMelodyPlayer(null);
            return;
        }
        MelodyHelper helper = new MelodyHelper();
        Melody song = helper.parseFromAssets(context, fileName);
        MelodyPlayer mp = new SingleSongMelodyPlayer(song);
        updateMelodyPlayer(mp);

        updateKeyRange(song);
    }

    private void updateKeyRange(Melody song) {
        final String[] notes = song.getNotes();
        final int len = null!=notes? notes.length: -1;
        if (len<1) return;
        int min = PianoConst.note2keyIdx.get(notes[0]), max= min;
        for (int i = 1; i < len; i++) {
            int idx = PianoConst.note2keyIdx.get(notes[i]);
            if (min> idx) min=idx;
            if (max< idx) max = idx;
        }
        keyRangeOfMelody[0]=min;
        keyRangeOfMelody[1]=max;
        CLogger.d(TAG, "updateKeyRange()");
    }

    private void updateMelodyPlayer(MelodyPlayer mp) {
        this.melody = mp;
        keyRangeOfMelody[0]=-1;
        keyRangeOfMelody[1]=-1;
        if (mp != null) {
            mp.reset();
        }
        key_idx_melody = -1;
    }

    public int[] getKeyRangeOfMelody() {
        CLogger.i(TAG, "getKeyRangeOfMelody()  %d, %d", keyRangeOfMelody[0], keyRangeOfMelody[1]);
        return keyRangeOfMelody;
    }

    int get_keys_flat_width() {
        return keys_flat_width;
    }

    int get_keys_width() {
        return keys_width;
    }

    int get_keys_count() {
        return keys_count;
    }

    void resetState() {
        Arrays.fill(key_pressed, false);
    }

    boolean is_key_pressed(int key_idx) {
        if (key_idx < 0 || key_idx >= key_pressed.length) {
            CLogger.d(TAG, "This shouldn't happen: Sound out of range, key" + key_idx);
            return false;
        }

        return key_pressed[key_idx];
    }

    private int key_idx_melody = -1;
    private final AtomicBoolean mKeyDownFlag = new AtomicBoolean(false);

    //  draw  icon
    void onDrawPianoKey(PianoCanvasView piano, Canvas canvas, int key_idx, Key key) {
        int rangeIdx = key_idx / 2;
        if (rangeIdx < 0 || rangeIdx >= PianoConst.RANGE.length) return;
        piano.draw_text_on_white_key(canvas, PianoConst.RANGE[rangeIdx], key_idx, 0.95f);
    }

    /**
     * draw music guid emoji
     */
    void onPianoRedrawFinish(PianoCanvasView piano, Canvas canvas) {
        if (piano == null || canvas == null || this.melody == null) {
            return;
        }
        if (!this.melody.hasNextNote()) {
            this.melody.reset();
        }

        int next_key_idx = key_idx_melody;
        draw_emoji_on_piano_key(piano, canvas, next_key_idx);
        CLogger.d(TAG, "onPianoRedrawFinish(), drew current key: " + key_idx_melody);

        if (mKeyDownFlag.get() && !is_key_pressed(key_idx_melody)) {
//        if (key_idx_melody>=0 && mKeyDownFlag.get() && key_idx_melody<keys_count && !key_pressed[key_idx_melody]) {
            CLogger.d(TAG, key_idx_melody + "  key is not pressed, 弹错了, onPianoRedrawFinish()");
            return;
        }
        if (next_key_idx < 0 || mKeyDownFlag.get()) {
            String note = this.melody.nextNote();
            next_key_idx = get_key_idx_from_note(note);
            key_idx_melody = next_key_idx;
            draw_emoji_on_piano_key(piano, canvas, next_key_idx);
            CLogger.d(TAG, String.format("onPianoRedrawFinish(), drew next key: %d, note: %s", next_key_idx, note));
        }
    }

    private void draw_emoji_on_piano_key(PianoCanvasView piano, Canvas canvas, int key_idx) {
        if (piano == null || canvas == null || key_idx < 0) {
            return;
        }
        // black: 1, 3, 7, 9, 11, 15 —— 奇数
        // white: 偶数

        // 👆 ✋
        String emoji = "\uD83D\uDC46";
        if (key_idx % 2 == 1) {
            piano.draw_emoji_on_black_key(canvas, emoji, key_idx);
        } else {
            piano.draw_emoji_on_white_key(canvas, emoji, key_idx);
        }
    }

    void on_key_down(int key_idx) {
        if (key_idx < 0 || key_idx >= key_pressed.length) {
            CLogger.d(TAG, "This shouldn't happen: Sound out of range, key" + key_idx);
            return;
        }
        mKeyDownFlag.set(true);

        key_pressed[key_idx] = true;
        play_sound(key_idx);
    }

    void on_key_cancel() {
        mKeyDownFlag.set(false);
        int size = key_pressed.length;
        for (int i = 0; i < size; i++) {
            if (key_pressed[i]) {
                key_pressed[i] = false;
            }
        }
    }

    void on_key_up(int key_idx) {
        mKeyDownFlag.set(false);
        if (key_idx < 0 || key_idx >= key_pressed.length) {
            CLogger.d(TAG, "This shouldn't happen: Sound out of range, key" + key_idx);
            return;
        }

        key_pressed[key_idx] = false;

        //  Settings Key
        boolean isSettings = isSettingsKey(key_idx);
        CLogger.d(TAG, "on_key_up , isSettings :  " + key_idx + isSettings);
        if (isSettings && null != mKeyClickListener) {
            mKeyClickListener.onSettingsKeyClicked();
        }
    }

    int pos_to_key_idx(float pos_x, float pos_y) {
        final int big_key_idx = 2 * ((int) pos_x / keys_width);
        if (pos_y > keys_flats_height) return big_key_idx;

        // Check if press is inside rect of flat key
        Key flat = get_area_for_flat_key(big_key_idx);
        CLogger.i(TAG, "pos_to_key_idx(%s, %s), big_key_idx: (%d, %d), flat_key: %s", pos_x, pos_y, big_key_idx, big_key_idx + 1, flat);
        if (flat.contains(pos_x, pos_y)) return big_key_idx + 1;

        if (big_key_idx > 0) {
            Key prev_flat = get_area_for_flat_key(big_key_idx - 2);
            CLogger.i(TAG, "pos_to_key_idx(%s, %s), big_key_idx: (%d, %d), prev_flat_key: %s", pos_x, pos_y, big_key_idx - 2, big_key_idx - 1, prev_flat);
            if (prev_flat.contains(pos_x, pos_y)) return big_key_idx - 1;
        }

        // If not in the current or previous flat, it must be a hit in the big key
        return big_key_idx;
    }

    Key get_area_for_key(int key_idx) {
        int x_i = key_idx / 2 * keys_width;
        return new Key(x_i, x_i + keys_width, 0, keys_height);
    }

    Key get_area_for_flat_key(final int key_idx) {
        // 首先，排除首尾两端的特殊情况
//        final int octave_idx = (key_idx / 2) % 7;
        final int octave_idx = (key_idx / 2 - 2) % 7;
        if (key_idx == 2 || key_idx == 3 || key_idx >= 103 || (octave_idx == 2 || octave_idx == 6)) {
//        if (!PianoConst.sBlackKeyIdx.contains(key_idx)) {
            // Keys without flat get a null-area
            return new Key(0, 0, 0, 0);
        }

        final int offset = keys_width - (keys_flat_width / 2);
        int x_i = (key_idx / 2) * keys_width + offset;
        return new Key(x_i, x_i + keys_flat_width, 0, keys_flats_height);
    }

    private static final Map<String, Integer> note_to_key_idx = PianoConst.note2keyIdx; // new HashMap<>();

    int get_key_idx_from_note(String note) {

        Integer key_idx = note_to_key_idx.get(note);
        if (key_idx == null) {
            CLogger.i("PianOli::Piano", "Could not find a key corresponding to the note \"" + note + "\".");

            // 5 is designated as the special sound T.raw.no_note, so the app wont crash, but it wont
            // play a noise either.
            return 5;
        }

        return key_idx;
    }


    private void initSoundSet(final Context context) {
        printThreadInfo();
        GfThreadPool.execute(()-> {
            PianoSoundHelper.tryInitSoundPool(context);
            printThreadInfo();
        });
//        selectSoundset(context, "-");
    }

    private void printThreadInfo() {
        Thread t = Thread.currentThread();
        CLogger.i(TAG, "currentThread: [%d], %s", t.getId(), t.getName());
    }

    // todo
    private void play_sound(final int key_idx) {
        if (key_idx < 0) {
//        if (key_idx < 0 || key_idx >= KeySoundIdx.length) {
            CLogger.d("PianOli::Piano", "This shouldn't happen: Sound out of range, key" + key_idx);
            return;
        }
        CLogger.d(TAG, "play_sound key_idx: " + key_idx);
        PianoSoundHelper.playPianoSoundByKey(key_idx);

//        if (this.melody != null) {
//            if (!this.melody.hasNextNote()) {
//                this.melody.reset();
//            }
//
//            String note = this.melody.nextNote();
//            int next_key_idx = get_key_idx_from_note(note);
//
//            CLogger.d(TAG,String.format("play_sound, melody note :[%s], next_key_idx: %d", note,next_key_idx));
//        }

    }

    /**
     * 最右边的按键 作为 设置键
     *
     * @param key_idx
     * @return
     */
    boolean isSettingsKey(int key_idx) {
        if (key_idx < 14) return false;
        Key key = (key_idx % 2 == 0) ? get_area_for_key(key_idx) : get_area_for_flat_key(key_idx);
        return isSettingsKey(key);
    }

    boolean isSettingsKey(int key_idx, Key key) {
        if (key_idx < 14) return false;
        return isSettingsKey(key);
    }

    boolean isSettingsKey(Key key) {
        if (key == null) {
            return false;
        }
        // 半个按键
        if (key.x_i < screen_width && key.x_f >= screen_width) return true;

        if (screen_width == key.x_f) return true;
        // 一整个 按键
        int keyWidth = key.x_f - key.x_i;
        return screen_width > key.x_f && screen_width - key.x_f < keyWidth;
    }

    static class Key {
        int x_i, x_f, y_i, y_f;

        /**
         * @param x_i 左边界，x轴
         * @param x_f 右边界
         * @param y_i 上边界，y轴
         * @param y_f 下边界
         *            <p>
         *            Draw big_key_(0), Key{x_i=0, x_f=220, y_i=0, y_f=1013}
         *            Draw flat_key_(1), Key{x_i=154, x_f=286, y_i=0, y_f=557}
         */
        Key(int x_i, int x_f, int y_i, int y_f) {
            this.x_i = x_i;
            this.x_f = x_f;
            this.y_i = y_i;
            this.y_f = y_f;
        }

        boolean contains(float pos_x, float pos_y) {
            return (pos_x > x_i && pos_x < x_f) &&
                    (pos_y > y_i && pos_y < y_f);
        }

        @NonNull
        @Override
        public String toString() {
            return "Key{" +
                    "x_i=" + x_i +
                    ", x_f=" + x_f +
                    ", y_i=" + y_i +
                    ", y_f=" + y_f +
                    '}';
        }
    }

    public interface OnPianoKeyClickListener {
        void onSettingsKeyClicked();
    }

    private OnPianoKeyClickListener mKeyClickListener;

    public void setOnPianoKeyClickListener(OnPianoKeyClickListener clickListener) {
        this.mKeyClickListener = clickListener;
    }

    public void printInfo() {
        PianoSoundHelper.printInfo();
    }
}
