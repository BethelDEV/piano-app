package funs.games;

import android.content.Context;

import java.io.IOException;

import funs.common.tools.CLogger;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games
 * @ClassName: PianoSoundHelper
 * @Description: {@link GameSoundCollection}
 */
public final class PianoSoundHelper {
    private static final String TAG = "PianoSoundHelper";

    private static GameSoundCollection soundCollection;

    public static void tryInitSoundPool(Context context) {
        CLogger.d(TAG, "tryInitSoundPool()");
        if (soundCollection == null) {
            initSoundPool(context);
        }
    }

    public static void initSoundPool(Context context) {
        releaseSoundPool();
        soundCollection = new GameSoundCollection();
        try {
            soundCollection.loadPianoSounds(context);
        } catch (IOException e) {
            CLogger.d(TAG, "initSoundPool, Failed to load sounds");
            e.printStackTrace();
        }
        CLogger.d(TAG, "initSoundPool()");
    }

    public static void playPianoSoundByKey(final int key_idx) {
        if (null != soundCollection) {
            soundCollection.playPianoSoundByKey(key_idx);
            CLogger.d(TAG, "playPianoSoundByKey ");
        }
    }

    public static void releaseSoundPool() {
        if (null != soundCollection) {
            soundCollection.release();
            soundCollection = null;
        }
        CLogger.d(TAG, "releaseSoundPool()");
    }

    public static void printInfo() {
        if (soundCollection != null) {
            soundCollection.printInfo();
        }
        CLogger.i(TAG, "printInfo , PianoConst.sBlackKeyIdx list : %s", PianoConst.sBlackKeyIdx);
        CLogger.i(TAG, "printInfo , PianoConst.note2keyIdx map : %s", PianoConst.note2keyIdx);
    }
}
