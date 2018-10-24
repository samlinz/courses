package com.testiappi.kurssiapplikaatio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 'Menu' Activity for the Brain trainer game.
 * Used only for launching the main Activity and root in case of error.
 */
public class BrainRootActivity extends AppCompatActivity {
    private static final String LogName = BrainRootActivity.class.getSimpleName();

    @BindView(R.id.startButton)
    Button startButton;

    @OnClick(R.id.startButton)
    public void onStartButton() {
        Log.d(LogName, "Start button clicked.");

        startNewGame();
    }

    /**
     * Start the game by launching the game activity.
     */
    private void startNewGame() {
        Log.i(LogName, "Starting new game");

        Intent startNewGameIntent = new Intent(this, BrainGameActivity.class);
        startActivity(startNewGameIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brain_root_layout);
        ButterKnife.bind(this);
    }
}
