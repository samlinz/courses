/**
* Three-in-a-row game for Android.
* For Udemy Course https://www.udemy.com/the-complete-android-oreo-developer-course/learn/v4/overview
*/

package com.courseapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Outcome of the game.
 */
enum OUTCOME {
    NOTHING,
    RED_WINS,
    YELLOW_WINS,
    STALEMATE
}

/**
 * Runnable function with an argument.
 * 
 * @param <T> Type of the argument.
 */
abstract class RunnableWithArgument<T> implements Runnable {
    private T obj;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}

/**
 * Players.
 */
enum COLOR {
    UNKOWN(0),
    YELLOW(1),
    RED(2);

    private final int id;

    COLOR(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

/**
 * State container which can be notified about a switch in state.
 */
interface SwitcableState {
    void switchState();
}

/**
 * State container which can be updated.
 */
interface NotifiableState {
    void notifyClick(int y, int x);
}

/**
 * A pawn.
 */
class Pawn {
    private COLOR color;
    private ImageView view;
    private final int row;
    private final int col;
    private final int ANIMATION_LENGTH = 500;
    private final int ANIMATION_DISTANCE = 2000;
    private AtomicBoolean animationRunning = new AtomicBoolean(false);
    private AtomicBoolean hasRunAction = new AtomicBoolean(false);
    private AtomicBoolean clickEnabled = new AtomicBoolean(true);
    private NotifiableState notifier;
    private SwitcableState checker;

    Pawn(NotifiableState notifier
            , SwitcableState checkableState
            , ImageView view
            , int row
            , int col) {
        this.row = row;
        this.col = col;
        this.view = view;
        this.notifier = notifier;
        this.checker = checkableState;

        initView();
    }

    /**
     * Set pawn color.
     * 
     * @param color {@link COLOR} enumeration which represents pawn color.
     */
    public void setColor(COLOR color) {
        int drawableResource;
        switch (color) {
            case YELLOW:
                drawableResource = R.drawable.yellow;
                break;
            case RED:
                drawableResource = R.drawable.red;
                break;
            default:
                throw new RuntimeException("Invalid COLOR " + color);
        }
        this.view.setImageResource(drawableResource);
    }

    /**
     * Get pawn color.
     * 
     * @return {@link COLOR} enumeration which represents the color.
     */
    public COLOR getColor() {
        return color;
    }

    /**
     * Get the view object which represents the pawn, for example for attaching to layout.
     * 
     * @return {@link ImageView} object which represents the Pawn's view object.
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Enable or disable click listener.
     * 
     * @param clickEnabled If true the vacant position can be clicked.
     */
    void setClickEnabled(boolean clickEnabled) {
        this.clickEnabled.set(clickEnabled);
    }

    /**
     * Initialize the view object.
     */
    void initView() {
        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAction();
            }
        });
    
        // Initially the pawn is 'vacant' or not in screen, but it can be clicked.
        this.view.setAlpha(0f);
    }

    /**
     * Handle click.
     */
    private void onClickAction() {
        if (animationRunning.get()) return;
        if (hasRunAction.get()) return;
        if (!clickEnabled.get()) return;
        
        initAnimation();
        playAnimation();
        
        // Notify the state that this vacant spot was clicked.
        notifier.notifyClick(this.row, this.col);
    }


    /**
     * Initialize the pawn animation.
     */
    private void initAnimation() {
        this.view.setTranslationY(-ANIMATION_DISTANCE);
    }

    /**
     * Handle animation end hook.
     */
    private void onAnimationEnd() {
        animationRunning.set(false);
        checker.switchState();
    }

    /**
     * Play the pawn entering screen animation.
     */
    private void playAnimation() {
        animationRunning.set(true);
        hasRunAction.set(true);
        this.view
            .animate()
            .translationYBy(ANIMATION_DISTANCE)
            .alpha(1f)
            .setDuration(ANIMATION_LENGTH)
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    onAnimationEnd();
                }
            });
    }
}

/**
 * Game state container, handles the board and the state transitions of the game.
 * Among other thigs.
 */
class GameState implements SwitcableState, NotifiableState {
    // Game constants.
    private final int ROWS = 3;
    private final int COLUMNS = 3;
    private final int WINNER_LENGTH = 3;

    private COLOR whoseTurn;

    private WeakReference<ViewGroup> gameGrid;
    private WeakReference<TextView> statusText;
    private String status;

    private Pawn[][] pawns = new Pawn[ROWS][COLUMNS];
    private COLOR[][] colors = new COLOR[ROWS][COLUMNS];

    private GameState() { }

    /**
     * Recursive algorithm for checking how long row of a single color there exists on the board.
     * Scales to any board size.
     * 
     * Recursion ends when the edge of the board is met or the color of pawn changes.
     * 
     * @param grid {@link COLOR} grid, represents the game state.
     * @param colorToCheck Which {@link COLOR} to check.
     * @param currentX Which X coordinate is under inspection currently.
     * @param currentY Which Y coordinate is under inspection currently.
     * @param moveX How many steps will the check progress horizontally.
     * @param moveY How many steps will the check progress vertically.
     * @param inRow How many pawns of the same color has been sequentially in row so far.
     * @return The total number of sequential pawns of same color in row from the start position.
     */
    private int colorLengthRecursive(COLOR[][] grid
            , final COLOR colorToCheck
            , final int currentX
            , final int currentY
            , final int moveX
            , final int moveY
            , final int inRow) {
        int nextX = currentX + moveX;
        int nextY = currentY + moveY;

        // Check if query out-of-bounds.
        if (nextX < 0 || nextX >= grid[0].length || nextY < 0 || nextY >= grid.length)
            return inRow;

        COLOR nextColor = grid[nextY][nextX];
        // If sequence continues, advance the recursion.
        if (nextColor == colorToCheck)
            return colorLengthRecursive(grid
                    , colorToCheck
                    , nextX
                    , nextY
                    , moveX
                    , moveY
                    , inRow + 1);

        // End condition is met, return recursion.
        return inRow;
    }

