package funs.games;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import funs.common.tools.CLogger;
import funs.games.bean.SongFile;

public final class PianoConst {
    private static final String TAG = "PianoConst";

    public static final String ACTION_SELECTED_SONG_CHANGED = "broadcast_ACTION_SELECTED_SONG_CHANGED";
    public static final String ACTION_KEYBOARD_STYLE_CHANGED = "broadcast_ACTION_KEYBOARD_STYLE_CHANGED";
    public static final String ACTION_MELODY_GUIDE_MODE_CHANGED = "broadcast_ACTION_MELODY_GUIDE_CHANGED";

    // 音符
    public static final String[] RANGE = {"A0", "B0",
            "C1", "D1", "E1", "F1", "G1", "A1", "B1",
            "C2", "D2", "E2", "F2", "G2", "A2", "B2",
            "C3", "D3", "E3", "F3", "G3", "A3", "B3",
            "C4", "D4", "E4", "F4", "G4", "A4", "B4",
            "C5", "D5", "E5", "F5", "G5", "A5", "B5",
            "C6", "D6", "E6", "F6", "G6", "A6", "B6",
            "C7", "D7", "E7", "F7", "G7", "A7", "B7",
            "C8"};
    public static final String[] PRONUNCIATION = {"la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do"};
    public static final int[] BLACK_GAPS = {0,
            2, 3, 5, 6, 7,
            9, 10, 12, 13, 14,
            16, 17, 19, 20, 21,
            23, 24, 26, 27, 28,
            30, 31, 33, 34, 35,
            37, 38, 40, 41, 42,
            44, 45, 47, 48, 49};

    public static final int[] WHITEKEY_CODE = {21, 23,
            24, 26, 28, 29, 31, 33, 35,
            36, 38, 40, 41, 43, 45, 47,
            48, 50, 52, 53, 55, 57, 59,
            60, 62, 64, 65, 67, 69, 71,
            72, 74, 76, 77, 79, 81, 83,
            84, 86, 88, 89, 91, 93, 95,
            96, 98, 100, 101, 103, 105, 107,
            108,};

    public static final int[] WHITE_KEY_FILE_ID = {1, 3,
            4, 6, 8, 9, 11, 33, 15,
            16, 18, 20, 21, 23, 25, 27,
            28, 30, 32, 33, 35, 37, 39,
            40, 42, 44, 45, 47, 49, 51,
            52, 54, 56, 57, 59, 61, 63,
            64, 66, 68, 69, 71, 73, 75,
            76, 78, 80, 81, 83, 85, 87,
            88}; // p01.mp3

    public static final int[] BLACK_KEY_FILE_ID = {2,
            5, 7, 10, 12, 14,
            17, 19, 22, 24, 26,
            29, 31, 34, 36, 38,
            41, 43, 46, 48, 50,
            53, 55, 58, 60, 62,
            65, 67, 70, 72, 74,
            77, 79, 82, 84, 86
    }; // p05.mp3

    public static final int[] BLACKKEY_CODE = {22,
            25, 27, 30, 32, 34,
            37, 39, 42, 44, 46,
            49, 51, 54, 56, 58,
            61, 63, 66, 68, 70,
            73, 75, 78, 80, 82,
            85, 87, 90, 92, 94,
            97, 99, 102, 104, 106
    };

    public static final Map<String, Integer> note2keyIdx = new HashMap<>();
    public static final ArrayList<Integer> sBlackKeyIdx = new ArrayList<>();
    public static final ArrayList<SongFile> localSongs = new ArrayList<>();

