package fi.samlinz.androidcourse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Main game activity.
 *
 * When the activity is created, a new instance of the game represented by
 * {@link GameHandler} will be created. Handler emits updates to game state and the
 * UI will respond accordingly. The user input for reset and chosen answer will be
 * directly invoked on the game handler.
 *
 * Game is reseted by recreating the Activity.
 */
public class BrainGameActivity extends AppCompatActivity {
    private static final String LogName = BrainGameActivity.class.getSimpleName();

    // Time limit for a single game.
    private static final int GAME_TIME = 20 * 1000;
    // Game handler which encloses all game related logic and timer.
    private GameHandler game;
    // Gson instance for stringifying objects.
    private Gson jsonConverter = new GsonBuilder().create();
    // Current question as a string, used to recognize a new question.
    private AtomicReference<String> currentQuestion = new AtomicReference<>(null);

    // Disposables whose references will be disposed along with the Activity.
    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder butterKnifeUnBinder;

    // UI references.
    private List<Button> answerButtons = new ArrayList<>();

    @BindView(R.id.resetButton)
    Button resetButton;

    @BindView(R.id.questionsCorrectTotal)
    TextView scoreText;

    @BindView(R.id.answerStatusText)
    TextView statusText;

    @BindView(R.id.timeLeft)
    TextView timeLeftText;

    @BindView(R.id.currentQuestion)
    TextView questionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LogName, "Activity is being created");

        setContentView(R.layout.brain_game_layout);
        butterKnifeUnBinder = ButterKnife.bind(this);

        // Get references for all the answer buttons.
        ViewGroup rootTable = findViewById(R.id.answersTable);
        final int rows = rootTable.getChildCount();
        for (int i = 0; i < rows; i++) {
            View child = rootTable.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow rowElement = (TableRow) child;
                final int rowChildren = rowElement.getChildCount();
                for (int j = 0; j < rowChildren; j++) {
                    View rowChild = rowElement.getChildAt(j);
                    if (rowChild instanceof Button) {
                        answerButtons.add((Button) rowChild);
                    }
                }
            }
        }

        // Build a new game logic instance.
        game = GameHandler.buildGame(GAME_TIME, answerButtons.size());
        // Register observer for game updates.
        registerObservers(game);
        // Start the game.
        game.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LogName, "Activity is being destroyed");

        // Dispose observer and UI bindings.
        disposable.clear();
        butterKnifeUnBinder.unbind();
    }

    @OnClick(R.id.resetButton)
    public void onResetButton() {
        Log.i(LogName, "Reset button clicked");

        // Recreate the Activity, which also creates a new instance of game logic.
        recreate();
    }

    /**
     * Register RxJava observers so that the UI can react to changes in game state.
     *
     * @param game Instance of game logic to observe.
     */
    private void registerObservers(GameHandler game) {
        Log.d(LogName, "Registering observers");

        Observable<GameState> observable = game.getGameStateObservable();
        Disposable observer = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNewState, this::onError);

        disposable.add(observer);
    }

    /**
     * Dispatch the chosen answer option to game logic.
     *
     * @param answer Answer as a number.
     */
    private void sendAnswer(int answer) {
        Log.i(LogName, "Sending answer " + answer);

        game.answer(answer);
    }

    /**
     * Shuffle the answer button colors randomly on new question.
     */
    private void shuffleColors() {
        Random rng = new Random();
        for (Button answerButton :
                answerButtons) {
            int r = rng.nextInt(256);
            int g = rng.nextInt(256);
            int b = rng.nextInt(256);
            int newColor = Color.argb(255, r, g, b);
            answerButton.setBackgroundColor(newColor);
        }
    }

    /**
     * React to an uncaught exception inside the game logic or UI callback.
     * Show a toast, log it and destroy the Activity returning to main menu.
     *
     * @param exception Thrown exception.
     */
    private void onError(Throwable exception) {
        Log.e(LogName, "Received exception in reactive sequence", exception);

        Toast.makeText(this,
                "GAME CRASHED: " + exception.getMessage(),
                Toast.LENGTH_LONG).show();

        finish();
    }

    /**
     * React to a new game state.
     *
     * @param newState New {@link GameState} sent from game logic handler.
     */
    private void onNewState(GameState newState) {
        String stateString = jsonConverter.toJson(newState);
        Log.d(LogName, "Got new state " + stateString);

        // Game ended by countdown running out.
        if (newState.gameEnded) {
            statusText.setText(R.string.gameoverText);
            resetButton.setVisibility(View.VISIBLE);
            return;
        }

        // Game is not running for some other reason, don't update UI.
        if (!newState.gameRunning) {
            return;
        }

        final int optionsCount = newState.options.size();
        final int answersCount = answerButtons.size();
        if (optionsCount != answersCount) {
            Log.w(LogName, String.format(
                    "Options size %d does not match button count %d", optionsCount, answersCount));
        }

        // Update UI.
        questionText.setText(newState.question);
        scoreText.setText(String.format(Locale.US,
                "%d / %d", newState.correctAnswers, newState.totalAnswers));
        timeLeftText.setText(String.format(Locale.US, "%ds", newState.timeLeft));

        if (newState.previousWrong && !newState.previousCorrect) {
            statusText.setText(R.string.wrongText);
        } else if (!newState.previousWrong && newState.previousCorrect) {
            statusText.setText(R.string.correctText);
        } else {
            statusText.setText("");
        }

        final String currentQuestion = this.currentQuestion.get();
        this.currentQuestion.set(newState.question);

        // No new question, no need to update buttons.
        if (newState.question.equals(currentQuestion)) return;

        // Bind answers to buttons.
        final int optionsBound = Math.min(optionsCount, answersCount);
        for (int i = 0; i < optionsBound; i++) {
            Button answerButton = answerButtons.get(i);
            Integer option = newState.options.get(i);
            answerButton.setText(String.valueOf(option));
            answerButton.setOnClickListener(null);
            answerButton.setOnClickListener(obj -> sendAnswer(option));
        }

        // Give answer buttons new colors.
        shuffleColors();
    }
}
