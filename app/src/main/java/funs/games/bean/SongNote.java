package funs.games.bean;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games.bean
 * @ClassName: SongNote
 * @Description: json - 音符
 *
 * {
 *       "type": 1,
 *       "group": 4,
 *       "position": 0,
 *       "time": 500
 *     }
 */
public class SongNote {
    private int type;
    private int group;
    private int position;
    private int time;

    public int getType() {
        return type;
    }

    public int getGroup() {
        return group;
    }

    public int getPosition() {
        return position;
    }

    public int getTime() {
        return time;
    }
}
