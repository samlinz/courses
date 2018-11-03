package com.samlinz.androidcourse.quesstheceleb;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Current state of the celeb game.
 */
public class CelebrityGameState implements GameStateContainer {
    public Bitmap bitmap;
    public List<String> choices;
    public int correct;
    public int total;
    public String correctChoice;
    public GameLogic logic;
}
