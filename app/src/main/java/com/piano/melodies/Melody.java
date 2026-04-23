package com.piano.melodies;

public class Melody {

    public static final Melody apis_florea = fromString("apis_florea",
            "C C D D    E E    F F   G G " +
                    "G F E D C    C G G    G F E D"); // 小蜜蜂
    public static final Melody canon = fromString("",
            "C G A E " +
            "F C D Bb " +
            "Eb Bb C G " +
            "Ab Eb F D"); // 卡农

    public static final Melody childhood = fromString("childhood","C F G F " +
            "C F G F " +
            "C E D C " +
            "D G C C"); // 童年

    public static final Melody theMoonRepresentsMyHeart = fromString(
            "theMoonRepresentsMyHeart",
            "C D E D C E G " +
                    "G A B A G E C " +
                    "C D E D C E G " +
                    "G A B A G E C " +
                    "C E G E C D E C"
    );// 月亮代表我的心

    public static final Melody twinkle_twinkle_little_star = fromString(
            "twinkle_twinkle_little_star",
            "C C G G A A G " +
                    // Twinkle, twinkle, little star

                    "F F E E D D C " +
                    // How I wonder what you are!

                    "G G F F E E D " +
                    // Up above the world so high,

                    "G G F F E E D " +
                    // Like a diamond in the sky...

                    "C C G G A A G " +
                    // Twinkle, twinkle, little star

                    "F F E E D D C"
            // How I wonder what you are!
    );

    public static final Melody a_mo_li_hua = fromString("a_mo_li_hua",
            "3 3 5 6 1 1 6 5 5 6 5 " +
                    "3 3 5 6 1 1 6 5 5 6 5 " +
            "5 5 5 3 5 6 6 5 " +
            "3 2 3 5 3 2 1 1 2 1 " +
            "3 2 1 3 2 3 5 6 1 5 " +
            "2 3 5 2 3 1 6 5 " +
                    "6 1 2 3 1 2 1 6 5"); // 茉莉花 http://www.jianpu.cn/pu/36/36672.htm

    public static final Melody[] all = new Melody[]{
            twinkle_twinkle_little_star,
            a_mo_li_hua,
//            huge_fish,
//            canon,
//            insy_winsy_spider,
//            im_a_little_teapot,
//            waltzing_matilda
    };

    /**
     * A somewhat-robust string to melody parser.
     * Allows melodies to be specified as a string of notes, where the notes are: "A", "B1", "C#1", "Bb1", "G2", etc.
     * <p>
     * Notes are separated by whitespace.
     * <p>
     * Notes in the first octave can leave off the octave designation and it will be automatically
     * appended (i.e. "C" will become "C1"). This makes it simpler to write songs that fall within a single octave.
     */
    public static Melody fromString(String id, String plainTextNotes) {
        String[] notes = plainTextNotes.trim().split("\\s+");

        for (int i = 0; i < notes.length; i++) {
            if (!notes[i].matches(".*\\d$")) {
                notes[i] = notes[i] + "5";
//                notes[i] = notes[i] + "1";
            }
        }
        return new Melody(id, notes);
    }

    private final String id;
    private final String[] notes;

    public Melody(String id, String[] notes) {
        this.id = id;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String[] getNotes() {
        return notes;
    }
}
