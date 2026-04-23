package com.piano.melodies;

public interface MelodyPlayer {
    String nextNote();
    boolean hasNextNote();
    void reset();
}
