package funs.games;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;
import java.util.HashMap;

import funs.common.tools.CLogger;

public class GameSoundCollection {
    private static final String TAG = "GameSoundCollection";

    private static final int MAXSIZE = 10;
    private final SoundPool collection;
    private final HashMap<String, Integer> map;
//    private final float mLeftVolume = 0.3f;
//    private final float mRightVolume = 0.3f;

    public GameSoundCollection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA) // 设置音效使用场景
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(); // 设置音效的类型

            SoundPool.Builder spb = new SoundPool.Builder();
            spb.setMaxStreams(MAXSIZE);
            spb.setAudioAttributes(attr);    //转换音频格式
            collection = spb.build();      //创建SoundPool对象
        } else {
            collection = new SoundPool(MAXSIZE, AudioManager.STREAM_MUSIC, 0);
        }
        map = new HashMap<String, Integer>();
    }

    public void load(Context context, String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        int id = collection.load(assetManager.openFd(fileName), 1);
        map.put(fileName, id);
        CLogger.i(TAG, "load  (id : %d, file: %s)", id, fileName);
    }

    public void loadPianoSounds(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        for (int i = 27; i <= 88; i++) {
            String fileName = getPianoFileName(i);//String.format("piano_sounds/p%02d.mp3", i);
            loadPianoSoundFile(assetManager, fileName);
        }
        for (int i = 26; i >0 ; i--) {
            String fileName = getPianoFileName(i);
            loadPianoSoundFile(assetManager, fileName);
        }
    }

    private void loadPianoSoundFile(AssetManager assetManager, final String fileName) throws IOException {
        int id = collection.load(assetManager.openFd(fileName), 1);
        map.put(fileName, id);
        CLogger.i(TAG, "load  (id : %d, file: %s)", id, fileName);
    }

    private int getPianoFileIdByKey(int key_index) {
        if (key_index<0) return -1;

        if (key_index%2 == 0) { // WHITE_KEY
//            int pos = key_index/2 +1;
            return PianoConst.WHITE_KEY_FILE_ID[key_index/2];
        }

        // BLACK_KEY
        if (PianoConst.sBlackKeyIdx.contains(key_index)) {
            return PianoConst.BLACK_KEY_FILE_ID[PianoConst.sBlackKeyIdx.indexOf(key_index)];
        }

        return -1;
    }

    public void playPianoSoundByKey(final int key_index) {
        final int fileId = getPianoFileIdByKey(key_index);
        if (fileId<0) return;

        String fileName = getPianoFileName(fileId);
        play(fileName);
    }

    private static String getPianoFileName(int fileId) {
        return String.format("piano_sounds/p%02d.mp3", fileId);
    }

    public void play(String key) {
        Integer id = map.get(key);
        if (id == null) {
            CLogger.i(TAG, "play id is null");
            return;
        }
        collection.play(id, 1f, 1f, 0, 0, 1);
        CLogger.i(TAG, "play id : " + id);
    }

    public void printInfo() {
        CLogger.i(TAG, "printInfo , map : %s", map);
    }

    public void release() {
        if (collection != null) {
            collection.autoPause();
            collection.release();
        }
        map.clear();
        CLogger.d(TAG, "release()");
    }
}