    /**
     * Check the state of the game.
     * 
     * @return {@link OUTCOME} representing the state of the game, if game is over or shall continue.
     */
    private OUTCOME checkState() {
        COLOR winner = COLOR.UNKOWN;
        final AtomicInteger vacancies = new AtomicInteger(0);

        COLOR[] colorsToCheck = { COLOR.RED, COLOR.YELLOW };
        for (COLOR color : colorsToCheck) {
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLUMNS; x++) {
                    // Calculate vacant spots.
                    if (color == colorsToCheck[0] && colors[y][x] == COLOR.UNKOWN)
                        vacancies.incrementAndGet();

                    int inRow =
                            colorLengthRecursive(colors, color, x, y, 1, 0, 1);
                    int inColumn =
                            colorLengthRecursive(colors, color, x, y, 0, 1, 1);
                    int inDiagonal =
                            colorLengthRecursive(colors, color, x, y, 1, 1, 1);
                    if (inRow >= WINNER_LENGTH
                            || inColumn >= WINNER_LENGTH
                            || inDiagonal >= WINNER_LENGTH) {
                        winner = color;
                        break;
                    }
                }
                if (winner != COLOR.UNKOWN)
                    break;
            }
            if (winner != COLOR.UNKOWN)
                break;
        }

        switch (winner) {
            case UNKOWN:
                if (vacancies.get() == 0)
                    return OUTCOME.STALEMATE;
                break;
            case YELLOW:
                return OUTCOME.YELLOW_WINS;
            case RED:
                return OUTCOME.RED_WINS;
        }

        return OUTCOME.NOTHING;
    }

    /**
     * Switch the player in turn.
     */
    @Override
    public void switchState() {
        OUTCOME outcome = checkState();

        whoseTurn = whoseTurn == COLOR.RED ? COLOR.YELLOW : COLOR.RED;

        boolean gameContinues = true;
        switch (outcome) {
            case NOTHING:
                status = whoseTurn.toString() + " turn";
                break;
            case RED_WINS:
                status = "RED WINS";
                gameContinues = false;
                break;
            case YELLOW_WINS:
                status = "YELLOW WINS";
                gameContinues = false;
                break;
            case STALEMATE:
                status = "STALEMATE";
                gameContinues = false;
                break;
        }

        showStatus();

        if (!gameContinues) {
            forEachPawn(new RunnableWithArgument<Pawn>() {
                @Override
                public void run() {
                    // Disable clicks
                    this.getObj().setClickEnabled(false);
                }
            });
        }
    }

    /**
     * Display status text.
     */
    private void showStatus() {
        statusText.get().setText(status);
    }

    /**
     * Initialize game.
     */
    private void initState() {
        whoseTurn = COLOR.RED;
        status = whoseTurn.toString() + " turn";
        showStatus();
    }

    /**
     * Run some action for each pawn on board.
     * 
     * @param runnable {@link RunnableWithArgument} to run against each pawn.
     */
    private void forEachPawn(RunnableWithArgument<Pawn> runnable) {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                runnable.setObj(pawns[y][x]);
                runnable.run();
            }
        }
    }

    /**
     * Reset game state to initial state.
     */
    public void resetState() {
        forEachPawn(new RunnableWithArgument<Pawn>() {
            @Override
            public void run() {
                Pawn pawn = this.getObj();
                pawn.initView();
            }
        });
        whoseTurn = COLOR.RED;
    }

    /**
     * Listen to a click on any vacant spot on the game board.
     * 
     * @param y Y coordinate of the click.
     * @param x X coordinate of the click.
     */
    @Override
    public void notifyClick(int y, int x) {
        COLOR pointColor = colors[y][x];
        if (pointColor != COLOR.UNKOWN) {
            Log.i("move", "Invalid move y" + y + " x" + x);
            return;
        }

        pawns[y][x].setColor(whoseTurn);
        colors[y][x] = whoseTurn;
    }

    /**
     * Factory method for constructing a fresh game state.
     * 
     * @param activity Parent activity which' context will be used.
     * @return A new {@link GameState} with initial configuration.
     */
    static GameState getGameState(Activity activity) {
        GameState newGameState = new GameState();
        newGameState.gameGrid =
                new WeakReference<>((ViewGroup) activity.findViewById(R.id.gameGrid));
        newGameState.statusText =
                new WeakReference<>((TextView) activity.findViewById(R.id.statusText));

        // Fetch pawns
        int i = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                ImageView view = (ImageView) newGameState.gameGrid.get().getChildAt(i);
                Pawn pawnObject =
                        new Pawn(newGameState, newGameState, view, y, x);
                newGameState.pawns[y][x] = pawnObject;
                newGameState.colors[y][x] = COLOR.UNKOWN;
                i++;
            }
        }

        newGameState.initState();

        return newGameState;
    }
}

/**
 * Main activity.
 */
public class MainActivity extends AppCompatActivity {
    private GameState gameState;

    public void onResetButton(View view) {
        gameState.resetState();
        createNewGame();
    }

    void createNewGame() {
        gameState = GameState.getGameState(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sibale);

        // Create new state.
        createNewGame();
    }
}
