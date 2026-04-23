package funs.games;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.piano.melodies.Melody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import funs.common.tools.CLogger;
import funs.games.bean.SongFile;
import funs.games.bean.SongNote;
import funs.games.bean.SongSheet;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games
 * @ClassName: MelodyHelper
 * @Description: parse jsonSong{@link SongSheet} to {@link Melody}
 */
public class MelodyHelper {
    private static final String TAG = "MelodyHelper";
    public static final String LOCAL_SONG_PREF = "jsongs";
    public static final String LOCAL_SONG_PATH = LOCAL_SONG_PREF+"/";

    /*
    {
    "type": 1,
    "group": 4,  => C4
    "position": 0, => C4+0
    "time": 500
    }

    [A, B] - [C D E F G A B] - [C]
     */

    private static final String[] WHITE_PRE = {"C", "D", "E", "F", "G", "A", "B"};

    private static String convertSongNote2MelodyStr(SongNote note) {
        if (note == null) return "";
        final int group = note.getGroup();
        int pos = note.getPosition();
        // 首先，排除首尾两端的特殊情况
        if (group < 1) {
            if (pos % 2 == 0) {
                return "A0";
            }
            return "B0";
        }
        if (group > 7) return "C8";

        int len = WHITE_PRE.length;
        if (pos >= len) pos = pos % len;

        if (note.getType() != 1) {
            // todo test
            CLogger.i(TAG, "SongNote type: %d", note.getType());
        }

        return WHITE_PRE[pos] + group;
    }

    private static String convertSongSheet2MelodyStr(SongSheet song) {
        List<SongNote> pianosheet = null != song ? song.getPianoSheet() : null;
        if (null == pianosheet || pianosheet.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (SongNote note : pianosheet) {
            String str = convertSongNote2MelodyStr(note);
            builder.append(str).append(" ");
        }
        return builder.toString();
    }

    private static SongSheet convertJsonSong2SongSheet(String json) {
        if (TextUtils.isEmpty(json)) return null;

        return new Gson().fromJson(json, SongSheet.class);
    }

    public List<Melody> parseFromAssets(@NonNull Context context) {
        List<Melody> list = new LinkedList<>();

        try {
            String[] fileNames = context.getAssets().list(LOCAL_SONG_PREF);// 获取assets目录下的所有文件及有文件的目录名
            for (String file : fileNames) {
                if (null == file || !file.endsWith(".json")) continue;

                Melody melody = parseFromAssets(context, LOCAL_SONG_PATH + file);
                list.add(melody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Melody parseFromAssets(@NonNull Context context, @NonNull String fileName) {
        String songJson = readTextFromAssets(context, fileName);
        SongSheet sheet = convertJsonSong2SongSheet(songJson);
        String melodyText = convertSongSheet2MelodyStr(sheet);
        CLogger.i(TAG, "parseFromAssets %s , %s ", fileName, melodyText);
        return Melody.fromString(fileName, melodyText);
    }

    private String readTextFromAssets(@NonNull Context context, @NonNull String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder builder = new StringBuilder();
//            String Result="";
            while ((line = bufReader.readLine()) != null) {
//                Result += line;
                builder.append(line);
            }
            return builder.toString();
//            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int songNote2pos(SongNote note) {
        if (note == null) {
            return -1;
        }
        if (note.getType() == 1) {
            int group = note.getGroup();
            int position = note.getPosition();
            if (group < 1) return position * 2;
            if (group < 8) return 2 + ((group - 1) * WHITE_PRE.length + 1 + position) * 2;
            return 2 + (7 * WHITE_PRE.length + 1) * 2;
        }
        // todo test
        CLogger.i(TAG, "SongNote type: %d", note.getType());
        return -1;
    }

    public static SongFile getLocalSong(String id) {
        List<SongFile> list = PianoConst.localSongs;
        for (SongFile song: list) {
            if (TextUtils.equals(id, song.getId())) return song;
        }
        return null;
    }
}
