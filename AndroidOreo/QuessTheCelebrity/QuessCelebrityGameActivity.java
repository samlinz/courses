
package com.samlinz.androidcourse.quesstheceleb;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samlinz.androidcourse.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity.
 */
public class QuessCelebrityGameActivity extends AppCompatActivity implements NotifiableUserInterface {
    private static final String LogName = QuessCelebrityGameActivity.class.getSimpleName();
    private static String BASE_URL = "http://www.posh24.se/kandisar";

    private boolean isInitialized = false;

    private HandlerThread handlerThread;
    private Handler handler;

    private List<Celebrity> celebrities;

    @BindView(R.id.celebImageView)
    ImageView celebImage;

    @BindView(R.id.buttonContainer)
    LinearLayout buttonList;

    @BindView(R.id.scoreCount)
    TextView scoreCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (handlerThread == null) {
            handlerThread = new HandlerThread("ActivityTaskHandler");
            handlerThread.start();
        }

        if (handler == null) {
            handler = new Handler(handlerThread.getLooper());
        }

        if (!isInitialized) {
            handler.post(this::init);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (handlerThread != null) {
            HandlerThread moribund = handlerThread;
            handlerThread = null;
            moribund.quit();
            moribund.interrupt();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * Initialize the application in background.
     */
    public void init() {
        if (isInitialized) return;
        isInitialized = true;

        Log.i(LogName, "Initializing game.");

        // Fetch list of top-100 celebs.
        List<Celebrity> celebrities = CelebrityFactory.GetCelebritites(100, BASE_URL);

        GameLogic gameLogic = GameLogic.getGame(celebrities);
        gameLogic.bindUI(this);
    }

    /**
     * Receive a new state and rebuild the UI to reflect this state.
     *
     * @param stateContainer StateContainer
     */
    public void pushState(GameStateContainer stateContainer) {
        Log.d(LogName, "Updating UI.");

        if (!(stateContainer instanceof CelebrityGameState)) {
            Log.e(LogName, "Invalid game state for celebrity game.");
            return;
        }

        CelebrityGameState gameState = (CelebrityGameState) stateContainer;

        scoreCount.setText(String.format(Locale.US, "Correct: %d / %d"
                , gameState.correct, gameState.total));

        buttonList.removeAllViews();

        if (gameState.correctChoice != null) {
            Toast.makeText(this, String.format("Wrong! Correct choice was %s"
                    , gameState.correctChoice.toUpperCase()), Toast.LENGTH_SHORT)
                    .show();
        }

        // Create and bind buttons for each choice of celeb.
        for (String choice :
                gameState.choices) {
            final Button button = new Button(this);
            button.setText(choice.toUpperCase());
            button.setOnClickListener(view -> gameState.logic.answer(choice));
            buttonList.addView(button);
        }

        // Update image.
        runOnUiThread(() -> celebImage.setImageBitmap(gameState.bitmap));
    }
}