    static {
        // BlackKeys index
        for (int key_idx = 1; key_idx < 105; key_idx += 2) {
            // 首先，排除首尾两端的特殊情况
            final int octave_idx = (key_idx / 2 - 2) % 7;
            if (key_idx == 2 || key_idx == 3 || key_idx >= 103 || (octave_idx == 2 || octave_idx == 6)) {
                continue; // 排除空位置
            }

            sBlackKeyIdx.add(key_idx);
        }

        // white key
        int white = RANGE.length;
        for (int i = 0; i < white; i++) {
            note2keyIdx.put(RANGE[i], 2 * i);
        }
        /* black key
        [A1,A#1,B1],
        [C2,C#2,D2,D#2,E2,F2,F#2,G2,G#2,A2,A#2,B2] ~ [C8,C#8,D8,D#8,E8,F8,F#8,G8,G#8,A8,A#8,B8],[C9]

        note_to_key_idx.put("C#1", 1);
        note_to_key_idx.put("Db1", 1);
        note_to_key_idx.put("D♭1", 1);

        note_to_key_idx.put("D#1", 3);
        note_to_key_idx.put("Eb1", 3);
        note_to_key_idx.put("E♭1", 3);

        note_to_key_idx.put("F#1", 7);
        note_to_key_idx.put("Gb1", 7);
        note_to_key_idx.put("G♭1", 7);

        note_to_key_idx.put("G#1", 9);
        note_to_key_idx.put("Ab1", 9);
        note_to_key_idx.put("A♭1", 9);

        note_to_key_idx.put("A#1", 11);
        note_to_key_idx.put("Bb1", 11);
        note_to_key_idx.put("B♭1", 11);
         */
        String[][] half = {{"C#", "Db", "D♭"}, {"D#", "Eb", "E♭"}, {"F#", "Gb", "G♭"}, {"G#", "Ab", "A♭"}, {"A#", "Bb", "B♭"}};
        int hLen = half.length;
        int size = sBlackKeyIdx.size();
//        int num = 2;
//        note2keyIdx.put("A#1", sBlackKeyIdx.get(0));
        int v0 = sBlackKeyIdx.get(0);
        String[] hs0 = half[(hLen - 1)];
        for (String h : hs0) {
            note2keyIdx.put(h + 1, v0);
        }
        for (int i = 1; i < size; i++) {
            int value = sBlackKeyIdx.get(i);
            String[] hs = half[(i - 1) % hLen];
            int num = (i - 1) / hLen + 2;
            for (String h : hs) {
                note2keyIdx.put(h + num, value);
                CLogger.i(TAG, "note2keyIdx, black key: %d , %s %d  %d ", i, h, num, value);
            }
        }

        // init localSongs
        if (!localSongs.isEmpty()) localSongs.clear();
        SongFile sf = new SongFile("Castle in the Sky", "jsongs/tian_kong_zhi_cheng.json");
        localSongs.add(sf);
        sf = new SongFile("Song of joy", "jsongs/huan_le_song.json");
        localSongs.add(sf);
//        sf = new SongFile("the_support_of_love", "jsongs/the_support_of_love.json");
//        localSongs.add(sf);
        sf = new SongFile("Happy Birthday", "jsongs/sheng_ri_ge.json");
        localSongs.add(sf);
        sf = new SongFile("Twinkle, Twinkle, Little Star", "jsongs/little_star.json");
        localSongs.add(sf);
        sf = new SongFile("Fireflies Fly", "jsongs/chong_er_fei.json");
        localSongs.add(sf);
        sf = new SongFile("Goodbye (Li ShuTong)", "jsongs/song_bie.json");
        localSongs.add(sf);
        sf = new SongFile("Goodbye (Zhang ZhenYue)", "jsongs/zai_jian.json");
        localSongs.add(sf);
//        sf = new SongFile("Song of Hakuryu", "jsongs/bai_long_ma.json");
//        localSongs.add(sf);
    }

    // 按键的颜色
    public static final int[] CLASSICAL_KEY_COLORS = new int[]{
            Color.rgb(255, 251, 240),  // WHITE
            Color.rgb(255, 251, 240)
    };
    // 按键按下时的颜色
    public static final int[] CLASSICAL_PRESSED_KEY_COLORS = new int[]{
            ColorUtils.blendARGB(CLASSICAL_KEY_COLORS[0], Color.rgb(205, 205, 205), 0.5f),    // WHITE
            ColorUtils.blendARGB(CLASSICAL_KEY_COLORS[1], Color.rgb(205, 205, 205), 0.5f)
    };

    // 按键的颜色
    public static final int[] COLORFUL_KEY_COLORS = new int[]{
            Color.rgb(210, 10, 10),     // Red
//            Color.rgb(255, 135, 0),   // Orange
            Color.rgb(245, 245, 50),   // Yellow
//            Color.rgb(245, 245, 0),   // Yellow
            Color.rgb(255, 251, 240),  // WHITE

            Color.rgb(80, 215, 40),   // Light Green
            Color.rgb(100, 80, 185),   // Purple
            Color.rgb(10, 155, 155),   // Dark Green

            Color.rgb(233, 53, 159),  // Pink

            Color.rgb(60, 120, 240),  // blue
//            Color.WHITE
    };

    // 按键按下时的颜色
    public static final int[] COLORFUL_PRESSED_KEY_COLORS = new int[]{
            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[0], Color.WHITE, 0.5f),    // Red
//            ColorUtils.blendARGB(KEY_COLORS[1], Color.WHITE, 0.6f),    // Orange
            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[1], Color.WHITE, 0.75f),   // Yellow
            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[2], Color.rgb(205, 205, 205), 0.5f),    // WHITE

            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[3], Color.WHITE, 0.6f),    // Light Green
            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[4], Color.WHITE, 0.5f),    // Purple
            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[5], Color.WHITE, 0.5f),    // Dark Green

            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[6], Color.WHITE, 0.5f),    // Pink

            ColorUtils.blendARGB(COLORFUL_KEY_COLORS[7], Color.WHITE, 0.5f),    // blue
    };

}
