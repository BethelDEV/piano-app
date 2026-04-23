package com.piano;

import android.content.Context;
import android.preference.PreferenceManager;
import funs.common.tools.CLogger;

public class Preferences {

    private static final String TAG = "Preferences";
//    private static final String DEFAULT_SOUNDSET = "piano";
//    private final static String PREF_SELECTED_SOUND_SET = "selected_SoundSet";
//    private final static String PREF_SELECTED_MELODIES = "selected_Melodies";
    private final static String PREF_ENABLE_MELODIES = "enable_Melodies";

    private final static String PREF_SELECTED_SONG = "selected_song_id";
    private static final String DEFAULT_SONG_ID = "jsongs/tian_kong_zhi_cheng.json";

    private static final String PREF_MIN_NUMBER_OF_KEYS = "MIN_NUMBER_OF_KEYS_ONE_PAGE";
    public static final int DEFAULT_MIN_NUMBER_OF_KEYS = 12;//14;

    private static final String PREF_PIANO_KEYBOARD_STYLE = "PIANO_KEYBOARD_COLOR_STYLE";
    public static final int KEYBOARD_STYLE_COLORFUL = 0;
    public static final int KEYBOARD_STYLE_CLASSICAL = 1;

    public static boolean areMelodiesEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_ENABLE_MELODIES, true);
    }

    public static void setMelodiesEnabled(Context context, boolean b) {
//        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_ENABLE_MELODIES, true);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_ENABLE_MELODIES, b)
                .apply();
    }

    public static String getSelectedSongId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SELECTED_SONG, DEFAULT_SONG_ID);
    }

    public static void setSelectedSongId(Context context, String songId) {
//        CLogger.d(TAG, "Selecting songId \"" + songId + "\"");
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SELECTED_SONG, songId)
                .apply();
    }

    public static int getKeyboardStyle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_PIANO_KEYBOARD_STYLE, KEYBOARD_STYLE_COLORFUL);
//        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_PIANO_KEYBOARD_STYLE, KEYBOARD_STYLE_CLASSICAL);
    }

    public static void setKeyboardStyle(Context context, int colorStyle) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_PIANO_KEYBOARD_STYLE, colorStyle)
                .apply();
    }

    public static int getMinNumberOfKeys(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_MIN_NUMBER_OF_KEYS, DEFAULT_MIN_NUMBER_OF_KEYS);
    }

    public static void setMinNumberOfKeys(Context context, int num) {
        if (num < 1) num = 1;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_MIN_NUMBER_OF_KEYS, num)
                .apply();
    }

}
